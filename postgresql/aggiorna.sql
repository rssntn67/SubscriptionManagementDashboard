alter table anagrafica drop column co_id;
alter table anagrafica alter COLUMN area_spedizione set not null;
alter table anagrafica alter COLUMN code_line_base set not null ;
alter table anagrafica alter COLUMN denominazione set not null ;
alter table anagrafica alter COLUMN diocesi set not null ;
alter table anagrafica alter COLUMN nome set not null ;
alter table anagrafica alter COLUMN provincia set not null ;

alter table abbonamento  drop column stato_abbonamento;
alter table abbonamento  alter COLUMN anno set not null ;
alter table abbonamento  alter COLUMN code_line  set not null ;
alter table abbonamento  alter COLUMN data  set not null ;
alter table abbonamento  alter COLUMN importo  set not null ;
alter table abbonamento  alter COLUMN incassato  set not null ;
alter table abbonamento  alter COLUMN pregresso  set not null ;
alter table abbonamento  alter COLUMN spese set not null ;
alter table abbonamento  alter COLUMN spese_estero   set not null ;
alter table abbonamento  alter COLUMN spese_estratto_conto    set not null ;
alter table abbonamento add COLUMN spese_contrassegno numeric(19,2);
update abbonamento set spese_contrassegno = 0.00;
alter table abbonamento  alter COLUMN spese_contrassegno set not null ;
update abbonamento set spese = spese-4.50,spese_contrassegno = 4.50 where contrassegno = true and spese >=4.50;

alter table campagna add COLUMN contrassegno numeric(19,2);
alter table campagna add COLUMN limite_invio_estratto numeric(19,2);
alter table campagna add COLUMN limite_invio_sollecito numeric(19,2);
alter table campagna add COLUMN max_debito numeric(19,2);
alter table campagna add COLUMN min_perc_incassato numeric(19,2);
alter table campagna add COLUMN soglia_importo_totale numeric(19,2);
alter table campagna add COLUMN spese_estratto_conto numeric(19,2);
alter table campagna add COLUMN spese_sollecito numeric(19,2);

update campagna set contrassegno = 4.50,
limite_invio_estratto=7.00,
limite_invio_sollecito=7.00,
max_debito=7.00,
min_perc_incassato=0.80,
soglia_importo_totale = 70.00,
spese_estratto_conto=2.00,
spese_sollecito=2.00
WHERE anno= 'ANNO2020';

update campagna set contrassegno = 4.50,
limite_invio_estratto=7.00,
limite_invio_sollecito=7-00,
max_debito=7.00,
min_perc_incassato=0.80,
soglia_importo_totale = 70.00,
spese_estratto_conto=0.00,
spese_sollecito=2.00
WHERE anno= 'ANNO2021';

update campagna set contrassegno = 4.50,
limite_invio_estratto=8.00,
limite_invio_sollecito=8.00,
max_debito=7.00,
min_perc_incassato=0.80,
soglia_importo_totale = 70.00,
spese_estratto_conto=0.00,
spese_sollecito=2.00
WHERE anno= 'ANNO2022';

alter table campagna alter COLUMN contrassegno set not null;
alter table campagna alter COLUMN limite_invio_estratto set not null;
alter table campagna alter COLUMN limite_invio_sollecito set not null;
alter table campagna alter COLUMN max_debito set not null;
alter table campagna alter COLUMN min_perc_incassato set not null;
alter table campagna alter COLUMN soglia_importo_totale set not null;
alter table campagna alter COLUMN spese_estratto_conto set not null;
alter table campagna alter COLUMN spese_sollecito set not null;

alter table rivista_abbonamento alter COLUMN anno_inizio set not null;
alter table rivista_abbonamento alter COLUMN anno_fine set not null;
alter table rivista_abbonamento alter COLUMN importo set not null;
alter table rivista_abbonamento alter COLUMN invio_spedizione set not null;
alter table rivista_abbonamento alter COLUMN mese_fine set not null;
alter table rivista_abbonamento alter COLUMN mese_inizio set not null;
alter table rivista_abbonamento alter COLUMN numero set not null;
alter table rivista_abbonamento alter COLUMN numero_totale_riviste set not null;
alter table rivista_abbonamento alter COLUMN tipo_abbonamento_rivista set not null;
alter table rivista_abbonamento alter COLUMN numero_totale_riviste set not null;
alter table rivista_abbonamento alter COLUMN stato_rivista set not null;


alter table rivista_abbonamento RENAME TO rivista;
alter table spedizione_item RENAME COLUMN rivista_abbonamento_id TO rivista_id;