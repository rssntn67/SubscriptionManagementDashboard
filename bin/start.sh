echo "Starting Gestione Abbonamenti ADP"
mv ../logs/smd.log ../logs/smd`date +"%y%m%d-%H-%M"`.log
nohup ../lib/smd-2.0-SNAPSHOT.jar > ../logs/smd.log 2>&1 &
pid=`echo $!`
sleep 3
pids=`ps -f | grep $pid | grep -v grep | awk {'print $2'} | grep -v $pid` 
echo $pids > ../logs/smd.pid
echo "Gestione Abbonamenti ADP Started"
