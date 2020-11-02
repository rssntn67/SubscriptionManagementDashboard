alter table abbonamento add column inviatoec boolean;
update abbonamento set inviatoec = false;
alter table abbonamento alter column inviatoec set not null;


alter table abbonamento add column sollecitato boolean;
update abbonamento set sollecitato = false;
alter table abbonamento alter column sollecitato set not null;

alter table abbonamento add column stato_abbonamento varchar(255);

alter table rivista_abbonamento add column stato_rivista varchar(255);

update abbonamento set inviatoec = true where id in (select abbonamento_id from rivista_abbonamento where stato_abbonamento = 'InviatoEC');
update abbonamento set sollecitato = true where id in (select abbonamento_id from rivista_abbonamento where stato_abbonamento = 'Sollecitato');
update rivista_abbonamento set stato_rivista = 'Attiva' where stato_abbonamento = 'Proposto';
update rivista_abbonamento set stato_rivista = 'Attiva' where stato_abbonamento = 'Sollecitato';
update rivista_abbonamento set stato_rivista = 'Attiva' where stato_abbonamento = 'Nuovo';
update rivista_abbonamento set stato_rivista = 'Attiva' where stato_abbonamento = 'Valido';
update rivista_abbonamento set stato_rivista = 'Sospesa' where stato_abbonamento = 'Sospeso';
update rivista_abbonamento set stato_rivista = 'Sospesa' where stato_abbonamento = 'InviatoEC';


update abbonamento set stato_abbonamento = 'Valido' where importo+spese+spese_estero+spese_estratto_conto+pregresso-incassato<=0;
update abbonamento set stato_abbonamento = 'Valido' where importo+spese+spese_estero+spese_estratto_conto+pregresso>=70 and (importo+spese+spese_estero+spese_estratto_conto+pregresso)*0.8-incassato<=0 and stato_abbonamento is null;
update abbonamento set stato_abbonamento = 'Valido' where importo+spese+spese_estero+spese_estratto_conto+pregresso<70 and importo+spese+spese_estero+spese_estratto_conto+pregresso-7-incassato<0 and stato_abbonamento is null;

update abbonamento set stato_abbonamento = 'Proposto' 
where id in 
( select a.id from abbonamento a where a.stato_abbonamento is null
 	and a.id in 
 	(select abbonamento_id from rivista_abbonamento r 
      where abbonamento_id = a.id and stato_rivista='Attiva') 
	and a.id not in 
  (select abbonamento_id from rivista_abbonamento r 
     where abbonamento_id = a.id and stato_rivista='Sospesa'));
     
update abbonamento set stato_abbonamento = 'ParzialmenteSospeso' 
where id in 
( select a.id from abbonamento a where a.stato_abbonamento is null
 	and a.id in 
 	(select abbonamento_id from rivista_abbonamento r 
      where abbonamento_id = a.id and stato_rivista='Attiva'));

      
update abbonamento set stato_abbonamento = 'Sospeso' where stato_abbonamento is null;

alter table rivista_abbonamento drop column stato_abbonamento;