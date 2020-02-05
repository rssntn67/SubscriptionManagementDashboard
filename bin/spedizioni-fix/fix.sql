delete from operazione;
update abbonamento set stato_abbonamento = 'Proposto' where stato_abbonamento != 'Valido';
update campagna set stato_campagna = 'Inviata';
update spedizione set stato_spedizione = 'PROGRAMMATA';



update spedizione_item set posticipata = false ;

delete from spedizione_item where mese_pubblicazione = 'GENNAIO';
delete from spedizione_item where mese_pubblicazione = 'FEBBRAIO';
delete from spedizione_item where mese_pubblicazione = 'MARZO';
delete from spedizione where mese_spedizione = 'OTTOBRE' and anno_spedizione = 'ANNO2019';
delete from spedizione where mese_spedizione = 'NOVEMBRE' and anno_spedizione = 'ANNO2019';
delete from spedizione where mese_spedizione = 'DICEMBRE' and anno_spedizione = 'ANNO2019';
delete from spedizione where mese_spedizione = 'GENNAIO' and anno_spedizione = 'ANNO2020';


//
select * from spedizione_item 
where mese_pubblicazione = 'APRILE' 
and anno_pubblicazione ='ANNO2020' 
and estratto_conto_id not in 
(select estratto_conto_id from spedizione_item 
  where mese_pubblicazione = 'GIUGNO'  
  and anno_pubblicazione ='ANNO2020');
// count ec 
select distinct estratto_conto_id,count(*) 
from spedizione_item 
where estratto_conto_id 
in (select ec.id from estratto_conto ec left join pubblicazione p on p.id = ec.pubblicazione_id where numero > 0 and nome='Messaggio') group by estratto_conto_id order by count;
// count spedizione  
select distinct
nome,posticipata,invio_spedizione,stato_spedizione, si.mese_pubblicazione, si.anno_pubblicazione,
count(*)                                                     
from pubblicazione p
left join spedizione_item si on si.pubblicazione_id = p.id
left join spedizione s on si.spedizione_id = s.id
where mese_spedizione = 'MARZO'
and anno_spedizione='ANNO2020'
group by nome,posticipata,invio_spedizione,stato_spedizione, si.mese_pubblicazione, si.anno_pubblicazione
order by nome,mese_pubblicazione;  



// checking Estratti con ec.numero > 0 e senza spedizione
select a.id as abb_id,ec.numero,stato_abbonamento,code_line_base,denominazione,code_line 
from anagrafica an 
left join abbonamento a on an.id = a.intestatario_id 
left join  estratto_conto ec on a.id=ec.abbonamento_id  
where ec.id 
in (
select id from estratto_conto 
where pubblicazione_id = 5 
and numero >0 
and id not in (select estratto_conto_id from spedizione_item where pubblicazione_id = 5));

// checking Estratti con ec.numero = 0 e senza spedizione
select a.id as abb_id,ec.numero,stato_abbonamento,code_line_base,denominazione,code_line 
from anagrafica an 
left join abbonamento a on an.id = a.intestatario_id 
left join  estratto_conto ec on a.id=ec.abbonamento_id  
where ec.id 
in (
select id from estratto_conto 
where pubblicazione_id = 5 
and numero = 0 
and id in (select estratto_conto_id from spedizione_item where pubblicazione_id = 5));

// checking Blocchetti con ec.numero > 0 e senza spedizione
select a.id as abb_id,ec.numero,stato_abbonamento,code_line_base,denominazione,code_line 
from anagrafica an 
left join abbonamento a on an.id = a.intestatario_id 
left join  estratto_conto ec on a.id=ec.abbonamento_id  
where ec.id 
in (
select id from estratto_conto 
where pubblicazione_id = 4 
and numero >0 
and id not in (select estratto_conto_id from spedizione_item where pubblicazione_id = 4));

