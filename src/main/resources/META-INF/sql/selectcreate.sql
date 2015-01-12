--select * into copy from CHILD
create table /*$to*/ as select * from /*$from*/ where 0 = 1;
