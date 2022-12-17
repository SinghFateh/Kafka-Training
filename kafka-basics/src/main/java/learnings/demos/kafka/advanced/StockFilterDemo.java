package com.training.demo;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class StockFilterDemo {

    static Properties getConsumerProperties() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "\t\n" + "127.0.0.1:9092");
        props.put("group.id", "Stock-Filter-CG2");
        props.put("application.id", "Stock-Filter-App");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return props;
    }

    static Properties getProducerProperties() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "Stock-Filter-Producer-App");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return props;
    }

    public static void main(String[] args) {

        String appleTopic = "APPLE";

        Map<String,Integer> stockCountMap = new HashMap<String,Integer>();

        System.out.println("starting filter consumer...");
        Duration duration = Duration.ofMillis(1000);
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(getConsumerProperties());
             KafkaProducer<String, String> producer = new KafkaProducer<String, String>(getProducerProperties())
        ) {
            consumer.subscribe(Collections.singletonList("stocks"));
            while (true) {
                ConsumerRecords<String, String> consumerRecords = consumer.poll(duration);
                consumerRecords.forEach(record -> {
                    //System.out.println("key= "+record.key()+ ", value=" + record.value());
                    if(record.key().equalsIgnoreCase("APPL")) {
                        ProducerRecord<String,String> producerRecord = new ProducerRecord<String,String>(appleTopic,record.key(),record.value());
                        producer.send(producerRecord, (metadata, exception)-> {
                            System.out.println("Exception occurred for topic=" +metadata.topic() + ", partition" +
                                    ""+metadata.partition()+ ", offset="+metadata.offset() + "exception="+exception.getStackTrace());
                        } );
                    }
                });
            }
        }
    }
}
