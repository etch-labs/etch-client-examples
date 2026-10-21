// Render templates/sales-workbook.xlsx with the data in data/sales-by-region.json.
//
// The template is an Excel workbook. A row loop fills each table, the total
// formulas below it grow to cover the new rows, and {{#sheet-each regions}}
// copies the region sheet once for each region, named after it.

import { readFile } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import path from 'node:path';
import { ETCHClient } from '@get-etch/etch-client';

const here = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(here, '..', '..');
const template = path.join(root, 'templates', 'sales-workbook.xlsx');
const dataFile = path.join(root, 'data', 'sales-by-region.json');
const outputDir = path.join(here, 'output');

const client = new ETCHClient();

const data = JSON.parse(await readFile(dataFile, 'utf8'));
const result = await client.process({ template, data });

const out = path.join(outputDir, 'sales-workbook.xlsx');
await result.saveToFile(out);
console.log(`Wrote ${path.relative(root, out)}`);
