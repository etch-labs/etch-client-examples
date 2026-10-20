# 12 QR codes and barcodes

This example renders `templates/shipping-label.docx`, a shipping label with a packing slip, with the
data in `data/shipping.json`. The program is the same as the quick start; the template turns values
from the data into codes that scan:

| In the template | What it draws |
|---|---|
| `{{#barcode type="code128" width="340" height="80" shipment.tracking}}` | A Code 128 barcode of the tracking number, `SMP-QX-0815001` |
| `{{#qr shipment.trackingUrl size="110"}}` | A QR code of the tracking page's address |
| `{{#barcode type="ean13" width="190" height="60" ean}}` | An EAN-13 product barcode on each line of the packing slip |

Each tag takes a path into the data (not a quoted value) and is replaced by a picture of the code.
`size` sets a QR code's side; `width` and `height` size a barcode. Code 128 encodes any text, while
EAN-13 takes a 12-digit number (the check digit is added) or a 13-digit one (the check digit is
verified).

The label also prints the tracking number and each product number as text next to its code, for
anyone who can't scan it. The product numbers start with `20`, a range kept for in-store use, so
they can't belong to a real product.

To learn more about QR codes and barcodes, see Special tags under Template syntax in the
[ETCH documentation](https://get-etch.app/docs).

The code is in [index.mjs](index.mjs).

## Run

From the `javascript` folder, after following [INSTALL.md](../INSTALL.md):

```bash
node 12-qr-codes-and-barcodes/index.mjs
```

The example writes the document to `12-qr-codes-and-barcodes/output/`.
