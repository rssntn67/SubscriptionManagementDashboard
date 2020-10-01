update rivista_abbonamento set stato_abbonamento = 'Valido' where stato_abbonamento='ValidoConResiduo';
update rivista_abbonamento set stato_abbonamento = 'Valido' where stato_abbonamento='ValidoInviatoEC';
update rivista_abbonamento set stato_abbonamento = 'InviatoEC' where stato_abbonamento='SospesoInviatoEC';
update rivista_abbonamento set stato_abbonamento = 'Valido' where stato_abbonamento='Duplicato';
delete from spedizione_item where rivista_abbonamento_id in (select id from rivista_abbonamento where stato_abbonamento = 'Annullato');
delete from rivista_abbonamento where stato_abbonamento = 'Annullato';