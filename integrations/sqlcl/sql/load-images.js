// Loads the product photos into PRODUCTS.IMAGE, after sql/setup.sql.
//
//   SQL> script sql/load-images.js                 (photos from integrations/sqlcl/images)
//   SQL> script sql/load-images.js /path/to/images  (photos from another folder)
//
// For each product with a FILENAME, it reads that file from the folder and
// stores its bytes in IMAGE, on SQLcl's own connection, then commits.
// It runs in SQLcl's own JavaScript (ECMAScript 5), so it needs the GraalJS
// engine from the README's first step.

(function () {
  var JFile = Java.type("java.io.File");
  var Files = Java.type("java.nio.file.Files");

  // args[0] is this script's path: integrations/sqlcl/sql/load-images.js.
  // The photos are in integrations/sqlcl/images, beside sql/.
  var here = new JFile(String(args[0])).getAbsoluteFile().getParentFile();
  var dir = args.length > 1
    ? new JFile(String(args[1])).getAbsoluteFile()
    : new JFile(here, "../images").getCanonicalFile();
  if (!dir.isDirectory()) {
    ctx.write("load-images.js: no folder " + dir + "\n");
    return;
  }

  var products = [];
  var st = conn.createStatement();
  try {
    var rs = st.executeQuery("select product_id, filename from products where filename is not null order by product_id");
    while (rs.next()) products.push({ id: rs.getLong(1), file: String(rs.getString(2)) });
  } finally {
    st.close();
  }

  var update = conn.prepareStatement("update products set image = ? where product_id = ?");
  var loaded = 0, missing = [];
  try {
    for (var i = 0; i < products.length; i++) {
      var f = new JFile(dir, products[i].file);
      if (!f.isFile()) { missing.push(products[i].file); continue; }
      update.setBytes(1, Files.readAllBytes(f.toPath()));
      update.setLong(2, products[i].id);
      update.executeUpdate();
      loaded++;
    }
    conn.commit();
  } finally {
    update.close();
  }
  ctx.write("load-images.js: loaded " + loaded + " photo" + (loaded === 1 ? "" : "s") + " from " + dir + "\n");
  if (missing.length) ctx.write("load-images.js: not found: " + missing.join(", ") + "\n");
})();
