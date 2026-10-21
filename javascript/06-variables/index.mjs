// Render templates/welcome-letter.docx with the data in data/welcome.json.
//
// The template uses three kinds of variable: nested paths such as
// {{customer.address.town}}, default values such as {{manager.phone | "..."}}
// for data that is missing or null, and built-in variables such as {{etch.date}}
// that ETCH fills in without any data.

import { readFile } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import path from 'node:path';
import { ETCHClient } from '@get-etch/etch-client';

const here = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(here, '..', '..');
const template = path.join(root, 'templates', 'welcome-letter.docx');
const dataFile = path.join(root, 'data', 'welcome.json');
const outputDir = path.join(here, 'output');

const client = new ETCHClient();

const data = JSON.parse(await readFile(dataFile, 'utf8'));
const result = await client.process({ template, data });

const out = path.join(outputDir, 'welcome-letter.docx');
await result.saveToFile(out);
console.log(`Wrote ${path.relative(root, out)}`);
