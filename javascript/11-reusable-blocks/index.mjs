// Render templates/quote.docx with the data in data/quote.json.
//
// The template defines its signature block and contact line once as snippets
// and uses them more than once, names values it reuses with #var, and keeps a
// note for the template's editor out of the document with #exclude.

import { readFile } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import path from 'node:path';
import { ETCHClient } from '@get-etch/etch-client';

const here = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(here, '..', '..');
const template = path.join(root, 'templates', 'quote.docx');
const dataFile = path.join(root, 'data', 'quote.json');
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
const result = await client.process({ template, data });

const out = path.join(outputDir, 'quote.docx');
await result.saveToFile(out);
console.log(`Wrote ${path.relative(root, out)}`);
