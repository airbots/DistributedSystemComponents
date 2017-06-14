package edu.unl.hcc.kafka.examples;

import com.oracle.kafka.connect.swift.SwiftSinkConnectorConfig;
import com.oracle.kafka.connect.swift.SwiftSinkTask;

import io.confluent.connect.avro.AvroData;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.sink.SinkTaskContext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by chehe on 2017/5/22.
 */
public class SwiftSinkEvaluation {

    protected Configuration conf;
    protected String url;
    protected Map<String, String> connectorProps;
    protected SwiftSinkConnectorConfig connectorConfig;
    protected String topicsDir;
    protected String logsDir;
    protected AvroData avroData;

    protected SinkTaskContext context;
    protected static final String TOPIC = "SwiftTopic";
    protected static final String TOPIC_WITH_DOTS = "topic.with.dots";
    protected static final int PARTITION = 12;
    protected static final int PARTITION2 = 13;
    protected static final int PARTITION3 = 14;
    protected static final TopicPartition TOPIC_PARTITION = new TopicPartition(TOPIC, PARTITION);
    protected static final TopicPartition TOPIC_PARTITION2 = new TopicPartition(TOPIC, PARTITION2);
    protected static final TopicPartition TOPIC_PARTITION3 = new TopicPartition(TOPIC, PARTITION3);
    protected static final TopicPartition TOPIC_WITH_DOTS_PARTITION = new TopicPartition(TOPIC_WITH_DOTS, PARTITION);
    protected static Set<TopicPartition> assignment;

    protected FileSystem fs;

    protected Map<String, String> createProps() {
        Map<String, String> props = new HashMap<String, String>();
        props.put(SwiftSinkConnectorConfig.HDFS_URL_CONFIG, url);
        props.put(SwiftSinkConnectorConfig.FLUSH_SIZE_CONFIG, "3");
        props.put(SwiftSinkConnectorConfig.SWIFT_AUTH_URL, "https://storage.oraclecorp.com/auth/v2.0/tokens");
        props.put(SwiftSinkConnectorConfig.SWIFT_ACCESS_PUBLIC, "true");
        props.put(SwiftSinkConnectorConfig.SWIFT_TENANT_ID, "Storage-StorageEval02admin");
        props.put(SwiftSinkConnectorConfig.SWIFT_USERNAME, "Storageadmin");
        props.put(SwiftSinkConnectorConfig.SWIFT_PASSWORD, "Welcome1");
        return props;
    }

    protected Schema createSchema() {
        return SchemaBuilder.struct().name("record").version(1)
                .field("boolean", Schema.BOOLEAN_SCHEMA)
                .field("int", Schema.INT32_SCHEMA)
                .field("long", Schema.INT64_SCHEMA)
                .field("float", Schema.FLOAT32_SCHEMA)
                .field("double", Schema.FLOAT64_SCHEMA)
                .build();
    }

    protected Configuration createConf(){
        Configuration conf = new Configuration();
        conf.set(SwiftSinkConnectorConfig.SWIFT_AUTH_URL,
                "https://storage.oraclecorp.com/auth/v2.0/tokens");
        conf.setBoolean(SwiftSinkConnectorConfig.SWIFT_ACCESS_PUBLIC,
                true);
        conf.set(SwiftSinkConnectorConfig.SWIFT_TENANT_ID,
                "Storage-StorageEval02admin");
        conf.set(SwiftSinkConnectorConfig.SWIFT_USERNAME,
                "Storageadmin");
        conf.set(SwiftSinkConnectorConfig.SWIFT_PASSWORD,
                "Welcome1");
        return conf;
    }

    protected Struct createRecord(Schema schema) {
        return new Struct(schema)
                .put("boolean", true)
                .put("int", 12)
                .put("long", 12L)
                .put("float", 12.2f)
                .put("double", 12.2);
    }

    protected Schema createNewSchema() {
        return SchemaBuilder.struct().name("record").version(2)
                .field("boolean", Schema.BOOLEAN_SCHEMA)
                .field("int", Schema.INT32_SCHEMA)
                .field("long", Schema.INT64_SCHEMA)
                .field("float", Schema.FLOAT32_SCHEMA)
                .field("double", Schema.FLOAT64_SCHEMA)
                .field("string", SchemaBuilder.string().defaultValue("abc").build())
                .build();
    }

    protected Struct createNewRecord(Schema newSchema) {
        return new Struct(newSchema)
                .put("boolean", true)
                .put("int", 12)
                .put("long", 12L)
                .put("float", 12.2f)
                .put("double", 12.2)
                .put("string", "def");
    }

    public void setUp() throws Exception {
        url = "swift://bdcsce.default/";
        connectorProps = createProps();
        configureConnector();
        conf = createConf();
        conf.set("fs.defaultFS", url);
        fs = FileSystem.get(conf);
        assignment = new HashSet<TopicPartition>();
        assignment.add(TOPIC_PARTITION);
        assignment.add(TOPIC_PARTITION2);
        context = new SwiftSinkTaskContext();
    }

    public void tearDown() throws Exception {
        if (assignment != null) {
            assignment.clear();
        }
    }

    protected void configureConnector() {
        connectorConfig = new SwiftSinkConnectorConfig(connectorProps);
        topicsDir = connectorConfig.getString(SwiftSinkConnectorConfig.TOPICS_DIR_CONFIG);
        logsDir = connectorConfig.getString(SwiftSinkConnectorConfig.LOGS_DIR_CONFIG);
        int schemaCacheSize = connectorConfig.getInt(SwiftSinkConnectorConfig.SCHEMA_CACHE_SIZE_CONFIG);
        avroData = new AvroData(schemaCacheSize);
    }

    protected static class SwiftSinkTaskContext implements SinkTaskContext {

        private Map<TopicPartition, Long> offsets;
        private long timeoutMs;

        public SwiftSinkTaskContext() {
            this.offsets = new HashMap<TopicPartition, Long>();
            this.timeoutMs = -1L;
        }

        public void offset(Map<TopicPartition, Long> offsets) {
            this.offsets.putAll(offsets);
        }

        public void offset(TopicPartition tp, long offset) {
            offsets.put(tp, offset);
        }

        /**
         * Get offsets that the SinkTask has submitted to be reset. Used by the Copycat framework.
         * @return the map of offsets
         */
        public Map<TopicPartition, Long> offsets() {
            return offsets;
        }

        public void timeout(long timeoutMs) {
            this.timeoutMs = timeoutMs;
        }

        /**
         * Get the timeout in milliseconds set by SinkTasks. Used by the Copycat framework.
         * @return the backoff timeout in milliseconds.
         */
        public long timeout() {
            return timeoutMs;
        }

        /**
         * Get the timeout in milliseconds set by SinkTasks. Used by the Copycat framework.
         * @return the backoff timeout in milliseconds.
         */

        public Set<TopicPartition> assignment() {
            return assignment;
        }

        public void pause(TopicPartition... partitions) {}

        public void resume(TopicPartition... partitions) {}

        public void requestCommit() {}
    }

    public static void main(String[] args) throws Exception {
        SwiftSinkEvaluation sse = new SwiftSinkEvaluation();
        sse.setUp();

        sse.context = new SwiftSinkTaskContext();
        SwiftSinkTask task = new SwiftSinkTask();

        task.initialize(sse.context);
        System.out.println("CHEN: SwiftSinkTask connectorConfig: " + sse.connectorProps.get("fs.swift.service.default.auth.url"));
        task.start(sse.connectorProps);

        sse.tearDown();
    }

}
