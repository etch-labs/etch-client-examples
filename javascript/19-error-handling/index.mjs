// Make three requests that fail, and handle each failure.
//
// 1. A wrong API key: AuthenticationError.
// 2. A stored template that does not exist: NotFoundError.
// 3. Data missing a field the template needs: ValidationError.
//
// Every client error is an ETCHError, so one handler can report any failure:
// its code (such as TEMPLATE_NOT_FOUND), the HTTP status and the message.

import { readFile } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import path from 'node:path';
import {
  AuthenticationError,
  ETCHClient,
  ETCHError,
  NotFoundError,
  ValidationError,
} from '@get-etch/etch-client';

const here = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(here, '..', '..');
const template = path.join(root, 'templates', 'invoice.docx');
const dataFile = path.join(root, 'data', 'invoice.json');

const apiKey = process.env.ETCH_API_KEY;
if (!apiKey) {
  console.error('Set ETCH_API_KEY to your ETCH API key.');
  process.exit(1);
}
const baseURL = process.env.ETCH_BASE_URL ?? 'https://api.get-etch.app';

function report(error, advice) {
  console.log(`  ${error.constructor.name}: ${error.code} (HTTP ${error.statusCode})`);
  console.log(`  ${error.message}`);
  console.log(`  What to do: ${advice}`);
}

async function attempt(description, render) {
  console.log(description);
  try {
    await render();
    console.log('  Rendered without an error');
  } catch (error) {
    if (error instanceof AuthenticationError) {
      report(error, 'Check the API key in ETCH_API_KEY.');
    } else if (error instanceof NotFoundError) {
      report(error, 'Upload the template first, or check its name.');
    } else if (error instanceof ValidationError) {
      report(error, 'Add the missing values to the data, or change the template.');
    } else if (error instanceof ETCHError) {
      report(error, 'Something else went wrong; the code and message say what.');
    } else {
      throw error;
    }
  }
}

const data = JSON.parse(await readFile(dataFile, 'utf8'));
const withoutCustomer = { ...data };
delete withoutCustomer.customer;

const wrongKey = new ETCHClient({ baseURL, apiKey: 'etch_not_a_real_key' });
await attempt('1. Rendering with a wrong API key', () =>
  wrongKey.process({ template, data }));

const client = new ETCHClient({ baseURL, apiKey });
await attempt('2. Rendering a stored template that does not exist', () =>
  client.process({ template: 'no-such-template.docx', templateSource: 'server', data }));
await attempt('3. Rendering with data that has no customer', () =>
  client.process({ template, data: withoutCustomer }));
