ALTER TABLE incasso RENAME TO distinta_versamento;
ALTER TABLE versamento RENAME COLUMN incasso_id TO distinta_versamento_id;

