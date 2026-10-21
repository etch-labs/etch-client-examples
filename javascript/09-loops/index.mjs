// Render templates/order-summary.docx with the data in data/order.json.
//
// The template does the looping: #each over the order's shipments, a nested
// #each over each shipment's items (and a third over each item's tags), #each
// over an object for the summary line, and #repeat for the star ratings.

import { readFile } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import path from 'node:path';
import { ETCHClient } from '@get-etch/etch-client';

const here = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(here, '..', '..');
const template = path.join(root, 'templates', 'order-summary.docx');
const dataFile = path.join(root, 'data', 'order.json');
const outputDir = path.join(here, 'output');

const client = new ETCHClient();

const data = JSON.parse(await readFile(dataFile, 'utf8'));
const result = await client.process({ template, data });

const out = path.join(outputDir, 'order-summary.docx');
await result.saveToFile(out);
console.log(`Wrote ${path.relative(root, out)}`);
