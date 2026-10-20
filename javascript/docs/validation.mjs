// Code from the JavaScript client page of the ETCH docs, section "Validate and fix a template".
// Run from the repository root: node javascript/docs/validation.mjs

// docs:start fix
import { writeFile } from 'node:fs/promises';
// docs:end fix
import { ETCHClient } from '@get-etch/etch-client';

const client = new ETCHClient({
  baseURL: process.env.ETCH_BASE_URL ?? 'https://api.get-etch.app',
  apiKey: process.env.ETCH_API_KEY,
});

// docs:start validate
const report = await client.validateTemplate('templates/invoice.docx', {
  data: { customer: { name: 'Example Ltd' } },
});

console.log(report.summary.text);
for (const issue of report.issues) {
  console.log(`${issue.severity} ${issue.code}: ${issue.message}`);
}
// docs:end validate

// docs:start fix
const fixed = await client.fixTemplate('templates/invoice.docx');

console.log(`Issues repaired: ${fixed.fix?.applied ?? 0}`);
if (fixed.document) {
  // for example invoice-fixed.docx
  await writeFile(fixed.documentName ?? 'fixed.docx', fixed.document);
}
// docs:end fix
