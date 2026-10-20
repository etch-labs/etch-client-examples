-- One JSON document for templates/schema.pptx: every table in your schema, with
-- its comment and its DBMS_DEVELOPER.GET_METADATA description (columns,
-- constraints, indexes and statistics).
--   {"schema", "generated",
--    "tables": [{"comment", "metadata": <GET_METADATA for the table>}, ...]}
--
--   SQL> etch templates/schema.pptx schema.pptx @sql/schema.sql
--
-- DBMS_DEVELOPER is in recent Oracle Database releases (tested on 26ai, 23.26.1;
-- check yours with `desc dbms_developer`). Without
-- json_serialize(... returning clob) a larger schema fails with ORA-40478.
-- To document only some tables, change the where clause, for example:
--   where t.table_name in ('ORDERS', 'PRODUCTS')
select json_serialize(
         json_object(
           'schema'    value user,
           'generated' value to_char(sysdate, 'YYYY-MM-DD'),
           'tables'    value json_arrayagg(
                          json_object(
                            'comment'  value c.comments,
                            'metadata' value dbms_developer.get_metadata(name => t.table_name, level => 'ALL')
                            returning clob)
                          order by t.table_name returning clob)
           returning clob)
         returning clob) as doc
from user_tables t
left join user_tab_comments c on c.table_name = t.table_name
where t.table_name not like 'DBTOOLS$%'
