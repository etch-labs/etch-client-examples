// Render templates/account-statement.docx with the data in data/statement.json.
//
// The data holds raw values (ISO dates, plain numbers, upper-case descriptions)
// and the template formats them: dateFormat and timeFormat for dates and times,
// currency and percentage for numbers, padLeft for the account number, and a
// chain such as trim | lowercase | title | truncate:40 to tidy a description.

import { readFile } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import path from 'node:path';
import { ETCHClient } from '@get-etch/etch-client';

const here = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(here, '..', '..');
const template = path.join(root, 'templates', 'account-statement.docx');
const dataFile = path.join(root, 'data', 'statement.json');
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

const out = path.join(outputDir, 'account-statement.docx');
await result.saveToFile(out);
console.log(`Wrote ${path.relative(root, out)}`);
