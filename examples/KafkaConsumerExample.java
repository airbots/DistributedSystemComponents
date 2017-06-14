package edu.unl.hcc.kafka.examples;

import com.oracle.kafka.connect.swift.SwiftSinkTask;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Properties;

/**
 * Created by chehe on 2017/5/22.
 */
public class KafkaConsumerExample {

    public static void main(String[] args){
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "consumer-tutorial");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        KafkaConsumer<String, String> consumer = new OCSConsumer(props);


    }

    static class OCSConsumer extends KafkaConsumer {

        public OCSConsumer(Properties properties) {
            super(properties);
        }


    }

    public SwiftSinkTask createTask(){
        SwiftSinkTask swiftSinkTask = new SwiftSinkTask();
        return swiftSinkTask;
    }

}
