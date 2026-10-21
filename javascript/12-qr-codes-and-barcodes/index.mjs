// Render templates/shipping-label.docx with the data in data/shipping.json.
//
// The template turns values from the data into codes: a Code 128 barcode of the
// tracking number, a QR code of the tracking page's address, and an EAN-13
// product barcode on each line of the packing slip.

import { readFile } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import path from 'node:path';
import { ETCHClient } from '@get-etch/etch-client';

const here = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(here, '..', '..');
const template = path.join(root, 'templates', 'shipping-label.docx');
const dataFile = path.join(root, 'data', 'shipping.json');
const outputDir = path.join(here, 'output');

const client = new ETCHClient();

const data = JSON.parse(await readFile(dataFile, 'utf8'));
const result = await client.process({ template, data });

const out = path.join(outputDir, 'shipping-label.docx');
await result.saveToFile(out);
console.log(`Wrote ${path.relative(root, out)}`);
