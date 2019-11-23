pid=`cat ../logs/smd.pid`
count=`ps -A | grep  $pid  | grep java|wc -l` 
   if [ $count -eq 1 ] 
   then echo "Gestione Abbonamenti ADP running"
   fi
   if [ $count -eq 0 ] 
   then echo "Gestione Abbonamenti ADP not running"
   fi
