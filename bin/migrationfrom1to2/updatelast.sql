drop table temp_operazione_incasso;
alter table abbonamento drop column ccp;
alter table abbonamento drop column versamento_id;
alter table versamento drop column operazione;

UPDATE spedizione set stato_spedizione ='INVIATA' where anno_spedizione = 'ANNO2019';