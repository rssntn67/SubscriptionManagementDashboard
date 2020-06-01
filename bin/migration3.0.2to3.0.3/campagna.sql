alter table campagna add column numero integer not null default 10;
alter table campagna alter column anno set not null;
alter table campagna alter column stato_campagna set not null;