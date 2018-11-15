package ai.customersmart.elastic;

import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;
import org.apache.flink.api.java.tuple.Tuple3;
import java.util.Map;
import java.util.HashMap;


public class SimpleESSinkBuilder
            implements ElasticsearchSinkFunction<Tuple3<String, String, String> > {

        public void process(
                Tuple3<String,String, String> record,
                RuntimeContext ctx,
                RequestIndexer indexer) {

            // construct JSON document to index

            Map<String, String> json = new HashMap<>();
            json.put("time", record.f0.toString());  // timestamp
            json.put("user", record.f1.toString());  // user
            json.put("payload", record.f2.toString());  // payload

            IndexRequest rqst = Requests.indexRequest()
                    .index("test-data")           // index name
                    .type("user-actions")     // mapping name
                    .source(json);

            indexer.add(rqst);
        }
    }


