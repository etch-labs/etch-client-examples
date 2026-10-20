// Code from the JavaScript client page of the ETCH docs, section "Quick start".
// Run from the repository root: node javascript/docs/quick-start.mjs

// docs:start quick-start
import { ETCHClient } from '@get-etch/etch-client';

// The client reads your API key from the ETCH_API_KEY environment variable.
const client = new ETCHClient({
  baseURL: process.env.ETCH_BASE_URL ?? 'https://api.get-etch.app',
});

const result = await client.process({
  template: 'templates/invoice.md',
  data: { name: 'Example Ltd', date: '2026-04-15', total: '99.99' },
});

await result.saveToFile('invoice-out.md');
console.log('Saved invoice-out.md');
// docs:end quick-start
