
import org.camunda.bpm.engine.delegate.{ DelegateExecution, JavaDelegate }
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.ProducerConfig
import helpers.KafkaHelper
import java.util.Properties
import org.camunda.bpm.engine.delegate.BpmnError

object BillCreditCardAdapter {
   private var current = 0
   private def inc = {current += 1; current}
}

class BillCreditCardAdapter extends JavaDelegate {
  
  def execute(execution: DelegateExecution): Unit = {
    val rider = execution.getVariable("rider")
    
    // now we would do whatever we need to do
    
    if (BillCreditCardAdapter.inc % 3 == 0) {
      // every 3 rides we assume that there was a problem  
    	println("we could not bill credit card for rider " + rider + ". Send to Human to sort it out");
      throw new BpmnError("Error_CreditCardFailure");
    } else {
      // otherwise we assume it worked
    	println("now we bill the credit card for rider " + rider);
    }

  }
}