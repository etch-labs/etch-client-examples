// Fetch the invoice data from a URL, then render templates/invoice.docx.
//
// Pass the URL as the first argument or in INVOICE_DATA_URL. Any URL that
// returns the JSON works, for example the raw URL of data/invoice.json in this
// repository. If the URL needs a token, put it in INVOICE_DATA_TOKEN and the
// example sends it as a bearer token.

import { fileURLToPath } from 'node:url';
import path from 'node:path';
import { ETCHClient } from '@get-etch/etch-client';

const here = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(here, '..', '..');
const template = path.join(root, 'templates', 'invoice.docx');
const outputDir = path.join(here, 'output');

async function fetchJson(url, token) {
  const headers = { Accept: 'application/json' };
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }
  const response = await fetch(url, { headers });
  if (!response.ok) {
    throw new Error(`GET ${url} returned ${response.status}`);
  }
  return response.json();
}

const url = process.argv[2] ?? process.env.INVOICE_DATA_URL;
if (!url) {
  console.error('Pass the URL of the invoice JSON as an argument or in INVOICE_DATA_URL.');
  process.exit(1);
}

const client = new ETCHClient();

const data = await fetchJson(url, process.env.INVOICE_DATA_TOKEN);
const result = await client.process({ template, data });

const out = path.join(outputDir, `invoice-${data.number}.docx`);
await result.saveToFile(out);
console.log(`Wrote ${path.relative(root, out)}`);
