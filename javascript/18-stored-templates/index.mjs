// Store a template on ETCH, render it by name, replace it, download it and delete it.
//
// 1. Remove any copy of the template left by an earlier run.
// 2. Upload templates/invoice.docx as example-invoice.docx.
// 3. List the stored templates that match the name.
// 4. Render the stored template by name.
// 5. Replace it with templates/invoice-with-logo.docx.
// 6. Render it by the same name again: the output now has the logo.
// 7. Download the stored template.
// 8. Delete it.

import { readFile } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import path from 'node:path';
import { ETCHClient, imageFromFile } from '@get-etch/etch-client';

const here = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(here, '..', '..');
const template = path.join(root, 'templates', 'invoice.docx');
const newVersion = path.join(root, 'templates', 'invoice-with-logo.docx');
const dataFile = path.join(root, 'data', 'invoice.json');
const logo = path.join(root, 'images', 'logo.png');
const outputDir = path.join(here, 'output');

const storedName = 'example-invoice.docx';

const client = new ETCHClient();

const data = JSON.parse(await readFile(dataFile, 'utf8'));
// Only the second version of the template shows the logo; the first ignores it.
data.logo = await imageFromFile(logo);

async function render(fileName) {
  const result = await client.process({ template: storedName, templateSource: 'server', data });
  const out = path.join(outputDir, fileName);
  await result.saveToFile(out);
  console.log(`Wrote ${path.relative(root, out)}`);
}

const earlier = await client.listTemplates({ search: storedName });
for (const t of earlier.templates) {
  if (t.name === storedName) {
    await client.deleteTemplate(t.id);
    console.log(`Removed ${storedName} left by an earlier run`);
  }
}

const stored = await client.uploadTemplate({ file: template, name: storedName });
console.log(`Uploaded ${stored.name}`);

const { templates, total } = await client.listTemplates({ search: storedName });
console.log(`Stored templates matching ${storedName}: ${total}`);
for (const t of templates) {
  console.log(`  ${t.name}, ${t.size} bytes`);
}

await render(`invoice-${data.number}.docx`);

await client.updateTemplate(stored.id, newVersion);
console.log(`Replaced ${storedName} with ${path.basename(newVersion)}`);

await render(`invoice-${data.number}-with-logo.docx`);

const downloaded = await client.downloadTemplate(stored.id);
const out = path.join(outputDir, storedName);
await downloaded.saveToFile(out);
console.log(`Wrote ${path.relative(root, out)}`);

await client.deleteTemplate(stored.id);
console.log(`Deleted ${storedName}`);
