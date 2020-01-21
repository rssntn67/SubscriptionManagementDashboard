alter table abbonamento 
	add column spese_estratto_conto numeric(19,2);

update abbonamento set spese_estratto_conto = 0.00;

update abbonamento set stato_abbonamento = 'Proposto' where stato_abbonamento = 'Sospeso'

update spedizione set stato_spedizione = 'PROGRAMMATA' where stato_spedizione = 'SOSPESA';
create table operazione_incasso (
  id bigint not null,
  data timestamp without time zone,
  description varchar(255),
  importo numeric(19,2),
  operatore varchar(255),
  stato_operazione_incasso varchar(255),
  abbonamento_id bigint not null,
  versamento_id bigint not null,
  CONSTRAINT operazione_incasso_pkey primary key (id),
  CONSTRAINT fk37hdd5edcujeawvih8i9ivlbm FOREIGN KEY (abbonamento_id) REFERENCES abbonamento(id),
  CONSTRAINT fk5tkok0gn784dwmk6uq1ytcb0m FOREIGN KEY (versamento_id) REFERENCES versamento(id)
);

create table temp_operazione_incasso as 
select
   a.id as abbid,
   v.id as verid,
   v.incasso_id as incid,
   i.cassa,
   a.code_line as abbcode,
   v.code_line as vercode,
   v.operazione,
   a.importo+a.pregresso+a.spese+a.spese_estero as totale,
   a.incassato as abbinc ,
   v.importo as verimp,
   v.incassato as verinc
 from abbonamento a
 left join versamento v
 on a.code_line = v.code_line
 left join incasso i
 on i.id = v.incasso_id
 where v.id is not null
 and a.incassato > 0
 and v.incassato > 0;
 
 insert into temp_operazione_incasso (select
 a.id as abbid,
   v.id as verid,
   v.incasso_id as incid,
   i.cassa,
   a.code_line as abbcode,
   v.code_line as vercode,
   v.operazione,
   a.importo+a.pregresso+a.spese+a.spese_estero as totale,
   a.incassato as abbinc ,
   v.importo as verimp,
   v.incassato as verinc
 from abbonamento a
 left join versamento v
 on a.versamento_id = v.id
 left join incasso i
 on i.id = v.incasso_id
 where
   a.versamento_id is not null);
 
alter table temp_operazione_incasso add column vercount integer;
update temp_operazione_incasso set vercount = 1;

