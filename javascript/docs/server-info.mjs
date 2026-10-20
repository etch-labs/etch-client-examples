// Code from the JavaScript client page of the ETCH docs, section "Server and account information".
// Run from the repository root: node javascript/docs/server-info.mjs

import { ETCHClient } from '@get-etch/etch-client';

const client = new ETCHClient({
  baseURL: process.env.ETCH_BASE_URL ?? 'https://api.get-etch.app',
  apiKey: process.env.ETCH_API_KEY,
});

// docs:start server
const health = await client.healthcheck();
console.log(health.status, health.version);

const server = await client.version();
console.log(server.version, server.formats);

const license = await client.licenseInfo();
console.log(license.customer, license.expiresAt);

const caps = await client.licenseCapabilities();
console.log(caps.has('format.pdf'));
// docs:end server
