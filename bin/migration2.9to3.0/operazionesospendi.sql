CREATE TABLE operazione_sospendi (
id bigint PRIMARY KEY,
anno varchar(255) not null,
data timestamp without time zone not null,
mese_spedizione varchar(255) not null,
operatore varchar(255) not null,
pubblicazione_id bigint not null,
UNIQUE (anno,pubblicazione_id),
CONSTRAINT  fktd8vxarfqw60w2s0r4nvb5emj 
   FOREIGN KEY (pubblicazione_id)
   REFERENCES pubblicazione (id)
);