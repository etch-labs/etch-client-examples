// Code from the JavaScript client page of the ETCH docs, section "Managing templates and folders".
// Run from the repository root: node javascript/docs/managing-templates.mjs

import { ETCHClient } from '@get-etch/etch-client';

const client = new ETCHClient({
  baseURL: process.env.ETCH_BASE_URL ?? 'https://api.get-etch.app',
  apiKey: process.env.ETCH_API_KEY,
});

// An earlier run may have left the folder behind; it must be empty to delete it.
for (const old of await client.listFolders()) {
  if (old.path !== 'docs-example-invoices') continue;
  const { templates: leftovers } = await client.listTemplates({ folder: old.path });
  for (const t of leftovers) await client.deleteTemplate(t.id);
  await client.deleteFolder(old.id);
}

// docs:start manage
const folder = await client.createFolder({ name: 'docs-example-invoices' });

const uploaded = await client.uploadTemplate({
  file: 'templates/letter.md',
  folder: folder.path,
  tags: ['letters'],
});

const { templates, total } = await client.listTemplates({
  folder: folder.path,
});
console.log(`Templates in the folder: ${total}`);
for (const t of templates) {
  console.log(t.name, t.id);
}

await client.deleteTemplate(uploaded.id);
await client.deleteFolder(folder.id);
// docs:end manage
