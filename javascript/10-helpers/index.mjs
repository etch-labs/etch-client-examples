// Render templates/sales-report.docx with the data in data/sales.json.
//
// Apart from the company and the period, the data is a list of orders, with no
// totals. The template works out every figure with helpers: filter, map, unique,
// sort, slice, sum, avg, max, length and join, used inline or chained with pipes
// in a loop.

import { readFile } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import path from 'node:path';
import { ETCHClient } from '@get-etch/etch-client';

const here = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(here, '..', '..');
const template = path.join(root, 'templates', 'sales-report.docx');
const dataFile = path.join(root, 'data', 'sales.json');
const outputDir = path.join(here, 'output');

const client = new ETCHClient();

const data = JSON.parse(await readFile(dataFile, 'utf8'));
const result = await client.process({ template, data });

const out = path.join(outputDir, 'sales-report.docx');
await result.saveToFile(out);
console.log(`Wrote ${path.relative(root, out)}`);
