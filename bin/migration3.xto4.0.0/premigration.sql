create table operazione_campagna (
id bigint PRIMARY KEY,
data timestamp without time zone not null,
operatore varchar(255) not null,
stato varchar(255) not null,
campagna_id bigint not null,
UNIQUE (stato,campagna_id),
CONSTRAINT  operazione_campagna_fkey 
   FOREIGN KEY (campagna_id)
   REFERENCES campagna (id)
);

drop table operazione_sospendi;

CREATE TABLE operazione_sospendi (
id bigint PRIMARY KEY,
data timestamp without time zone not null,
mese_spedizione varchar(255) not null,
operatore varchar(255) not null,
campagna_id bigint not null,
pubblicazione_id bigint not null,
UNIQUE (campagna_id,pubblicazione_id),
CONSTRAINT  operazione_sospendi_fk1 
   FOREIGN KEY (pubblicazione_id)
   REFERENCES pubblicazione (id),
CONSTRAINT  operazione_sospendi_fk2 
   FOREIGN KEY (campagna_id)
   REFERENCES campagna (id)

);

alter table rivista_abbonamento add column stato_abbonamento varchar(255);
update rivista_abbonamento set stato_abbonamento = 'Valido' where tipo_abbonamento_rivista like 'Omaggio%';
update rivista_abbonamento set invio_spedizione = 'AdpSedeNoSpese' where invio_spedizione='AdpSede' and tipo_abbonamento_rivista like 'Omaggio%';
update rivista_abbonamento ra set stato_abbonamento = a.stato_abbonamento from abbonamento a WHERE ra.stato_abbonamento is null and a.id = ra.abbonamento_id;


alter table spedizione_item add column stato_spedizione varchar(255);

update spedizione_item si set stato_spedizione = 'PROGRAMMATA' where anno_pubblicazione = 'ANNO2021';

update spedizione_item si set stato_spedizione = s.stato_spedizione from spedizione s where si.stato_spedizione is null and s.id=si.spedizione_id;

update spedizione_item set stato_spedizione = 'INVIATA' 
where id in (SELECT si.id from spedizione_item si left join rivista_abbonamento ra on ra.id=si.rivista_abbonamento_id 
where tipo_abbonamento_rivista like 'Omaggio%' 
and stato_spedizione='SOSPESA' 
and anno_pubblicazione='ANNO2020' 
and mese_pubblicazione NOT IN ('NOVEMBRE','DICEMBRE') 
and si.pubblicazione_id = 2);

update spedizione_item set stato_spedizione = 'PROGRAMMATA' 
where id in (SELECT si.id from spedizione_item si left join rivista_abbonamento ra on ra.id=si.rivista_abbonamento_id 
where tipo_abbonamento_rivista like 'Omaggio%' 
and stato_spedizione='SOSPESA' 
and anno_pubblicazione='ANNO2020' 
and mese_pubblicazione IN ('NOVEMBRE','DICEMBRE') 
and si.pubblicazione_id = 2);

update spedizione_item set stato_spedizione = 'INVIATA' 
where id in (SELECT si.id from spedizione_item si left join rivista_abbonamento ra on ra.id=si.rivista_abbonamento_id 
where tipo_abbonamento_rivista like 'Omaggio%' 
and stato_spedizione='SOSPESA' 
and anno_pubblicazione='ANNO2020' 
and mese_pubblicazione NOT IN ('NOVEMBRE','DICEMBRE') 
and si.pubblicazione_id = 3);

update spedizione_item set stato_spedizione = 'PROGRAMMATA' 
where id in (SELECT si.id from spedizione_item si left join rivista_abbonamento ra on ra.id=si.rivista_abbonamento_id 
where tipo_abbonamento_rivista like 'Omaggio%' 
and stato_spedizione='SOSPESA' 
and anno_pubblicazione='ANNO2020' 
and mese_pubblicazione IN ('NOVEMBRE','DICEMBRE') 
and si.pubblicazione_id = 3);

update spedizione_item set stato_spedizione = 'INVIATA' 
where id in (SELECT si.id from spedizione_item si left join rivista_abbonamento ra on ra.id=si.rivista_abbonamento_id 
where tipo_abbonamento_rivista like 'Omaggio%' 
and stato_spedizione='SOSPESA' 
and anno_pubblicazione='ANNO2020' 
and si.pubblicazione_id = 4);

update spedizione_item set stato_spedizione = 'INVIATA' 
where id in (SELECT si.id from spedizione_item si left join rivista_abbonamento ra on ra.id=si.rivista_abbonamento_id 
where tipo_abbonamento_rivista like 'Omaggio%' 
and stato_spedizione='SOSPESA' 
and anno_pubblicazione='ANNO2020' 
and si.pubblicazione_id = 5);

alter table abbonamento drop column stato_abbonamento;
alter table spedizione drop column stato_spedizione;

