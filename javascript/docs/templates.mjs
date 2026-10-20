// Code from the JavaScript client page of the ETCH docs, section "Templates".
// Run from the repository root: node javascript/docs/templates.mjs

import { ETCHClient } from '@get-etch/etch-client';

const client = new ETCHClient({
  baseURL: process.env.ETCH_BASE_URL ?? 'https://api.get-etch.app',
  apiKey: process.env.ETCH_API_KEY,
});

// docs:start templates
// A local file, uploaded with the request
await client.process({
  template: 'templates/letter.md',
  data: { name: 'Sample Co' },
});

// Template text you already have; the name's extension gives the format
await client.process({
  template: 'greeting.md',
  templateContent: 'Hello {{name}}, welcome to {{company}}.',
  data: { name: 'Sample Co', company: 'Example Ltd' },
});

// A template stored on your account, rendered by name. Upload it once:
await client.uploadTemplate({ file: 'templates/letter.md' });
await client.process({
  template: 'letter.md',
  templateSource: 'server',
  data: { name: 'Sample Co' },
});
// docs:end templates

// Remove the stored copy again.
const { templates } = await client.listTemplates();
for (const stored of templates.filter((t) => t.name === 'letter.md' && !t.folder)) {
  await client.deleteTemplate(stored.id);
}
console.log('Rendered from a file, from text and from a stored template');
