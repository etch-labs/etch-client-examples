# Invoice {{ref}}

Issued {{issued | dateFormat:"D MMMM YYYY"}} to {{customer.name}}, {{customer.email}}.

| Description | Quantity | Price |
|---|---:|---:|
{{#each lines}}
| {{description}} | {{quantity}} | {{price}} |
{{/each}}

Prices are in pounds. One pound is {{rate}} euros.

{{#if note}}Note: {{note}}{{else}}There is no note on this invoice.{{/if}}

Created {{created | dateFormat:"D MMMM YYYY, HH:mm"}}.
