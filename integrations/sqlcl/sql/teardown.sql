-- Drops the ORDERS and PRODUCTS tables that sql/setup.sql created, and nothing else.
--
--   SQL> @sql/teardown.sql
--
-- It drops a table only if it carries setup.sql's comment ('ETCH SQLcl example: ...'),
-- so tables of your own with the same names are left alone.

set serveroutput on
set feedback off

declare
  procedure drop_if_ours(p_table varchar2) is
    l_comment user_tab_comments.comments%type;
  begin
    select comments into l_comment from user_tab_comments where table_name = p_table;
    if l_comment like 'ETCH SQLcl example:%' then
      execute immediate 'drop table ' || p_table || ' purge';
      dbms_output.put_line('teardown.sql: dropped ' || p_table);
    else
      dbms_output.put_line('teardown.sql: left ' || p_table || ' alone: setup.sql did not create it');
    end if;
  exception
    when no_data_found then
      dbms_output.put_line('teardown.sql: ' || p_table || ' does not exist');
  end;
begin
  drop_if_ours('ORDERS');   -- first: it references PRODUCTS
  drop_if_ours('PRODUCTS');
end;
/

set feedback on
