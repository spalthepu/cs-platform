# cs-platform
Customer Smart Platform

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




