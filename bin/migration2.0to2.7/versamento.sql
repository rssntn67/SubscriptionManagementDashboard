ALTER TABLE versamento 
ADD COLUMN committente_id bigint;
ALTER TABLE versamento 
ADD CONSTRAINT fkdmn92jhrpgc4vpsrn1symqj0o 
FOREIGN KEY (committente_id) 
REFERENCES anagrafica(id);