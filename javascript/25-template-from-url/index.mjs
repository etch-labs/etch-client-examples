// Render a template that lives at a URL rather than in a local file.
//
// Pass the URL as the first argument or in INVOICE_TEMPLATE_URL, for example the
// raw URL of templates/invoice.docx in this repository. The client downloads the
// template and sends it with the data from data/invoice.json. The download is a
// plain request: your ETCH API key goes only to ETCH, never to the template's host.

import { readFile } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import path from 'node:path';
import { ETCHClient } from '@get-etch/etch-client';

const here = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(here, '..', '..');
const dataFile = path.join(root, 'data', 'invoice.json');
const outputDir = path.join(here, 'output');

const url = process.argv[2] ?? process.env.INVOICE_TEMPLATE_URL;
if (!url) {
  console.error('Pass the URL of the template as an argument or in INVOICE_TEMPLATE_URL.');
  process.exit(1);
}

const client = new ETCHClient();

const data = JSON.parse(await readFile(dataFile, 'utf8'));
const result = await client.process({ template: url, data });

const out = path.join(outputDir, 'invoice.docx');
await result.saveToFile(out);
console.log(`Wrote ${path.relative(root, out)}`);
