-- Creates the two sample tables the demos use, ORDERS and PRODUCTS, in your
-- current schema, with made-up data: 20 products and 40 orders.
--
--   SQL> @sql/setup.sql
--   SQL> script sql/load-images.js      (loads the product photos; needs the JavaScript
--                                        engine from step 1 of the README)
--
-- The user needs CREATE TABLE and CREATE SEQUENCE (ORDERS has an identity
-- column), and quota on its default tablespace. If a privilege is missing, or
-- either table already exists, it stops (and SQLcl exits) before changing
-- anything. It never drops or alters an existing table. sql/teardown.sql removes
-- what this script made, even after a run that failed part of the way through.

whenever sqlerror exit failure
set serveroutput on
set feedback off

declare
  n       pls_integer;
  missing varchar2(100);
begin
  -- the privileges, held directly or through a role, before anything is created
  for p in (select column_value as name from sys.odcivarchar2list('CREATE TABLE', 'CREATE SEQUENCE')) loop
    select count(*) into n from session_privs
     where privilege in (p.name, replace(p.name, 'CREATE ', 'CREATE ANY '));
    if n = 0 then
      missing := missing || case when missing is not null then ' and ' end || p.name;
    end if;
  end loop;
  if missing is not null then
    raise_application_error(-20002,
      'setup.sql: this user is missing ' || missing || '. '
      || 'Nothing was created or changed. Ask your DBA to grant what is missing, then run setup.sql again.');
  end if;

  -- the two tables, and the names of the constraints and indexes created below
  select count(*) into n from (
    select object_name from user_objects
     where object_name in ('ORDERS', 'PRODUCTS', 'ORDERS_PK', 'PRODUCTS_PK', 'ORDERS_ORDER_NO_UK')
    union all
    select constraint_name from user_constraints
     where constraint_name in ('ORDERS_PK', 'PRODUCTS_PK', 'ORDERS_ORDER_NO_UK', 'ORDERS_PRODUCT_FK'));
  if n > 0 then
    raise_application_error(-20001,
      'setup.sql: ORDERS or PRODUCTS (or one of their key names) already exists in this schema. '
      || 'Nothing was created or changed. Run setup.sql in a schema without them, or use the tables you have.');
  end if;
end;
/

create table products (
  product_id    number         constraint products_pk primary key,
  product_name  varchar2(100)  not null,
  category      varchar2(50),
  price         number(10,2),
  image         blob,
  mime_type     varchar2(100),
  filename      varchar2(200)
);
-- Commented straight away, so that teardown.sql recognises the table even if a
-- later statement fails.
comment on table products is 'ETCH SQLcl example: sample products, with a photo in IMAGE';

create table orders (
  order_id      number generated always as identity constraint orders_pk primary key,
  order_no      varchar2(20)   not null constraint orders_order_no_uk unique,
  customer      varchar2(100)  not null,
  email         varchar2(100),
  product_id    number         not null constraint orders_product_fk references products,
  quantity      number         not null,
  order_date    date           not null,
  status        varchar2(20)   not null,
  total         number(10,2)
);
comment on table orders is 'ETCH SQLcl example: sample orders, one product each';

