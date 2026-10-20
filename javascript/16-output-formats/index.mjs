// Render the invoice in different output formats, then convert a document to PDF.
//
// 1. Render templates/invoice.docx to Word: the format follows the template.
// 2. Render the same template straight to PDF with outputFormat.
// 3. Render a password-protected Word document.
// 4. Convert an existing document (the Word file from step 1) to PDF.

import { readFile } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import path from 'node:path';
import { ETCHClient } from '@get-etch/etch-client';

const here = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(here, '..', '..');
const template = path.join(root, 'templates', 'invoice.docx');
const dataFile = path.join(root, 'data', 'invoice.json');
const outputDir = path.join(here, 'output');

// A placeholder: in a real application, take the password from configuration.
const password = 'example-password';

const apiKey = process.env.ETCH_API_KEY;
if (!apiKey) {
  console.error('Set ETCH_API_KEY to your ETCH API key.');
  process.exit(1);
}

const client = new ETCHClient({
  baseURL: process.env.ETCH_BASE_URL ?? 'https://api.get-etch.app',
  apiKey,
});

async function save(result, fileName) {
  const out = path.join(outputDir, fileName);
  await result.saveToFile(out);
  console.log(`Wrote ${path.relative(root, out)}`);
}

const data = JSON.parse(await readFile(dataFile, 'utf8'));
const name = `invoice-${data.number}`;

const word = await client.process({ template, data });
await save(word, `${name}.docx`);

const pdf = await client.process({ template, data, outputFormat: 'pdf' });
await save(pdf, `${name}.pdf`);

const protectedWord = await client.process({ template, data, password });
await save(protectedWord, `${name}-protected.docx`);

const converted = await client.convert({
  input: path.join(outputDir, `${name}.docx`),
  outputFormat: 'pdf',
});
await save(converted, `${name}-converted.pdf`);
