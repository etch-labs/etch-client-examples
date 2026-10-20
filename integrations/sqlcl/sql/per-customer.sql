-- One JSON document for templates/per-customer.xlsx: a sheet per customer,
-- listing that customer's orders.
--   {"customers": [{"customer", "email", "orders": [{"order_no", "order_date",
--                   "product", "quantity", "status", "total"}, ...]}, ...]}
--
--   SQL> etch templates/per-customer.xlsx per-customer.xlsx @sql/per-customer.sql
select json_object(
         'customers' value json_arrayagg(
           json_object(
             'customer' value c.customer,
             'email'    value c.email,
             'orders'   value (
               select json_arrayagg(
                        json_object(
                          'order_no'   value o.order_no,
                          'order_date' value to_char(o.order_date, 'YYYY-MM-DD'),
                          'product'    value p.product_name,
                          'quantity'   value o.quantity,
                          'status'     value o.status,
                          'total'      value o.total)
                        order by o.order_date, o.order_no returning clob)
               from orders o
               join products p on p.product_id = o.product_id
               where o.customer = c.customer)
             returning clob)
           order by c.customer returning clob)
         returning clob) as doc
from (select customer, max(email) as email from orders group by customer) c