-- 20 products, the ones ET007 shows. FILENAME names the photo in
-- integrations/sqlcl/images, and sql/load-images.js reads it into IMAGE.
insert into products (product_id, product_name, category, price, mime_type, filename) values (1, 'Studio Headphones', 'Audio', 149.00, 'image/jpeg', 'aud-001.jpg');
insert into products (product_id, product_name, category, price, mime_type, filename) values (2, 'Wireless Earbuds', 'Audio', 89.00, 'image/jpeg', 'aud-002.jpg');
insert into products (product_id, product_name, category, price, mime_type, filename) values (3, 'Bluetooth Speaker', 'Audio', 59.00, 'image/jpeg', 'aud-003.jpg');
insert into products (product_id, product_name, category, price, mime_type, filename) values (4, 'Studio Microphone', 'Audio', 119.00, 'image/jpeg', 'aud-004.jpg');
insert into products (product_id, product_name, category, price, mime_type, filename) values (5, 'Ultrabook 14', 'Computing', 1299.00, 'image/jpeg', 'cmp-001.jpg');
insert into products (product_id, product_name, category, price, mime_type, filename) values (6, 'All-in-One Desktop', 'Computing', 1499.00, 'image/jpeg', 'cmp-002.jpg');
insert into products (product_id, product_name, category, price, mime_type, filename) values (7, 'Mechanical Keyboard', 'Computing', 99.00, 'image/jpeg', 'cmp-003.jpg');
insert into products (product_id, product_name, category, price, mime_type, filename) values (8, 'Wireless Mouse', 'Computing', 39.00, 'image/jpeg', 'cmp-004.jpg');
insert into products (product_id, product_name, category, price, mime_type, filename) values (9, 'Laptop Backpack', 'Accessories', 79.00, 'image/jpeg', 'acc-001.jpg');
insert into products (product_id, product_name, category, price, mime_type, filename) values (10, 'USB-C Hub', 'Accessories', 49.00, 'image/jpeg', 'acc-002.jpg');
insert into products (product_id, product_name, category, price, mime_type, filename) values (11, 'Power Bank', 'Accessories', 45.00, 'image/jpeg', 'acc-003.jpg');
insert into products (product_id, product_name, category, price, mime_type, filename) values (12, 'Charging Cable', 'Accessories', 15.00, 'image/jpeg', 'acc-004.jpg');
insert into products (product_id, product_name, category, price, mime_type, filename) values (13, 'Smartwatch', 'Wearables', 249.00, 'image/jpeg', 'wea-001.jpg');
insert into products (product_id, product_name, category, price, mime_type, filename) values (14, 'Fitness Band', 'Wearables', 49.00, 'image/jpeg', 'wea-002.jpg');
insert into products (product_id, product_name, category, price, mime_type, filename) values (15, 'Wireless Headphones', 'Audio', 199.00, 'image/jpeg', 'aud-005.jpg');
insert into products (product_id, product_name, category, price, mime_type, filename) values (16, 'VR Headset', 'Wearables', 399.00, 'image/jpeg', 'wea-004.jpg');
insert into products (product_id, product_name, category, price, mime_type, filename) values (17, 'Rangefinder Camera', 'Photography', 899.00, 'image/jpeg', 'pho-001.jpg');
insert into products (product_id, product_name, category, price, mime_type, filename) values (18, 'Camera Tripod', 'Photography', 69.00, 'image/jpeg', 'pho-002.jpg');
insert into products (product_id, product_name, category, price, mime_type, filename) values (19, 'Action Camera', 'Photography', 229.00, 'image/jpeg', 'pho-003.jpg');
insert into products (product_id, product_name, category, price, mime_type, filename) values (20, 'Drone', 'Photography', 549.00, 'image/jpeg', 'pho-004.jpg');

