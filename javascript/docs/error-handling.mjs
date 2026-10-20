// Code from the JavaScript client page of the ETCH docs, section "Error handling".
// Run from the repository root: node javascript/docs/error-handling.mjs

// docs:start errors
import {
  AuthenticationError, CapabilityNotLicensedError, ValidationError,
  RateLimitError, NotFoundError, ETCHError,
} from '@get-etch/etch-client';
// docs:end errors
import { ETCHClient } from '@get-etch/etch-client';

const client = new ETCHClient({
  baseURL: process.env.ETCH_BASE_URL ?? 'https://api.get-etch.app',
  apiKey: process.env.ETCH_API_KEY,
});

// docs:start errors
try {
  await client.process({ template: 'invoice.docx', data: {} });
} catch (error) {
  if (error instanceof AuthenticationError) {
    console.error('Check your API key:', error.message);
  } else if (error instanceof CapabilityNotLicensedError) {
    console.error('Not on your plan:', error.capability);
  } else if (error instanceof ValidationError) {
    console.error('Invalid request:', error.message);
  } else if (error instanceof RateLimitError) {
    console.error('Rate limited. Retry after', error.retryAfter, 'seconds');
  } else if (error instanceof NotFoundError) {
    console.error('Not found:', error.message);
  } else if (error instanceof ETCHError) {
    console.error(`[${error.code}] ${error.message}`);
  } else {
    throw error;
  }
}
// docs:end errors
console.log('Done');
