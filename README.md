# cs-platform

Customer Smart Data Platform

1. Download and install zookeeper & KFKA

  zookeper 3.4.12
  kafka_2.11-2.0.0

  > bin/zkServer.sh start

  > bin/kafka-server-start.sh config/server.properties



2. Dowload and install Elasticsearch
   elasticsearch-6.4.2

3. Download and install KIBANA

   kibana-6.2.3


4. Start zookeper

5. Start KAFKA server

6. create a topic on KAFKA

 from KAFKA directory :
 > ./bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic customer-activity

7. create an index in ElasticSeaerch database

 > curl -XPUT "http://localhost:9200/customer-activity


8.  Start the CSPlatform


9. Play some data onto KAFKA

     > bin/kafka-console-producer.sh --broker-list localhost:9092 --topic customer-activity


     Copy some data from data files in the directory data. For example accounts.json
     You will see this data on KIBANA

10. Bringup KIBANA on a browser at:   http://localhost:5601



11. Execute  the CSPlatform with the command:

> mvn exec:java -Dexec.mainClass=ai.customersmart.CSPlatform


12. Put some data onto the KAFKA topic 'customer-activity'

Run the console KAFKA producer
$ bin/kafka-console-producer.sh --broker-list localhost:9092 --topic customer-activity

Then type the content:
> {"account_number":1,"balance":39225,"firstname":"Amber","lastname":"Duke","age":32,"gender":"M","address":"880 Holmes Lane","employer":"Pyrami","email":"amberduke@pyrami.com","city":"Brogan","state":"IL"}

There is a file in data/ directory where sample data is located. We can use accounts.json for this testing
