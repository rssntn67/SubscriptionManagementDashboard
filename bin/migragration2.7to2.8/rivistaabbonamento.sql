ALTER TABLE estratto_conto RENAME TO rivista_abbonamento;
ALTER TABLE rivista_abbonamento RENAME COLUMN tipo_estratto_conto TO tipo_abbonamento_rivista;
ALTER TABLE storico RENAME COLUMN tipo_estratto_conto TO tipo_abbonamento_rivista;

