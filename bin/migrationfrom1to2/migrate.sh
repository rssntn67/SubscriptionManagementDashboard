SQL_GET_VERSAMENTO_WITH_MULTI_ABBONAMENTO="
select distinct 
  verid,
  count(*) 
  from temp_operazione_incasso 
  where vercount=1
  group by verid 
  order by count desc;"

psql smd -tAX -c "$SQL_GET_VERSAMENTO_WITH_MULTI_ABBONAMENTO" | while read line
do 
verid=`echo $line | cut -d '|' -f1`
count=`echo $line | cut -d '|' -f2`
if [ $count -gt 1 ]
then 
echo fixing $line
psql smd -c "update temp_operazione_incasso set vercount=$count where verid = $verid"
fi
done

SQL_CHECK_ABBONAMENTO="select distinct abbid, abbinc-sum(verinc) as diff 
   from temp_operazione_incasso 
   where vercount = 1 group by abbid,abbinc order by diff;"

psql smd -tAX -c "$SQL_CHECK_ABBONAMENTO" | while read line
do 
abbid=`echo $line | cut -d '|' -f1`
diff=`echo $line | cut -d '|' -f2`
if [ "$diff" != "0.00" ]
then 
echo error temp_operazione_incasso abbid $abbid $diff
fi
done

SQL_CHECK_MULTI="select distinct verid, verinc-sum(abbinc) as diff 
    from temp_operazione_incasso 
    where vercount > 1 group by verid,verinc order by diff desc;"

psql smd -tAX -c "$SQL_CHECK_MULTI" | while read line
do 
verid=`echo $line | cut -d '|' -f1`
diff=`echo $line | cut -d '|' -f2`
echo $verid $diff
if [ "$diff" != "0.00" ]
then 
echo error temp_operazione_incasso verid $verid $diff
fi
done

INSERT1="insert into operazione_incasso  
      select nextval('hibernate_sequence'),
      now(), 
      operazione,
      abbinc,
      'adp',
      'Incasso',
      abbid,
      verid from temp_operazione_incasso where vercount > 1;"
INSERT2="insert into operazione_incasso  
      select nextval('hibernate_sequence'),
      now(), 
      operazione,
      verinc,
      'adp',
      'Incasso',
      abbid,
      verid from temp_operazione_incasso where vercount = 1;"
      
psql smd -tAX -c "$INSERT1"     
psql smd -tAX -c "$INSERT2"     