// Render one invoice per record in data/invoices.json, in a single request.
//
// The template's {{#document-each invoices filename="invoice-{{number}}.docx"}}
// marker makes the server render it once per invoice and return the documents
// together. The example saves them to a folder and as one zip file, and reports
// any record that failed: one bad record fails on its own, and the others are
// still returned.

import { readFile } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import path from 'node:path';
import { ETCHClient } from '@get-etch/etch-client';

const here = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(here, '..', '..');
const template = path.join(root, 'templates', 'invoice-batch.docx');
const dataFile = path.join(root, 'data', 'invoices.json');
const outputDir = path.join(here, 'output');

const apiKey = process.env.ETCH_API_KEY;
if (!apiKey) {
  console.error('Set ETCH_API_KEY to your ETCH API key.');
  process.exit(1);
}

const client = new ETCHClient({
  baseURL: process.env.ETCH_BASE_URL ?? 'https://api.get-etch.app',
  apiKey,
});

const data = JSON.parse(await readFile(dataFile, 'utf8'));
const result = await client.renderBatch({ template, data });

console.log(`Rendered ${result.succeeded} of ${result.total} invoices`);
for (const record of result.failures) {
  console.log(`  record ${record.index + 1} failed: ${record.error}`);
}

for (const file of await result.saveAll(path.join(outputDir, 'invoices'))) {
  console.log(`Wrote ${path.relative(root, file)}`);
}
const archive = path.join(outputDir, 'invoices.zip');
await result.saveArchive(archive);
console.log(`Wrote ${path.relative(root, archive)}`);
