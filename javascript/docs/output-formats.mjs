// Code from the JavaScript client page of the ETCH docs, section "Output formats and conversion".
// Run from the repository root: node javascript/docs/output-formats.mjs

import { ETCHClient } from '@get-etch/etch-client';

const client = new ETCHClient({
  baseURL: process.env.ETCH_BASE_URL ?? 'https://api.get-etch.app',
  apiKey: process.env.ETCH_API_KEY,
});

// convert() needs a document: render the Word file first.
const word = await client.process({
  template: 'templates/invoice.docx',
  dataFile: 'data/invoice.json',
});
await word.saveToFile('invoice.docx');

// docs:start formats
// Render a Word template straight to PDF, with the data from a JSON file
const pdf = await client.process({
  template: 'templates/invoice.docx',
  dataFile: 'data/invoice.json',
  outputFormat: 'pdf',
});
await pdf.saveToFile('invoice.pdf');

// Convert an existing document
const converted = await client.convert({
  input: 'invoice.docx',
  outputFormat: 'pdf',
});
await converted.saveToFile('converted.pdf');
// docs:end formats
console.log('Saved invoice.pdf and converted.pdf');
