// Add a logo to the invoice data, then render templates/invoice-with-logo.docx.
//
// The template places the image with {{#image logo width="56"}}. An image in
// the data is a data: URI, and imageFromFile builds one from a file on disk
// (imageFromBytes does the same for bytes you already have).

import { readFile } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import path from 'node:path';
import { ETCHClient, imageFromFile } from '@get-etch/etch-client';

const here = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(here, '..', '..');
const template = path.join(root, 'templates', 'invoice-with-logo.docx');
const dataFile = path.join(root, 'data', 'invoice.json');
const logo = path.join(root, 'images', 'logo.png');
const outputDir = path.join(here, 'output');

const client = new ETCHClient();

const data = JSON.parse(await readFile(dataFile, 'utf8'));
data.logo = await imageFromFile(logo);

const result = await client.process({ template, data });

const out = path.join(outputDir, `invoice-${data.number}-with-logo.docx`);
await result.saveToFile(out);
console.log(`Wrote ${path.relative(root, out)}`);
