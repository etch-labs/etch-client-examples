// Choose what happens when the data lacks a value the template uses.
//
// The invoice is rendered for a customer whose address is not known yet, three
// times, once with each setting:
//
// - error (the default): the render fails and names the missing value
// - empty: the missing value is left out
// - keep: the tag stays in the document, {{customer.address}}, so the gap shows

import { readFile } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import path from 'node:path';
import { ETCHClient, ValidationError } from '@get-etch/etch-client';

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

const client = new ETCHClient({
  baseURL: process.env.ETCH_BASE_URL ?? 'https://api.get-etch.app',
  apiKey,
});

const data = JSON.parse(await readFile(dataFile, 'utf8'));
delete data.customer.address; // not known yet

for (const mode of ['error', 'empty', 'keep']) {
  let result;
  try {
    result = await client.process({ template, data, undefinedVariables: mode });
  } catch (error) {
    if (!(error instanceof ValidationError)) throw error;
    console.log(`${mode}: the render failed: ${error.message}`);
    continue;
  }
  const out = path.join(outputDir, `invoice-${mode}.docx`);
  await result.saveToFile(out);
  console.log(`${mode}: wrote ${path.relative(root, out)}`);
}
