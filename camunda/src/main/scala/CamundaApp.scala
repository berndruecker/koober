
import scala.collection.JavaConversions._

import org.camunda.bpm.engine.ProcessEngineConfiguration

import org.apache.kafka.common.serialization._
import org.apache.kafka.streams._
import org.apache.kafka.streams.kstream.{ KStream, KStreamBuilder }
import helpers.KafkaHelper
import java.util.Properties
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import java.util.Collections
import java.util.concurrent.Executors
import org.apache.kafka.clients.consumer.ConsumerRecord
import play.api.libs.json.Json
import org.camunda.bpm.engine.variable.Variables
import org.h2.tools.Server
import java.util.ArrayList

object CamundaApp extends App {

  val configuration = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration()
    //.setJdbcDriver("org.postgresql.Driver")
    .setJdbcUrl("jdbc:h2:./camunda-db")
    //.setJdbcUsername("camunda")
    //.setJdbcPassword("camunda")
    .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE)
    .setHistory(ProcessEngineConfiguration.HISTORY_FULL)
    .setJobExecutorActivate(true)

  val h2Server = Server.createTcpServer("-tcpPort", "8092", "-tcpAllowOthers").start()
  // now you can connect to "jdbc:h2:tcp://localhost:8092/./camunda-db" 

  val engine = configuration.buildProcessEngine()

  val deployment = engine.getRepositoryService.createDeployment()
  deployment
    .addClasspathResource("KooberRide.bpmn")
    .addClasspathResource("KooberPaymentSimple.bpmn")
    .enableDuplicateFiltering(true)
  deployment.deploy()

  val props = new Properties()
  props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaHelper.kafkaUrl())
  props.put(ConsumerConfig.GROUP_ID_CONFIG, "camunda")
  props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
  props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000")
  props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000")
  props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
  props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")

  val consumer = new KafkaConsumer[String, String](props)

  consumer.subscribe(List("driver", "rider"))

  Executors.newSingleThreadExecutor.execute(new Runnable {
    override def run(): Unit = {
      while (true) {
        val allRecords = consumer.poll(1000)

        val riderRecords = allRecords.records("rider");
        for (record <- riderRecords) {
          val json = Json.parse(record.value);
          val rider: Option[String] = (json \ "rider").asOpt[String];
          if (rider != None) {
            val alreadyRunningCount = engine.getHistoryService.createHistoricProcessInstanceQuery
              .variableValueEquals("rider", rider.get)
              .count();
            if (alreadyRunningCount == 0) {
              engine.getRuntimeService.startProcessInstanceByKey("KooberRide",
                Variables.createVariables().putValue("rider", rider.get))
              println("Start Camunda process instance for rider " + rider.get);
            }
          }
        }

        val driverRecords = allRecords.records("driver");
        for (record <- driverRecords) {
          val json = Json.parse(record.value);
          val rider = (json \ "rider").asOpt[String];
          val driver = (json \ "driver").asOpt[String];
          val status = (json \ "status").asOpt[String];

          // Status changed to pickup -> send message to process (if existing)
          if (status != None && rider != None && driver != None
            && status.get.equals("pickup")) {

            val waitingForPickupCount = engine.getRuntimeService.createExecutionQuery
              .messageEventSubscriptionName("Msg_DriverPickedUpRider")
              .processVariableValueEquals("rider", rider.get)
              .count();

            if (waitingForPickupCount > 0) {
              engine.getRuntimeService.createMessageCorrelation("Msg_DriverPickedUpRider")
                .processInstanceVariableEquals("rider", rider.get)
                .setVariable("driver", driver.get)
                .correlateAllWithResult()
              println("Correlate pick up message to waiting process instance for rider " + rider.get);
            }

          }
        }

      }
    }
  });
}