update rivista_abbonamento set stato_abbonamento = 'Valido' where stato_abbonamento='ValidoConResiduo';
update rivista_abbonamento set stato_abbonamento = 'Valido' where stato_abbonamento='ValidoInviatoEC';
update rivista_abbonamento set stato_abbonamento = 'InviatoEC' where stato_abbonamento='SospesoInviatoEC';
update rivista_abbonamento set stato_abbonamento = 'Valido' where stato_abbonamento='Duplicato';
delete from spedizione_item where rivista_abbonamento_id in (select id from rivista_abbonamento where stato_abbonamento = 'Annullato');
delete from rivista_abbonamento where stato_abbonamento = 'Annullato';

CREATE TABLE documenti_trasporto_cumulati (
id bigint PRIMARY KEY,
anno varchar(255) unique not null,
importo numeric(19,2) not null
);

CREATE TABLE documento_trasporto (
id bigint PRIMARY KEY,
data timestamp without time zone not null,
ddt varchar(255) not null,
importo numeric(19,2) not null,
operatore varchar(255) not null,
stato_operazione_incasso varchar(255) not null,
committente_id bigint not null,
documenti_trasporto_cumulati_id bigint not null,
versamento_id bigint not null,
CONSTRAINT  ddt_comm_id 
   FOREIGN KEY (committente_id)
   REFERENCES anagrafica (id),
CONSTRAINT  ddt_offe_cumu_id 
   FOREIGN KEY (documenti_trasporto_cumulati_id)
   REFERENCES documenti_trasporto_cumulati (id),
CONSTRAINT  ddt_versamento_id 
   FOREIGN KEY (versamento_id)
   REFERENCES versamento (id)
);