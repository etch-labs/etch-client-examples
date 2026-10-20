// Check the server and the licence before starting work.
//
// 1. Health: is the server up? The program stops here if it is not.
// 2. Version: which ETCH server is answering.
// 3. Licence: whose it is, its status and expiry, what it allows, and a check
//    for one capability (PDF output) before relying on it.

import { ETCHClient } from '@get-etch/etch-client';

const capability = 'format.pdf';

const apiKey = process.env.ETCH_API_KEY;
if (!apiKey) {
  console.error('Set ETCH_API_KEY to your ETCH API key.');
  process.exit(1);
}
const baseURL = process.env.ETCH_BASE_URL ?? 'https://api.get-etch.app';

const client = new ETCHClient({ baseURL, apiKey });

const health = await client.healthcheck();
if (health.status !== 'healthy') {
  console.error(`The server at ${baseURL} is not healthy: ${health.status}`);
  process.exit(1);
}

const version = await client.version();
console.log(`Server: ${health.status}, version ${version.version}`);

const licence = await client.licenseCapabilities();
const expires = licence.expiresAt ? licence.expiresAt.toISOString().slice(0, 10) : 'never';
console.log(`Licence: ${licence.customer}, ${licence.status}, expires ${expires}`);
console.log(`Capabilities: ${licence.licenseCapabilities.join(', ')}`);
console.log(`PDF output: ${licence.has(capability) ? 'allowed' : 'not in this licence'}`);
