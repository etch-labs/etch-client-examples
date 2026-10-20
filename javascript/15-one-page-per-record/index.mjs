// Render templates/certificate.docx with the data in data/certificates.json.
//
// {{#page-each attendees | sort "name"}} in the template repeats the page once
// per attendee, in name order, with a page break between them: one Word
// document holding a certificate for everyone on the course.

import { readFile } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import path from 'node:path';
import { ETCHClient } from '@get-etch/etch-client';

const here = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(here, '..', '..');
const template = path.join(root, 'templates', 'certificate.docx');
const dataFile = path.join(root, 'data', 'certificates.json');
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

const out = path.join(outputDir, 'certificates.docx');
await result.saveToFile(out);
console.log(`Wrote ${path.relative(root, out)}`);
