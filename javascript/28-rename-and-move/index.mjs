// Rename a stored template, move it to another folder, and render it at its new path.
//
// 1. Remove anything left under example-rename by an earlier run.
// 2. Create the folder example-rename, and example-rename/archive inside it.
// 3. Upload templates/invoice.docx into example-rename as invoice-draft.docx.
// 4. Rename it to invoice.docx.
// 5. Move it into example-rename/archive.
// 6. Render it by its new path, example-rename/archive/invoice.docx.
// 7. Try two changes ETCH refuses: a new extension, and a folder that does not exist.
// 8. Delete the template and both folders.

import { readFile } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import path from 'node:path';
import { ETCHClient, ETCHError } from '@get-etch/etch-client';

const here = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(here, '..', '..');
const template = path.join(root, 'templates', 'invoice.docx');
const dataFile = path.join(root, 'data', 'invoice.json');
const outputDir = path.join(here, 'output');

const top = 'example-rename';
const archive = `${top}/archive`;
const draft = 'invoice-draft.docx';
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

// Make a change ETCH should refuse, and print the error's code and HTTP status with the reason.
async function refused(description, change, reason) {
  try {
    await change();
    console.log(`${description}: done`);
  } catch (error) {
    if (!(error instanceof ETCHError)) throw error;
    console.log(`${description}: ${error.code} (HTTP ${error.statusCode}). ${reason}`);
  }
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
await client.createFolder({ name: 'archive', parent: top });
console.log(`Created folder ${archive}`);

const stored = await client.uploadTemplate({ file: template, name: draft, folder: top });
console.log(`Uploaded ${stored.name} to ${stored.folder}`);

// Each change returns the template as it is now. Use the id from the result
// for the next call: some servers give a renamed template a new id.
const renamed = await client.renameTemplate(stored.id, name);
console.log(`Renamed ${stored.name} to ${renamed.name}`);

const moved = await client.moveTemplate(renamed.id, archive);
console.log(`Moved ${moved.name} to ${moved.folder}`);

const result = await client.process({
  template: `${moved.folder}/${moved.name}`,
  templateSource: 'server',
  data,
});
const out = path.join(outputDir, `invoice-${data.number}.docx`);
await result.saveToFile(out);
console.log(`Wrote ${path.relative(root, out)}`);

await refused(
  `Renaming ${moved.name} to invoice.pdf`,
  () => client.renameTemplate(moved.id, 'invoice.pdf'),
  'A template keeps its extension.',
);
await refused(
  `Moving ${moved.name} to ${top}/drafts`,
  () => client.moveTemplate(moved.id, `${top}/drafts`),
  'Create the folder first.',
);

await client.deleteTemplate(moved.id);
await client.deleteFolder(archive);
await client.deleteFolder(top);
console.log(`Deleted ${name}, ${archive} and ${top}`);
