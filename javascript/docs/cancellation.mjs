// Code from the JavaScript client page of the ETCH docs, section "Cancellation".
// Run from the repository root: node javascript/docs/cancellation.mjs

import { ETCHClient } from '@get-etch/etch-client';

const client = new ETCHClient({
  baseURL: process.env.ETCH_BASE_URL ?? 'https://api.get-etch.app',
  apiKey: process.env.ETCH_API_KEY,
});

// docs:start cancel
const controller = new AbortController();
const timer = setTimeout(() => controller.abort(), 5000);

const result = await client.process({
  template: 'templates/invoice.docx',
  dataFile: 'data/invoice.json',
  signal: controller.signal,
});
clearTimeout(timer);
// docs:end cancel
console.log(`Rendered ${result.data.length} bytes before the 5 second limit`);
