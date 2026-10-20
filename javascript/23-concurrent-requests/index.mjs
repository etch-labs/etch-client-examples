// Render several invoices at the same time, at most two at once.
//
// Each invoice in data/invoices.json is its own request to templates/invoice.docx.
// LIMIT workers take invoices from a shared queue until it is empty, so no more
// than LIMIT requests are in flight and a long list does not run into the
// server's rate limit.

import { readFile } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import path from 'node:path';
import { ETCHClient } from '@get-etch/etch-client';

const here = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(here, '..', '..');
const template = path.join(root, 'templates', 'invoice.docx');
const dataFile = path.join(root, 'data', 'invoices.json');
const outputDir = path.join(here, 'output');

const LIMIT = 2;

const apiKey = process.env.ETCH_API_KEY;
if (!apiKey) {
  console.error('Set ETCH_API_KEY to your ETCH API key.');
  process.exit(1);
}

const client = new ETCHClient({
  baseURL: process.env.ETCH_BASE_URL ?? 'https://api.get-etch.app',
  apiKey,
});

const { seller, invoices } = JSON.parse(await readFile(dataFile, 'utf8'));

async function render(invoice) {
  const result = await client.process({ template, data: { ...invoice, seller } });
  const out = path.join(outputDir, `invoice-${invoice.number}.docx`);
  await result.saveToFile(out);
  console.log(`Wrote ${path.relative(root, out)}`);
}

const queue = [...invoices];
async function worker() {
  while (queue.length > 0) {
    await render(queue.shift());
  }
}

const started = performance.now();
await Promise.all(Array.from({ length: LIMIT }, worker));
const elapsed = (performance.now() - started) / 1000;
console.log(`Rendered ${invoices.length} invoices in ${elapsed.toFixed(1)} seconds, up to ${LIMIT} at a time`);
