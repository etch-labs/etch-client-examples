// Render templates/renewal-letter.docx once for each customer in data/renewals.json.
//
// The same template produces a different letter for each customer: its
// conditionals choose the greeting, describe the customer's plan, and add or
// leave out sentences about renewal, usage and discounts.

import { readFile } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import path from 'node:path';
import { ETCHClient } from '@get-etch/etch-client';

const here = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(here, '..', '..');
const template = path.join(root, 'templates', 'renewal-letter.docx');
const dataFile = path.join(root, 'data', 'renewals.json');
const outputDir = path.join(here, 'output');

const client = new ETCHClient();

const { seller, customers } = JSON.parse(await readFile(dataFile, 'utf8'));

for (const customer of customers) {
  const result = await client.process({ template, data: { ...customer, seller } });

  const out = path.join(outputDir, `renewal-${customer.accountNumber}.docx`);
  await result.saveToFile(out);
  console.log(`Wrote ${path.relative(root, out)}`);
}
