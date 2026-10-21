// Build the invoice from JavaScript classes and render templates/typed-invoice.md.
//
// The invoice has a date, a timestamp, exact decimal amounts, a field renamed
// in toJSON() and a field that is null. The client turns the objects into
// JSON itself: no plain object is built by hand.

import { fileURLToPath } from 'node:url';
import path from 'node:path';
import { ETCHClient } from '@get-etch/etch-client';

const here = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(here, '..', '..');
const template = path.join(root, 'templates', 'typed-invoice.md');
const outputDir = path.join(here, 'output');

// JavaScript has no decimal type, and a number is a double. An exact amount is
// kept as its text and written into the JSON as a number with JSON.rawJSON.
class Decimal {
  constructor(text) {
    this.text = text;
  }

  toJSON() {
    return JSON.rawJSON(this.text);
  }
}

class Customer {
  constructor(name, email) {
    this.name = name;
    this.email = email;
  }
}

class Line {
  constructor(description, quantity, price) {
    this.description = description;
    this.quantity = quantity;
    this.price = price;
  }
}

class Invoice {
  constructor({ reference, issued, created, customer, lines, rate, note = null }) {
    this.reference = reference;
    this.issued = issued;
    this.created = created;
    this.customer = customer;
    this.lines = lines;
    this.rate = rate;
    this.note = note;
  }

  // The template calls the reference "ref", so toJSON() renames it.
  toJSON() {
    const { reference, ...fields } = this;
    return { ref: reference, ...fields };
  }
}

function buildInvoice() {
  return new Invoice({
    reference: 'INV-2026-0042',
    issued: new Date(Date.UTC(2026, 9, 8)),
    created: new Date(Date.UTC(2026, 9, 8, 9, 30)),
    customer: new Customer('Example Ltd', 'accounts@example-ltd.example'),
    lines: [
      new Line('Paper clips, box of 100', 3, new Decimal('19.99')),
      new Line('Postage, per letter', 12, new Decimal('0.10')),
      new Line('Annual support', 1, new Decimal('1E+3')),
    ],
    rate: new Decimal('1.1523456789012345678901234567'),
  });
}

const client = new ETCHClient();

const invoice = buildInvoice();
const result = await client.process({ template, data: invoice });

console.log(result.data.toString('utf8'));
const out = path.join(outputDir, `invoice-${invoice.reference}.md`);
await result.saveToFile(out);
console.log(`Wrote ${path.relative(root, out)}`);