// checking Blocchetti con ec.numero = 0 e con spedizione
select * from (select distinct estratto_conto_id,ec.numero,stato_abbonamento,code_line,code_line_base,denominazione,count(*) as cnt
from spedizione_item si
left join estratto_conto ec on ec.id = si.estratto_conto_id
left join abbonamento abb on abb.id=ec.abbonamento_id
left join anagrafica a on a.id = abb.intestatario_id
where si.pubblicazione_id = 4 and ec.numero = 0
group by estratto_conto_id,ec.numero,code_line,code_line_base,denominazione,stato_abbonamento
order by cnt desc) as t where t.cnt >0;

// Blocchetti numero di spedizione_item = 1
select * from (select distinct estratto_conto_id,ec.numero,stato_abbonamento,code_line,code_line_base,denominazione,count(*) as cnt
from spedizione_item si
left join estratto_conto ec on ec.id = si.estratto_conto_id
left join abbonamento abb on abb.id=ec.abbonamento_id
left join anagrafica a on a.id = abb.intestatario_id
where si.pubblicazione_id = 4 and ec.numero > 0
group by estratto_conto_id,ec.numero,code_line,code_line_base,denominazione,stato_abbonamento
order by cnt desc) as t where t.cnt <2;

/////////////////////////
 // checking Lodare con ec.numero > 0 e senza spedizione
select a.id as abb_id,ec.numero,stato_abbonamento,code_line_base,denominazione,code_line 
from anagrafica an 
left join abbonamento a on an.id = a.intestatario_id 
left join  estratto_conto ec on a.id=ec.abbonamento_id  
where ec.id 
in (
select id from estratto_conto 
where pubblicazione_id = 3 
and numero >0 
and id not in (select estratto_conto_id from spedizione_item where pubblicazione_id = 3));

// checking Lodare con ec.numero = 0 e con spedizione
select * from (select distinct estratto_conto_id,ec.numero,stato_abbonamento,code_line,code_line_base,denominazione,count(*) as cnt
from spedizione_item si
left join estratto_conto ec on ec.id = si.estratto_conto_id
left join abbonamento abb on abb.id=ec.abbonamento_id
left join anagrafica a on a.id = abb.intestatario_id
where si.pubblicazione_id = 3 and ec.numero = 0
group by estratto_conto_id,ec.numero,code_line,code_line_base,denominazione,stato_abbonamento
order by cnt desc) as t where t.cnt >0;

// Lodare numero di spedizione_item < 12
select * from (select distinct estratto_conto_id,ec.numero,stato_abbonamento,code_line,code_line_base,denominazione,count(*) as cnt
from spedizione_item si
left join estratto_conto ec on ec.id = si.estratto_conto_id
left join abbonamento abb on abb.id=ec.abbonamento_id
left join anagrafica a on a.id = abb.intestatario_id
where si.pubblicazione_id = 3 and ec.numero > 0
group by estratto_conto_id,ec.numero,code_line,code_line_base,denominazione,stato_abbonamento
order by cnt desc) as t where t.cnt <12;


/////////////////////////
 // checking Messaggio con ec.numero > 0 e senza spedizione
select a.id as abb_id,ec.numero,stato_abbonamento,code_line_base,denominazione,code_line 
from anagrafica an 
left join abbonamento a on an.id = a.intestatario_id 
left join  estratto_conto ec on a.id=ec.abbonamento_id  
where ec.id 
in (
select id from estratto_conto 
where pubblicazione_id = 2 
and numero >0 
and id not in (select estratto_conto_id from spedizione_item where pubblicazione_id = 2));

// checking Messaggio con ec.numero = 0 e con spedizione
select * from (select distinct estratto_conto_id,ec.numero,stato_abbonamento,code_line,code_line_base,denominazione,count(*) as cnt
from spedizione_item si
left join estratto_conto ec on ec.id = si.estratto_conto_id
left join abbonamento abb on abb.id=ec.abbonamento_id
left join anagrafica a on a.id = abb.intestatario_id
where si.pubblicazione_id = 2 and ec.numero = 0
group by estratto_conto_id,ec.numero,code_line,code_line_base,denominazione,stato_abbonamento
order by cnt desc) as t where t.cnt >0;

