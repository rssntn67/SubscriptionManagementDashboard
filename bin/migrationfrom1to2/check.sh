SQL_GET_VERSAMENTI_DUPLICATI="select
   a.id as abbid,
   v.id as verid,
   v.incasso_id as incid,
   i.cassa,
   a.code_line as abbcode,
   v.operazione,
   a.importo+a.pregresso+a.spese+a.spese_estero as totale,
   a.incassato as abbinc ,
   v.importo as verimp
 from abbonamento a
 left join versamento v
 on a.code_line = v.code_line
 left join incasso i
 on i.id = v.incasso_id
 where v.id is not null
 and a.incassato > 0
 and v.incassato = 0;"

SQL_CHECK_IMPORTO="select 
       i.id,
       i.importo,
       sum(v.importo) 
   from incasso i left join versamento v 
   on v.incasso_id=i.id  
   group by i.id 
   order by i.importo desc;"
         
echo check incassi con importo maggiore della somma dei versamenti
psql smd -tAX -c "$SQL_CHECK_IMPORTO" | while read line
do 
id=`echo $line | cut -d '|' -f1` 
inc=`echo $line | cut -d '|' -f2`; 
ver=`echo $line | cut -d '|' -f3`; 
if [ "$ver" != "$inc" ]
 then echo "error: importo errato incassois=$incasso importo=$inc versmaneti=$ver"
fi
done
echo "------------------"
echo

SQL_CHECK_INCASSATO="select i.id,
       i.incassato,
       sum(v.incassato) 
  from incasso i 
  left join versamento v on v.incasso_id=i.id  
  group by i.id 
  order by i.incassato desc;"
 
echo check incassi con incasso maggiore della somma dei versamenti
psql smd -tAX -c "$SQL_CHECK_INCASSATO" | while read line
do 
id=`echo $line | cut -d '|' -f1` 
inc=`echo $line | cut -d '|' -f2`; 
ver=`echo $line | cut -d '|' -f3`; 
if [ "$ver" != "$inc" ]
 then echo "fixing: idincasso=$id, incassato.incass=$inc incassato.versamenti=$ver"
psql smd -c "update incasso set incassato = $ver where id = $id;" 
fi
done
echo "------------------"
echo

SQL_FIX_INCASSO_ABBONAMENTI_1="select 
   a.id as abbid,
   v.id as verid,
   v.incasso_id as incid,
   a.code_line as abbcode,
   v.code_line as vercode,
   v.operazione,
   a.importo+a.pregresso+a.spese+a.spese_estero as totale,
   a.incassato as abbinc ,
   v.importo as verimp,
   v.incassato as verinc 
 from abbonamento a left join versamento v 
 on a.versamento_id = v.id 
 where 
   a.versamento_id is not null 
 and 
   a.incassato=v.incassato 
 and 
   a.importo+a.pregresso+a.spese+a.spese_estero < a.incassato;"

echo check abbonamenti con incasso maggiore totale
psql smd -tAX -c "$SQL_FIX_INCASSO_ABBONAMENTI_1" | while read line
do
abbid=`echo $line | cut -d '|' -f1` 
verid=`echo $line | cut -d '|' -f2` 
incid=`echo $line | cut -d '|' -f3`
totale=`echo $line | cut -d '|' -f7`
verinc=`echo $line | cut -d '|' -f10`
echo fixing: abbid=$abbid verid=$verid incid=$incid totale=$totale incassato=$verinc 
psql smd -c "update abbonamento set incassato = $totale where id = $abbid;"
psql smd -c "update versamento set incassato = $totale where id= $verid;"
psql smd -c "update incasso set incassato= incassato - $verinc + $totale where id = $incid;"
done 
echo "------------------"
echo

echo check versamenti duplicati
psql smd -tAX -c "$SQL_GET_VERSAMENTI_DUPLICATI"   
echo "------------------"
echo

echo check versamenti with null code_line
psql smd -tAX -c "select * from versamento where code_line is null;"
echo "------------------"
echo

CHECK_ABBONAMENTI="select * from abbonamento where incassato -importo -pregresso -spese-spese_estero > 0;
select * from abbonamento where incassato > 0 and versamento_id not in (select id from versamento);
select * from abbonamento where incassato > 0 and code_line  not in (select code_line from versamento);"

echo check Abbonamenti
psql smd -tAX -c "$CHECK_ABBONAMENTI"   
echo "------------------"
echo

SQL_ABB_SENZA_VERS="select * from abbonamento a 
 where versamento_id is null 
 and incassato > 0 
 and not exists (select id from versamento v where v.code_line = a.code_line);"

SQL_SUM_ABB_SENZA_VERS="select sum(incassato) from abbonamento a 
 where versamento_id is null 
 and incassato > 0 
 and not exists (select id from versamento v where v.code_line = a.code_line);"

SQL_VERS_SENZA_ABB="select * from versamento v 
where incassato > 0 
and not exists 
(select id from abbonamento a 
  where v.code_line = a.code_line or 
  (a.versamento_id is not null and a.versamento_id = v.id));"

SQL_SUM_VERS_SENZA_ABB="select sum(incassato) from versamento v 
where incassato > 0 
and not exists 
(select id from abbonamento a 
  where v.code_line = a.code_line or 
  (a.versamento_id is not null and a.versamento_id = v.id));"

echo abbonamenti incassati senza un versamento corrispondente
psql smd -tAX -c "$SQL_ABB_SENZA_VERS"
echo "------------------"
echo

echo versamenti incassati senza un abbonamento corrispondente
psql smd -tAX -c "$SQL_VERS_SENZA_ABB"
echo "------------------"
echo

incassoAbb=`psql smd -tAX -c "select sum(incassato) from abbonamento;"`
echo incassoAbb = $incassoAbb
echo incassoSenzaVersamento=`psql smd -tAX -c "$SQL_SUM_ABB_SENZA_VERS"`
incassoInc=`psql smd -tAX -c "select sum(incassato) from incasso;"`
echo incassoInc = $incassoInc
incassoVer=`psql smd -tAX -c "select sum(incassato) from versamento;"`
echo incassoVer = $incassoVer
echo incassoSenzaAbbonamento=`psql smd -tAX -c "$SQL_SUM_VERS_SENZA_ABB"`

impInc=`psql smd -tAX -c "select sum(importo) from incasso;"`
echo impInc = $impInc
impVer=`psql smd -tAX -c "select sum(importo) from versamento;"`
echo impVer = $impVer

