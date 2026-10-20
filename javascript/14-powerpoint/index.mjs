// Render templates/project-update.pptx with the data in data/projects.json.
//
// The template is a PowerPoint deck. {{#slide-each projects}} turns its middle
// slide into one slide per project, each with its own milestones and speaker
// notes, and a table row loop on the last slide lists every project.

import { readFile } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import path from 'node:path';
import { ETCHClient } from '@get-etch/etch-client';

const here = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(here, '..', '..');
const template = path.join(root, 'templates', 'project-update.pptx');
const dataFile = path.join(root, 'data', 'projects.json');
const outputDir = path.join(here, 'output');

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
const result = await client.process({ template, data });

const out = path.join(outputDir, 'project-update.pptx');
await result.saveToFile(out);
console.log(`Wrote ${path.relative(root, out)}`);
