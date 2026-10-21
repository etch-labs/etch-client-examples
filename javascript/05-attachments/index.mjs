// Render templates/product-sheet.docx with product pictures sent as attachments.
//
// data/products.json names each product's picture file. Each file is added to
// an Attachments collection, which returns a key; the key goes in the data
// where the template's {{#image photo}} reads it. The pictures travel as
// separate parts of the request instead of base64 text inside the JSON.

import { readFile } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import path from 'node:path';
import { Attachments, ETCHClient } from '@get-etch/etch-client';

const here = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(here, '..', '..');
const template = path.join(root, 'templates', 'product-sheet.docx');
const dataFile = path.join(root, 'data', 'products.json');
const imagesDir = path.join(root, 'images');
const outputDir = path.join(here, 'output');

const client = new ETCHClient();

const data = JSON.parse(await readFile(dataFile, 'utf8'));

const attachments = new Attachments();
for (const product of data.products) {
  product.photo = await attachments.addFile(path.join(imagesDir, product.image));
}

const result = await client.process({ template, data, attachments });

const out = path.join(outputDir, 'product-sheet.docx');
await result.saveToFile(out);
console.log(`Wrote ${path.relative(root, out)}`);
