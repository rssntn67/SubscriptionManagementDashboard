echo "Stopping Gestione Abbonamento ADP"
kill $pid `cat ../logs/smd.pid` 
sleep 2
echo "Stopped"
