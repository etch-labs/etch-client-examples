// Render a template written in the program instead of read from a file.
//
// The template is Markdown: an order confirmation of the kind an application
// might send as an email. Its name, confirmation.md, tells ETCH what format it
// is in, so the result is Markdown too. The program prints the result and saves
// it to a file.

import { readFile } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import path from 'node:path';
import { ETCHClient } from '@get-etch/etch-client';

const here = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(here, '..', '..');
const dataFile = path.join(root, 'data', 'order.json');
const outputDir = path.join(here, 'output');

const template = `# Order {{order.number}}

Hello {{customer.name}},

Thank you for your order of {{order.placed | dateFormat:"D MMMM YYYY"}}. It travels in {{summary.Parcels}} parcels:

{{#each shipments}}
## Parcel {{@index1}}, {{carrier}}

Arrives {{arrives | dateFormat:"dddd D MMMM"}}.

{{#each items}}
- {{qty}} x {{name}}
{{/each}}

{{/each}}
Delivering to {{customer.address}}.
`;

const client = new ETCHClient();

const data = JSON.parse(await readFile(dataFile, 'utf8'));
const result = await client.process({
  template: 'confirmation.md',
  templateContent: template,
  data,
});

console.log(result.data.toString('utf8'));
const out = path.join(outputDir, 'order-confirmation.md');
await result.saveToFile(out);
console.log(`Wrote ${path.relative(root, out)}`);
