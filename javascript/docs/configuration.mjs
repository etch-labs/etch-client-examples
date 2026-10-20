// Code from the JavaScript client page of the ETCH docs, section "Configuration".
// Run from the repository root: node javascript/docs/configuration.mjs
// The client is built but makes no request, because the proxy address is a placeholder.

import { ETCHClient } from '@get-etch/etch-client';

const apiKey = process.env.ETCH_API_KEY;

// docs:start config
const client = new ETCHClient({
  baseURL: 'https://api.get-etch.app',
  apiKey,                     // from ETCH_API_KEY
  timeout: 30000,             // ms per attempt; default 30000
  maxRetries: 3,              // retries after the first attempt; default 3
  headers: { 'X-Request-Source': 'nightly-run' },
  proxy: { host: 'proxy.example.com', port: 8080 },  // Node only
});
// docs:end config
console.log('Client configured:', client instanceof ETCHClient);