// Messaggio numero di spedizione_item < 11
select * from (select distinct estratto_conto_id,ec.numero,stato_abbonamento,code_line,code_line_base,denominazione,count(*) as cnt
from spedizione_item si
left join estratto_conto ec on ec.id = si.estratto_conto_id
left join abbonamento abb on abb.id=ec.abbonamento_id
left join anagrafica a on a.id = abb.intestatario_id
where si.pubblicazione_id = 2 and ec.numero > 0
group by estratto_conto_id,ec.numero,code_line,code_line_base,denominazione,stato_abbonamento
order by cnt desc) as t where t.cnt <11;


select distinct estratto_conto_id,ec.numero,code_line,code_line_base,denominazione,count(*) 
from spedizione_item si 
left join estratto_conto ec on ec.id = si.estratto_conto_id 
left join abbonamento abb on abb.id=ec.abbonamento_id 
left join anagrafica a on a.id = abb.intestatario_id 
where si.pubblicazione_id = 3 
group by estratto_conto_id,ec.numero,code_line,code_line_base,denominazione 
order by count;
// 
select ec.id from estratto_conto ec 
left join spedizione_item si on ec.id = si.estratto_conto_id 
where ec.pubblicazione_id != si.pubblicazione_id;


// spedizione item, estratto conto.
select distinct
ec.id as ecid,
si.id as siid, 
nome,
posticipata,
s.invio_spedizione ,
ec.invio_spedizione as ecsped,
stato_spedizione, si.mese_pubblicazione, si.anno_pubblicazione, s.mese_spedizione, s.anno_spedizione 
from pubblicazione p 
left join spedizione_item si on si.pubblicazione_id = p.id 
left join spedizione s on si.spedizione_id = s.id 
left join estratto_conto ec  on ec.id = si.estratto_conto_id
where ec.numero != si.numero;

//Fix SPEDIZIONE OTTOBRE 2019;

select distinct 
	nome,posticipata,invio_spedizione,stato_spedizione, si.mese_pubblicazione, si.anno_pubblicazione 
from pubblicazione p 
left join spedizione_item si on si.pubblicazione_id = p.id 
left join spedizione s on si.spedizione_id = s.id 
where mese_spedizione = 'OTTOBRE' 
and anno_spedizione='ANNO2019' order by nome;

select distinct 
	p.nome,count(*) 
from pubblicazione p 
left join spedizione_item si on si.pubblicazione_id = p.id 
left join spedizione s on si.spedizione_id = s.id 
where mese_spedizione = 'OTTOBRE' 
and anno_spedizione='ANNO2019' 
and posticipata = true
and si.mese_pubblicazione = 'GENNAIO'
and si.anno_pubblicazione = 'ANNO2020'
group by p.nome
order by nome;

select
	count(*) 
from pubblicazione p 
left join spedizione_item si on si.pubblicazione_id = p.id 
left join spedizione s on si.spedizione_id = s.id 
where mese_spedizione = 'OTTOBRE' 
and anno_spedizione='ANNO2019' 
and posticipata = true
and si.mese_pubblicazione = 'GENNAIO'
and si.anno_pubblicazione = 'ANNO2020';

update spedizione_item set posticipata = false 
where id in  
(select 
	si.id 
from pubblicazione p 
left join spedizione_item si on si.pubblicazione_id = p.id 
left join spedizione s on si.spedizione_id = s.id 
where mese_spedizione = 'OTTOBRE' 
and anno_spedizione='ANNO2019' 
and posticipata = true
and (p.nome = 'Estratti' or p.nome = 'Blocchetti')
and si.mese_pubblicazione = 'GENNAIO'
and si.anno_pubblicazione = 'ANNO2020');

   
//Fix SPEDIZIONE NOVEMBRE 2019;

