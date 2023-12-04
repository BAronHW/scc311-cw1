cd server 
sleep 1
rmiregistry &
sleep 2
java Replica 1 &
sleep 1
java Replica 2 &
sleep 1
java Replica 3 