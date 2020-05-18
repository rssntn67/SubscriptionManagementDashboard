CREATE TABLE offerte_cumulate (
id bigint PRIMARY KEY,
anno varchar(255) unique not null,
importo numeric(19,2) not null
);

CREATE TABLE offerta (
id bigint PRIMARY KEY,
data timestamp without time zone not null,
importo numeric(19,2) not null,
operatore varchar(255) not null,
stato_operazione_incasso varchar(255) not null,
committente_id bigint not null,
offerte_cumulate_id bigint not null,
versamento_id bigint not null,
CONSTRAINT  fk755k0q54ere3a7qdiaupicuhw 
   FOREIGN KEY (committente_id)
   REFERENCES anagrafica (id),
CONSTRAINT  fkjnjjlhr48wb98sy5it57y5l5g 
   FOREIGN KEY (offerte_cumulate_id)
   REFERENCES offerte_cumulate (id),
CONSTRAINT  fk8agi1ks0q7h9v3kqva34q4cb 
   FOREIGN KEY (versamento_id)
   REFERENCES versamento (id)
);