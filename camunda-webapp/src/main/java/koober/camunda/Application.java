package koober.camunda;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.camunda.consulting.util.LicenseHelper;
import com.camunda.consulting.util.UserGenerator;

@SpringBootApplication
@EnableAutoConfiguration
@EnableProcessApplication
public class Application  {

  public static void main(String... args) {    
    SpringApplication.run(Application.class, args);

    // now install license and add default users to Camunda to be ready-to-go
    ProcessEngine engine = BpmPlatform.getDefaultProcessEngine();    
    LicenseHelper.setLicense(engine);
    UserGenerator.createDefaultUsers(engine);
  }

}
