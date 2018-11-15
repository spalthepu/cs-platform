/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ai.customersmart;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer08;
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction;

import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;

import java.util.*;

/*
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
*/

// import org.apache.flink.streaming.util.serialization.;

/**
 * Skeleton for a Flink Streaming Job.
 *
 * <p>For a tutorial how to write a Flink streaming application, check the
 * tutorials and examples on the <a href="http://flink.apache.org/docs/stable/">Flink Website</a>.
 *
 * <p>To package your application into a JAR file for execution, run
 * 'mvn clean package' on the command line.
 *
 * <p>If you change the name of the main class (with the public static void main(String[] args))
 * method, change the respective entry in the POM.xml file (simply search for 'mainClass').
 */
public class CSPlatform {

	public static void main(String[] args) throws Exception {
		// set up the streaming execution environment
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();


		String topic = "customer-activity";
		String groupId = "customer";

		Properties properties = new Properties();
		properties.setProperty("bootstrap.servers", "localhost:9092");
        // only required for Kafka 0.8
		properties.setProperty("zookeeper.connect", "localhost:2181");
		properties.setProperty("group.id", groupId);
		FlinkKafkaConsumer08<String> myConsumer = new FlinkKafkaConsumer08<>(topic, new SimpleStringSchema(), properties);

		//myConsumer.setStartFromEarliest();     // start from the earliest record possible
		//myConsumer.setStartFromLatest();       // start from the latest record
		// myConsumer.setStartFromTimestamp(...); // start from specified epoch timestamp (milliseconds)
		myConsumer.setStartFromGroupOffsets(); // the default behaviour

		DataStream<String> stream = env.addSource(myConsumer);

		// convert strings to Tupple inline
		DataStream<Tuple2<String,String>> parsedStream =
				stream.map(
				new MapFunction<String, Tuple2<String,String>>() {
					@Override
					public Tuple2<String,String> map(String s) throws Exception {
						String time = Long.toString(System.currentTimeMillis());
						return new Tuple2<String,String>(time, s);
					}
				}
		);


		// Add transformation


		// Add elastic search sink
        Map<String, String> config = new HashMap<>();
        config.put("bulk.flush.max.actions", "1");   // flush inserts after every event
        config.put("cluster.name", "my_cluster"); // default cluster name

		List<HttpHost> httpHosts = new ArrayList<>();
		httpHosts.add(new HttpHost("127.0.0.1", 9200, "http"));

		// use a ElasticsearchSink.Builder to create an ElasticsearchSink
		ElasticsearchSink.Builder <Tuple2<String,String>> esSinkBuilder = new ElasticsearchSink.Builder<>(
				httpHosts,
				new ElasticsearchSinkFunction<Tuple2<String,String>>() {
					public IndexRequest createIndexRequest(Tuple2<String,String> element) {
						Map<String, String> json = new HashMap<>();
						json.put("data", element.toString());

						return Requests.indexRequest()
								.index("customer-activity")
								.type("customer-type")
								.source(json);
					}

					@Override
					public void process(Tuple2<String,String> element, RuntimeContext ctx, RequestIndexer indexer) {
						System.out.println("got: " + element);
						indexer.add(createIndexRequest(element));
					}
				}
		);
		esSinkBuilder.setBulkFlushMaxActions(1);
		parsedStream.addSink(new PrintSinkFunction<>());
		parsedStream.addSink(esSinkBuilder.build());

		
		

		// execute program
		env.execute("Flink Streaming Java API Skeleton");
	}
}
