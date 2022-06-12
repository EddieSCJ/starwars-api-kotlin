kafka-topics.sh --bootstrap-server kafka:9092 --list

echo -e 'Creating kafka topics'
kafka-topics.sh --bootstrap-server kafka:9092 --create --if-not-exists --topic planet-event --replication-factor 1 --partitions 1

echo -e 'Successfully created the following topics:'
kafka-topics.sh --bootstrap-server kafka:9092 --list