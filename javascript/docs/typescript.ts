// Code from the JavaScript client page of the ETCH docs, section "TypeScript".
// Run from the repository root: node javascript/docs/typescript.ts
// Node 23.6 or later runs it as it is; it also passes tsc --noEmit.

// docs:start typescript
import { ETCHClient, type ProcessResult } from '@get-etch/etch-client';
// docs:end typescript

// docs:start typescript
type InvoiceData = {
  customer: string;
  items: Array<{ description: string; price: number }>;
  total: number;
};

// The client reads the API key from ETCH_API_KEY.
const client = new ETCHClient({
  baseURL: process.env.ETCH_BASE_URL ?? 'https://api.get-etch.app',
});

const data: InvoiceData = {
  customer: 'Example Ltd',
  items: [{ description: 'Consulting', price: 1500 }],
  total: 1500,
};

const result: ProcessResult = await client.process({
  template: 'summary.md',
  templateContent: '{{customer}} owes {{total}}.',
  data,
});
// docs:end typescript
console.log(result.data.toString());
