alter table storico add column contrassegno boolean not null default false;
update storico set contrassegno = true where cassa = 'Contrassegno'
alter table storico drop column cassa;
alter table storico alter column invio_spedizione not null;
alter table storico alter column numero not null;
alter table storico alter column stato_storico not null;
alter table storico alter column tipo_abbonamento_rivista not null;

alter table abbonamento add column contrassegno boolean not null default false;
update abbonamento set contrassegno = true where cassa = 'Contrassegno'
alter table abbonamento drop column cassa;
create unique index abb_idx_select on abbonamento (intestatario_id, campagna_id, contrassegno);
