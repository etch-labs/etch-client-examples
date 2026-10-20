// Keep stored templates in folders, and render one by its path.
//
// 1. Remove anything left under example-folders by an earlier run.
// 2. Create the folder example-folders, and example-folders/invoices inside it.
// 3. Upload templates/invoice.docx into example-folders/invoices.
// 4. List the folders, and the templates in example-folders/invoices.
// 5. Render the template by its path, example-folders/invoices/invoice.docx.
// 6. Delete the template and both folders.

import { readFile } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import path from 'node:path';
import { ETCHClient } from '@get-etch/etch-client';

const here = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(here, '..', '..');
const template = path.join(root, 'templates', 'invoice.docx');
const dataFile = path.join(root, 'data', 'invoice.json');
const outputDir = path.join(here, 'output');

const top = 'example-folders';
const invoices = `${top}/invoices`;
const name = 'invoice.docx';

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

// The folders under top, deepest first, so each is empty when its turn comes.
async function exampleFolders() {
  const folders = (await client.listFolders())
    .filter((f) => f.path === top || f.path.startsWith(`${top}/`));
  return folders.sort((a, b) => b.path.split('/').length - a.path.split('/').length);
}

// A folder must be empty before it can be deleted.
const leftovers = await exampleFolders();
for (const folder of leftovers) {
  const { templates } = await client.listTemplates({ folder: folder.path });
  for (const t of templates) {
    await client.deleteTemplate(t.id);
  }
  await client.deleteFolder(folder.id);
}
if (leftovers.length > 0) {
  console.log(`Removed ${top} left by an earlier run`);
}

await client.createFolder({ name: top });
console.log(`Created folder ${top}`);
await client.createFolder({ name: 'invoices', parent: top });
console.log(`Created folder ${invoices}`);

const stored = await client.uploadTemplate({ file: template, name, folder: invoices });
console.log(`Uploaded ${stored.name} to ${invoices}`);

console.log(`Folders under ${top}:`);
for (const folder of (await exampleFolders()).sort((a, b) => a.path.localeCompare(b.path))) {
  console.log(`  ${folder.path}, templates: ${folder.templateCount}`);
}

const { templates, total } = await client.listTemplates({ folder: invoices });
console.log(`Templates in ${invoices}: ${total}`);
for (const t of templates) {
  console.log(`  ${t.name}`);
}

const result = await client.process({ template: `${invoices}/${name}`, templateSource: 'server', data });
const out = path.join(outputDir, `invoice-${data.number}.docx`);
await result.saveToFile(out);
console.log(`Wrote ${path.relative(root, out)}`);

await client.deleteTemplate(stored.id);
await client.deleteFolder(invoices);
await client.deleteFolder(top);
console.log(`Deleted ${name}, ${invoices} and ${top}`);
