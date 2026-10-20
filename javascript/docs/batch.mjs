// Code from the JavaScript client page of the ETCH docs, section "Batches: one document per record".
// Run from the repository root: node javascript/docs/batch.mjs

import { ETCHClient } from '@get-etch/etch-client';

const client = new ETCHClient({
  baseURL: process.env.ETCH_BASE_URL ?? 'https://api.get-etch.app',
  apiKey: process.env.ETCH_API_KEY,
});

// docs:start batch
const batch = await client.renderBatch({
  template: 'templates/invoice-batch.docx',
  dataFile: 'data/invoices.json',
});

await batch.saveAll('invoices');
console.log(`${batch.succeeded} of ${batch.total} documents saved`);
// docs:end batch