select distinct 
	nome,posticipata,invio_spedizione,stato_spedizione, si.mese_pubblicazione, si.anno_pubblicazione 
from pubblicazione p 
left join spedizione_item si on si.pubblicazione_id = p.id 
left join spedizione s on si.spedizione_id = s.id 
where mese_spedizione = 'NOVEMBRE' 
and anno_spedizione='ANNO2019' order by nome;

update spedizione_item  set posticipata = false 
where id in (select 
    si.id from pubblicazione p 
    left join spedizione_item si on si.pubblicazione_id = p.id 
    left join spedizione s on si.spedizione_id = s.id 
    where mese_spedizione = 'NOVEMBRE' 
    and anno_spedizione='ANNO2019' 
    and posticipata = true and 
    (p.nome = 'Messaggio' or p.nome='Lodare')
    and si.mese_pubblicazione = 'GENNAIO'
    and si.anno_pubblicazione = 'ANNO2020');

//Fix SPEDIZIONE DICEMBRE 2019;

select distinct 
	nome,posticipata,invio_spedizione,stato_spedizione, si.mese_pubblicazione, si.anno_pubblicazione,
	count(*)
from pubblicazione p 
left join spedizione_item si on si.pubblicazione_id = p.id 
left join spedizione s on si.spedizione_id = s.id 
where mese_spedizione = 'DICEMBRE' 
and anno_spedizione='ANNO2019' 
group by nome,posticipata,invio_spedizione,stato_spedizione, si.mese_pubblicazione, si.anno_pubblicazione
order by nome,mese_pubblicazione;

update spedizione_item  set posticipata = false 
where id in (select 
    si.id from pubblicazione p 
    left join spedizione_item si on si.pubblicazione_id = p.id 
    left join spedizione s on si.spedizione_id = s.id 
    where mese_spedizione = 'DICEMBRE' 
    and anno_spedizione='ANNO2019' 
    and posticipata = true and 
    (p.nome = 'Messaggio' or p.nome='Lodare')
    and si.mese_pubblicazione = 'FEBBRAIO'
    and si.anno_pubblicazione = 'ANNO2020');

    
//Fix SPEDIZIONE GENNAIO 2020;

select distinct 
	nome,posticipata,invio_spedizione,stato_spedizione, si.mese_pubblicazione, si.anno_pubblicazione,
	count(*)
from pubblicazione p 
left join spedizione_item si on si.pubblicazione_id = p.id 
left join spedizione s on si.spedizione_id = s.id 
where mese_spedizione = 'GENNAIO' 
and anno_spedizione='ANNO2020' 
group by nome,posticipata,invio_spedizione,stato_spedizione, si.mese_pubblicazione, si.anno_pubblicazione
order by nome,mese_pubblicazione;

update spedizione_item  set posticipata = false 
where id in (select 
    si.id from pubblicazione p 
    left join spedizione_item si on si.pubblicazione_id = p.id 
    left join spedizione s on si.spedizione_id = s.id 
	where mese_spedizione = 'GENNAIO' 
	and anno_spedizione='ANNO2020' 
    and posticipata = true and 
    (p.nome = 'Messaggio' or p.nome='Lodare')
    and si.mese_pubblicazione = 'MARZO'
    and si.anno_pubblicazione = 'ANNO2020');

    //Fix SPEDIZIONE FEBBRAIO+ 2020;

select distinct 
	nome,posticipata,invio_spedizione,stato_spedizione, si.mese_pubblicazione, si.anno_pubblicazione,
	count(*)
from pubblicazione p 
left join spedizione_item si on si.pubblicazione_id = p.id 
left join spedizione s on si.spedizione_id = s.id 
where mese_spedizione = 'FEBBRAIO' 
and anno_spedizione='ANNO2020' 
group by nome,posticipata,invio_spedizione,stato_spedizione, si.mese_pubblicazione, si.anno_pubblicazione
order by nome,mese_pubblicazione;
