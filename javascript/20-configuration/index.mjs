// Configure the client: where it connects, how long it waits and how often it retries.
//
// The API key and the server address come from the environment, never from the
// code. Two clients are built:
//
// 1. One for normal use: up to two minutes for each attempt, and up to three
//    retries after the first. It renders the invoice to PDF.
// 2. One that gives up quickly: 50 milliseconds and no retries. The same
//    render takes longer than that, so it fails with a TimeoutError.

import { readFile } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import path from 'node:path';
import { ETCHClient, TimeoutError } from '@get-etch/etch-client';

const here = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(here, '..', '..');
const template = path.join(root, 'templates', 'invoice.docx');
const dataFile = path.join(root, 'data', 'invoice.json');
const outputDir = path.join(here, 'output');

const apiKey = process.env.ETCH_API_KEY;
if (!apiKey) {
  console.error('Set ETCH_API_KEY to your ETCH API key.');
  process.exit(1);
}
const baseURL = process.env.ETCH_BASE_URL ?? 'https://api.get-etch.app';
console.log(`Connecting to ${baseURL}`);

const data = JSON.parse(await readFile(dataFile, 'utf8'));
const request = { template, data, outputFormat: 'pdf' };

const client = new ETCHClient({
  baseURL,
  apiKey,
  timeout: 120_000, // milliseconds for each attempt
  maxRetries: 3, // retries after the first attempt
});
const result = await client.process(request);
const out = path.join(outputDir, `invoice-${data.number}.pdf`);
await result.saveToFile(out);
console.log(`Wrote ${path.relative(root, out)}`);

const quick = new ETCHClient({ baseURL, apiKey, timeout: 50, maxRetries: 0 });
const started = performance.now();
try {
  await quick.process(request);
  console.log('The quick client finished in time');
} catch (error) {
  if (!(error instanceof TimeoutError)) throw error;
  const waited = (performance.now() - started) / 1000;
  console.log(`The quick client gave up after ${waited.toFixed(2)} seconds: ${error.code}`);
}
