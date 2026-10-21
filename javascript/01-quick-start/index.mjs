// Render templates/invoice.docx with the data in data/invoice.json.

import { readFile } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import path from 'node:path';
import { ETCHClient } from '@get-etch/etch-client';

const here = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(here, '..', '..');

const data = JSON.parse(await readFile(path.join(root, 'data', 'invoice.json'), 'utf8'));

const client = new ETCHClient(); // reads ETCH_API_KEY, and ETCH_BASE_URL if you set it
const result = await client.process({
  template: path.join(root, 'templates', 'invoice.docx'),
  data,
});

const out = path.join(here, 'output', `invoice-${data.number}.docx`);
await result.saveToFile(out);
console.log(`Wrote ${path.relative(root, out)}`);
