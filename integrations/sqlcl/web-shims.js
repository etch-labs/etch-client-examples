// Web-platform shims for the ETCH JavaScript client inside SQLcl.
//
// SQLcl's JavaScript (GraalJS) has neither Node's nor a browser's platform: no
// fetch, FormData, Blob, URL, TextEncoder or AbortController. The ETCH client is
// written for Node and browsers, so these shims supply the parts of each that
// the client uses, built on Java classes. Everything is synchronous underneath:
// fetch blocks on java.net.http and returns an already-settled promise.
(function (g) {
  var JByteArrayOutputStream = Java.type("java.io.ByteArrayOutputStream");
  var JString = Java.type("java.lang.String");
  var JHttpClient = Java.type("java.net.http.HttpClient");
  var JHttpRequest = Java.type("java.net.http.HttpRequest");
  var JBodyPublishers = Java.type("java.net.http.HttpRequest$BodyPublishers");
  var JBodyHandlers = Java.type("java.net.http.HttpResponse$BodyHandlers");
  var JURI = Java.type("java.net.URI");
  var JDuration = Java.type("java.time.Duration");
  var JStandardCharsets = Java.type("java.nio.charset.StandardCharsets");
  var JByteArray = Java.type("byte[]");

  // Java byte arrays and Uint8Arrays, converted both ways.
  function toJavaBytes(u8) {
    var out = new JByteArray(u8.length);
    for (var i = 0; i < u8.length; i++) out[i] = (u8[i] << 24) >> 24;
    return out;
  }
  function fromJavaBytes(jb) {
    var n = jb.length, u8 = new Uint8Array(n);
    for (var i = 0; i < n; i++) u8[i] = jb[i] & 0xff;
    return u8;
  }
  g.__etchBytes = { toJava: toJavaBytes, fromJava: fromJavaBytes };

  // TextEncoder and TextDecoder, UTF-8 only.
  if (typeof g.TextEncoder === "undefined") {
    g.TextEncoder = function () {};
    g.TextEncoder.prototype.encode = function (s) {
      return fromJavaBytes(new JString(String(s)).getBytes(JStandardCharsets.UTF_8));
    };
  }
  if (typeof g.TextDecoder === "undefined") {
    g.TextDecoder = function () {};
    g.TextDecoder.prototype.decode = function (u8) {
      u8 = u8 || new Uint8Array(0);
      if (!(u8 instanceof Uint8Array)) u8 = new Uint8Array(u8);
      return String(new JString(toJavaBytes(u8), JStandardCharsets.UTF_8));
    };
  }

  // setTimeout and clearTimeout. There is no event loop between SQLcl commands. A
  // zero delay runs the callback as a promise job; a real delay is recorded and
  // never fires. fetch is synchronous, so the client's request timeouts are never
  // needed, and etch-core.js turns retries (which wait with setTimeout) off.
  var timers = {}, nextTimer = 1;
  g.setTimeout = function (fn, ms) {
    var id = nextTimer++;
    timers[id] = true;
    if (!ms) Promise.resolve().then(function () { if (timers[id]) { delete timers[id]; fn(); } });
    return id;
  };
  g.clearTimeout = function (id) { delete timers[id]; };

  // AbortController. abort() marks the signal as aborted and calls its listeners.
  if (typeof g.AbortController === "undefined") {
    g.AbortController = function () {
      var listeners = [];
      this.signal = { aborted: false, addEventListener: function (t, f) { listeners.push(f); } };
      var sig = this.signal;
      this.abort = function () { sig.aborted = true; listeners.forEach(function (f) { f(); }); };
    };
  }

  // URL, with only the parts of it that the client uses.
  if (typeof g.URL === "undefined") {
    g.URL = function (u, base) {
      var uri = base ? JURI.create(String(base)).resolve(String(u)) : JURI.create(String(u));
      if (!uri.isAbsolute()) throw new TypeError("Invalid URL: " + u);
      this.protocol = String(uri.getScheme()) + ":";
      var params = [];
      this.searchParams = {
        append: function (k, v) { params.push(encodeURIComponent(k) + "=" + encodeURIComponent(v)); }
      };
      this.toString = function () {
        var s = String(uri.toString());
        return params.length ? s + (s.indexOf("?") < 0 ? "?" : "&") + params.join("&") : s;
      };
    };
  }
  if (typeof g.URLSearchParams === "undefined") {
    g.URLSearchParams = function () {
      var e = [];
      this.append = function (k, v) { e.push([k, v]); };
      this.forEach = function (f) { e.forEach(function (p) { f(p[1], p[0]); }); };
      this.toString = function () { return e.map(function (p) { return encodeURIComponent(p[0]) + "=" + encodeURIComponent(p[1]); }).join("&"); };
    };
  }

  // Headers, Blob and FormData replace any the engine already has, because fetch
  // below recognises only these three.
  g.Headers = function (init) {
    var m = {};
    this._m = m;
    if (init) Object.keys(init).forEach(function (k) { m[k.toLowerCase()] = String(init[k]); });
  };
  g.Headers.prototype.get = function (k) { var v = this._m[k.toLowerCase()]; return v === undefined ? null : v; };
  g.Headers.prototype.set = function (k, v) { this._m[k.toLowerCase()] = String(v); };
  g.Headers.prototype.has = function (k) { return k.toLowerCase() in this._m; };
  g.Headers.prototype.delete = function (k) { delete this._m[k.toLowerCase()]; };
  g.Headers.prototype.forEach = function (f) { var m = this._m; Object.keys(m).forEach(function (k) { f(m[k], k); }); };

  g.Blob = function (parts, opts) {
    var chunks = [], size = 0;
    (parts || []).forEach(function (p) {
      var u8;
      if (p instanceof g.Blob) u8 = p._bytes;
      else if (p instanceof ArrayBuffer) u8 = new Uint8Array(p);
      else if (ArrayBuffer.isView(p)) u8 = new Uint8Array(p.buffer, p.byteOffset, p.byteLength);
      else u8 = new g.TextEncoder().encode(String(p));
      chunks.push(u8); size += u8.length;
    });
    var all = new Uint8Array(size), off = 0;
    chunks.forEach(function (c) { all.set(c, off); off += c.length; });
    this._bytes = all;
    this.size = size;
    this.type = (opts && opts.type) || "";
  };
  g.Blob.prototype.arrayBuffer = function () { return Promise.resolve(this._bytes.slice().buffer); };
  g.Blob.prototype.text = function () { return Promise.resolve(new g.TextDecoder().decode(this._bytes)); };

  g.FormData = function () { this._parts = []; };
  g.FormData.prototype.append = function (name, value, filename) {
    this._parts.push({ name: name, value: value, filename: filename });
  };

  function encodeMultipart(fd) {
    var boundary = "----etchsqlcl" + java.util.UUID.randomUUID().toString().replace(/-/g, "");
    var out = new JByteArrayOutputStream();
    function w(s) { out.writeBytes(new JString(s).getBytes(JStandardCharsets.UTF_8)); }
    fd._parts.forEach(function (p) {
      w("--" + boundary + "\r\n");
      if (p.value instanceof g.Blob) {
        w('Content-Disposition: form-data; name="' + p.name + '"; filename="' + (p.filename || "blob") + '"\r\n');
        w("Content-Type: " + (p.value.type || "application/octet-stream") + "\r\n\r\n");
        out.writeBytes(toJavaBytes(p.value._bytes));
      } else {
        w('Content-Disposition: form-data; name="' + p.name + '"\r\n\r\n');
        w(String(p.value));
      }
      w("\r\n");
    });
    w("--" + boundary + "--\r\n");
    return { contentType: "multipart/form-data; boundary=" + boundary, bytes: out.toByteArray() };
  }

  // fetch, built on java.net.http. It blocks until the response arrives. The
  // client's own timeout uses setTimeout, which never fires here, so the connect
  // and request timeouts below are the only limits.
  var http = null;
  g.fetch = function (url, init) {
    try {
      init = init || {};
      if (http === null) http = JHttpClient.newBuilder().connectTimeout(JDuration.ofSeconds(20)).build();
      var b = JHttpRequest.newBuilder(JURI.create(String(url))).timeout(JDuration.ofSeconds(120));
      var headers = init.headers instanceof g.Headers ? init.headers : new g.Headers(init.headers || {});
      var body = init.body, publisher;
      if (body instanceof g.FormData) {
        var mp = encodeMultipart(body);
        headers.set("Content-Type", mp.contentType);
        publisher = JBodyPublishers.ofByteArray(mp.bytes);
      } else if (body === undefined || body === null) {
        publisher = JBodyPublishers.noBody();
      } else {
        publisher = JBodyPublishers.ofString(String(body));
      }
      // java.net.http sets Host, Content-Length and Connection itself and refuses
      // them, and setHeader replaces its own User-Agent rather than adding a second.
      headers.forEach(function (v, k) {
        if (k === "user-agent" || k === "host" || k === "content-length" || k === "connection") {
          if (k === "user-agent") b.setHeader("User-Agent", v);
          return;
        }
        b.header(k, v);
      });
      b.method(String(init.method || "GET"), publisher);
      var resp = http.send(b.build(), JBodyHandlers.ofByteArray());
      var rh = new g.Headers();
      resp.headers().map().forEach(function (k, vals) { rh.set(String(k), String(vals.get(0))); });
      var bytes = resp.body();
      var status = resp.statusCode();
      return Promise.resolve({
        ok: status >= 200 && status < 300,
        status: status,
        statusText: String(status),
        headers: rh,
        arrayBuffer: function () { return Promise.resolve(fromJavaBytes(bytes).buffer); },
        text: function () { return Promise.resolve(String(new JString(bytes, JStandardCharsets.UTF_8))); },
        blob: function () { return Promise.resolve(new g.Blob([fromJavaBytes(bytes)])); }
      });
    } catch (e) {
      return Promise.reject(new TypeError("fetch failed: " + e));
    }
  };
})(globalThis);
