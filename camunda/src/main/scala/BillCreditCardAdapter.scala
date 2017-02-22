
import org.camunda.bpm.engine.delegate.{ DelegateExecution, JavaDelegate }
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.ProducerConfig
import helpers.KafkaHelper
import java.util.Properties

class BillCreditCardAdapter extends JavaDelegate {
  
  def execute(execution: DelegateExecution): Unit = {
    val rider = execution.getVariable("rider")
    
    println("now we bill the credit card for rider " + rider);

  }
}