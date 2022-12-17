package learnings.demos.kafka.advanced;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class StockFilterStreamsDemo {

    static Properties getConsumerProperties() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "\t\n" + "127.0.0.1:9092");
        props.put("group.id", "Stock-Filter-streams-CG2");
        props.put("application.id", "Stock-Filter-streams-App");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return props;
    }

    static Properties getProducerProperties() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "Stock-Filter-streams-App");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return props;
    }

    static Properties getStreamProperties() {
        Properties props = new Properties();
        Serde<String> STRING_SERDE = Serdes.String();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-app");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.EXACTLY_ONCE_V2, "true");

        return props;
    }

    public static void main(String[] args) {

        Serde<String> STRING_SERDE = Serdes.String();

        String appleTopic = "APPLE";

        Map<String,Integer> stockCountMap = new HashMap<String,Integer>();
        System.out.println("starting filter streams app...");

        final StreamsBuilder builder = new StreamsBuilder();
        //consumer
        KStream<String, String> messageStream = builder.stream("stocks", Consumed.with(STRING_SERDE, STRING_SERDE));

        //filter
        KStream<String, String> filteredStream  =  messageStream.filter((key, stock) -> key.equalsIgnoreCase("APPL"));

        //producer
        filteredStream.to(appleTopic, Produced.with(Serdes.String(), Serdes.String()));

        //starting the application
        KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), getStreamProperties());
        kafkaStreams.start();
    }
}

// KSQL - select * from from stocks where <> and to desTop