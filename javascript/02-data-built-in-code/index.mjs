// Build the invoice data in code, then render templates/invoice.docx.
//
// The line items come from an array, the totals and tax are calculated here,
// and the dates are based on today. Any JSON-compatible object works as data.

import { fileURLToPath } from 'node:url';
import path from 'node:path';
import { ETCHClient } from '@get-etch/etch-client';

const here = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(here, '..', '..');
const template = path.join(root, 'templates', 'invoice.docx');
const outputDir = path.join(here, 'output');

const taxRate = 8;

const lineItems = [
  { description: 'Website copywriting, five pages', quantity: 5, unitPrice: 180 },
  { description: 'Product photography (half day)', quantity: 1, unitPrice: 650 },
  { description: 'Image retouching', quantity: 24, unitPrice: 12 },
];

// toISOString gives the UTC date, so build YYYY-MM-DD from the local date.
function isoDate(d) {
  const pad = (n) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
}

function buildInvoice() {
  const issued = new Date();
  const due = new Date(issued);
  due.setDate(due.getDate() + 30);

  const subtotal = lineItems.reduce((sum, item) => sum + item.quantity * item.unitPrice, 0);
  const tax = Math.round(subtotal * taxRate) / 100;

  return {
    number: `DEMO-${isoDate(issued).replaceAll('-', '')}`,
    issued: isoDate(issued),
    due: isoDate(due),
    seller: {
      name: 'Mei Example Design',
      address: '12 Sample Lane, Exampleton',
      email: 'mei@example.com',
    },
    customer: {
      name: 'Kwame Placeholder',
      address: '123 Placeholder Road, Demotown',
    },
    items: lineItems,
    subtotal,
    taxRate,
    tax,
    total: subtotal + tax,
  };
}

const client = new ETCHClient();

const data = buildInvoice();
const result = await client.process({ template, data });

const out = path.join(outputDir, `invoice-${data.number}.docx`);
await result.saveToFile(out);
console.log(`Wrote ${path.relative(root, out)}`);