-- 40 orders from 20 customers, as in ET007, three of them overdue. Every name is
-- made up.
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0001', 'Arjun Example', 'arjun.example@example.com', 12, 2, date '2026-07-29', 'Delivered', 30.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0002', 'Fatima Placeholder', 'fatima.placeholder@example.com', 3, 3, date '2026-08-01', 'Delivered', 177.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0003', 'Łukasz Example', 'lukasz.example@example.com', 14, 1, date '2026-08-02', 'Delivered', 49.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0004', 'Yuki Placeholder', 'yuki.placeholder@example.com', 5, 2, date '2026-08-05', 'Delivered', 2598.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0005', 'Olu Demo', 'olu.demo@example.com', 16, 3, date '2026-08-06', 'Delivered', 1197.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0006', 'Mei Placeholder', 'mei.placeholder@example.com', 7, 1, date '2026-08-09', 'Delivered', 99.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0007', 'Søren Demo', 'soren.demo@example.com', 18, 2, date '2026-08-10', 'Delivered', 138.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0008', 'Ingrid Testcase', 'ingrid.testcase@example.com', 9, 3, date '2026-08-13', 'Delivered', 237.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0009', 'Kwame Demo', 'kwame.demo@example.com', 20, 1, date '2026-08-14', 'Overdue', 549.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0010', 'Tomáš Testcase', 'tomas.testcase@example.com', 11, 2, date '2026-08-17', 'Delivered', 90.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0011', 'Mateo Mockup', 'mateo.mockup@example.com', 2, 3, date '2026-08-18', 'Delivered', 267.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0012', 'Zoë Testcase', 'zoe.testcase@example.com', 13, 1, date '2026-08-21', 'Delivered', 249.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0013', 'Amara Mockup', 'amara.mockup@example.com', 4, 2, date '2026-08-22', 'Delivered', 238.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0014', 'Aiyana Sample', 'aiyana.sample@example.com', 15, 3, date '2026-08-25', 'Delivered', 597.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0015', 'José Mockup', 'jose.mockup@example.com', 6, 1, date '2026-08-26', 'Delivered', 1499.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0016', 'Chiara Sample', 'chiara.sample@example.com', 17, 2, date '2026-08-29', 'Delivered', 1798.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0017', 'Ravi Example', 'ravi.example@example.com', 8, 3, date '2026-08-30', 'Delivered', 117.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0018', 'Agnieszka Sample', 'agnieszka.sample@example.com', 19, 1, date '2026-09-02', 'Overdue', 229.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0019', 'Hiroshi Example', 'hiroshi.example@example.com', 10, 2, date '2026-09-03', 'Delivered', 98.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0020', 'Priya Sample', 'priya.sample@example.com', 1, 3, date '2026-09-06', 'Delivered', 447.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0021', 'Arjun Example', 'arjun.example@example.com', 12, 1, date '2026-09-07', 'Delivered', 15.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0022', 'Fatima Placeholder', 'fatima.placeholder@example.com', 3, 2, date '2026-09-10', 'Delivered', 118.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0023', 'Łukasz Example', 'lukasz.example@example.com', 14, 3, date '2026-09-11', 'Delivered', 147.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0024', 'Yuki Placeholder', 'yuki.placeholder@example.com', 5, 1, date '2026-09-14', 'Delivered', 1299.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0025', 'Olu Demo', 'olu.demo@example.com', 16, 2, date '2026-09-15', 'Delivered', 798.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0026', 'Mei Placeholder', 'mei.placeholder@example.com', 7, 3, date '2026-09-18', 'Delivered', 297.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0027', 'Søren Demo', 'soren.demo@example.com', 18, 1, date '2026-09-19', 'Overdue', 69.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0028', 'Ingrid Testcase', 'ingrid.testcase@example.com', 9, 2, date '2026-09-22', 'Delivered', 158.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0029', 'Kwame Demo', 'kwame.demo@example.com', 20, 3, date '2026-09-23', 'Delivered', 1647.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0030', 'Tomáš Testcase', 'tomas.testcase@example.com', 11, 1, date '2026-09-26', 'Delivered', 45.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0031', 'Mateo Mockup', 'mateo.mockup@example.com', 2, 2, date '2026-09-27', 'Delivered', 178.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0032', 'Zoë Testcase', 'zoe.testcase@example.com', 13, 3, date '2026-09-30', 'Delivered', 747.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0033', 'Amara Mockup', 'amara.mockup@example.com', 4, 1, date '2026-10-01', 'Delivered', 119.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0034', 'Aiyana Sample', 'aiyana.sample@example.com', 15, 2, date '2026-10-04', 'Delivered', 398.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0035', 'José Mockup', 'jose.mockup@example.com', 6, 3, date '2026-10-05', 'Shipped', 4497.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0036', 'Chiara Sample', 'chiara.sample@example.com', 17, 1, date '2026-10-08', 'Shipped', 899.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0037', 'Ravi Example', 'ravi.example@example.com', 8, 2, date '2026-10-09', 'Shipped', 78.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0038', 'Agnieszka Sample', 'agnieszka.sample@example.com', 19, 3, date '2026-10-12', 'Processing', 687.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0039', 'Hiroshi Example', 'hiroshi.example@example.com', 10, 1, date '2026-10-13', 'Processing', 49.00);
insert into orders (order_no, customer, email, product_id, quantity, order_date, status, total) values ('DEMO-0040', 'Priya Sample', 'priya.sample@example.com', 1, 2, date '2026-10-16', 'Processing', 298.00);

commit;

begin
  dbms_stats.gather_table_stats(user, 'PRODUCTS');
  dbms_stats.gather_table_stats(user, 'ORDERS');
end;
/

prompt setup.sql: created PRODUCTS (20 rows) and ORDERS (40 rows).
prompt Next: script sql/load-images.js   to load the product photos.
whenever sqlerror continue none
set feedback on
