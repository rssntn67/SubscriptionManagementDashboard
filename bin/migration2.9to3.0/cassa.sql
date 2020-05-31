alter table storico add column contrassegno boolean not null default false;
update storico set contrassegno = true where cassa = 'Contrassegno';
alter table storico drop column cassa;

alter table storico alter column invio_spedizione set not null;
alter table storico alter column numero set not null;
alter table storico alter column stato_storico set not null;
alter table storico alter column tipo_abbonamento_rivista set not null;

update spedizione set abbonamento_id = 192737 where abbonamento_id = 172361;
update rivista_abbonamento set abbonamento_id = 192737 where abbonamento_id = 172361;
update operazione_incasso set abbonamento_id = 192737 where abbonamento_id = 172361;
delete from abbonamento a where a.id = 172361;

update spedizione set abbonamento_id = 211143 where abbonamento_id = 211122;
update rivista_abbonamento set abbonamento_id = 211143 where abbonamento_id = 211122;
update operazione_incasso set abbonamento_id = 211143 where abbonamento_id = 211122;
update spedizione set abbonamento_id = 211143 where abbonamento_id = 214083;
update rivista_abbonamento set abbonamento_id = 211143 where abbonamento_id = 214083;
update operazione_incasso set abbonamento_id = 211143 where abbonamento_id = 214083;
update spedizione set abbonamento_id = 211143 where abbonamento_id = 214133;
update rivista_abbonamento set abbonamento_id = 211143 where abbonamento_id = 214133;
update operazione_incasso set abbonamento_id = 211143 where abbonamento_id = 214133;
update spedizione set abbonamento_id = 211143 where abbonamento_id = 214102;
update rivista_abbonamento set abbonamento_id = 211143 where abbonamento_id = 214102;
update operazione_incasso set abbonamento_id = 211143 where abbonamento_id = 214102;
update spedizione set abbonamento_id = 211143 where abbonamento_id = 214152;
update rivista_abbonamento set abbonamento_id = 211143 where abbonamento_id = 214152;
update operazione_incasso set abbonamento_id = 211143 where abbonamento_id = 214152;
delete from abbonamento a where a.id = 214152;
delete from abbonamento a where a.id = 214102;
delete from abbonamento a where a.id = 214133;
delete from abbonamento a where a.id = 214083;
delete from abbonamento a where a.id = 211122;
update abbonamento set importo=112.95, spese=11.00 where id = 211143;


update spedizione set abbonamento_id = 209825 where abbonamento_id = 78368;
update rivista_abbonamento set abbonamento_id = 209825 where abbonamento_id = 78368;
update operazione_incasso set abbonamento_id = 209825 where abbonamento_id = 78368;
update abbonamento set importo=320.00, incassato=325.50, spese=5.50 where id = 209825;
delete from abbonamento a where a.id = 78368;

update spedizione set abbonamento_id = 79512 where abbonamento_id = 200143;
update rivista_abbonamento set abbonamento_id = 79512 where abbonamento_id = 200143;
update operazione_incasso set abbonamento_id = 79512 where abbonamento_id = 200143;
update abbonamento set importo=54.00, incassato=54.00 where id = 79512;
delete from abbonamento a where a.id = 200143;

update spedizione set abbonamento_id = 83639 where abbonamento_id = 196356;
update rivista_abbonamento set abbonamento_id = 83639 where abbonamento_id = 196356;
update operazione_incasso set abbonamento_id = 83639 where abbonamento_id = 196356;
update abbonamento set importo=66.00, incassato=66.00,pregresso=0.00 where id = 83639;
delete from abbonamento a where a.id = 196356;

update spedizione set abbonamento_id = 92069 where abbonamento_id = 211181;
update rivista_abbonamento set abbonamento_id = 92069 where abbonamento_id = 211181;
update operazione_incasso set abbonamento_id = 92069 where abbonamento_id = 211181;
update abbonamento set importo=80.00, incassato=70.00,spese=5.50 where id = 92069;
delete from abbonamento a where a.id = 211181;

update spedizione set abbonamento_id = 192754 where abbonamento_id = 103132;
update rivista_abbonamento set abbonamento_id = 192754 where abbonamento_id = 103132;
update operazione_incasso set abbonamento_id = 192754 where abbonamento_id = 103132;
delete from abbonamento a where a.id = 103132;

update spedizione set abbonamento_id = 128465 where abbonamento_id = 200106;
update rivista_abbonamento set abbonamento_id = 128465 where abbonamento_id = 200106;
update operazione_incasso set abbonamento_id = 128465 where abbonamento_id = 200106;
update abbonamento set importo=73.00, incassato=78.50,spese=5.50 where id = 128465;
delete from abbonamento a where a.id = 200106;

update spedizione set abbonamento_id = 141696 where abbonamento_id = 215329;
update rivista_abbonamento set abbonamento_id = 141696 where abbonamento_id = 215329;
update operazione_incasso set abbonamento_id = 141696 where abbonamento_id = 215329;
update abbonamento set stato_abbonamento='Valido',importo=45.00, incassato=147.00,spese_estero=86.00,spese=16.00,spese_estratto_conto=0.00,pregresso=0.00 where id = 141696;
delete from abbonamento a where a.id = 215329;

alter table abbonamento add column contrassegno boolean not null default false;
update abbonamento set contrassegno = true where cassa = 'Contrassegno';
alter table abbonamento drop column cassa;

create unique index abb_idx_select on abbonamento (intestatario_id, campagna_id, contrassegno);
