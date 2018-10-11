package marist;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@RestController
public class Endpoint {

  private final Logger logger = LoggerFactory.getLogger(Endpoint.class);

  @RequestMapping(method = RequestMethod.POST, value = "/")
  public String getDegreeWorksText(@RequestParam String username,
          @RequestParam String password,
          @RequestParam(value = "analytics", required = false, defaultValue = "true") boolean sendAnalytics) {
    Analytics analytics = new Analytics();
    String degreeWorksText;

    Instant startTime = Instant.now();
    AutomationService automationService = new AutomationService(username, password);
    Instant endTime = Instant.now();
    analytics.timeToInstantiate = Duration.between(startTime, endTime).toMillis();

    startTime = Instant.now();
    degreeWorksText = automationService.getText();
    endTime = Instant.now();
    analytics.timeToExecute = Duration.between(startTime, endTime).toMillis();

    if (sendAnalytics) {
      // asynchronously send analytics
      new Thread(() -> {
        logger.trace("Sending analytics");
        analytics.send();
      }).start();
    } else {
      logger.trace("Skipping analytics");
    }

    return degreeWorksText;
  }

  @RequestMapping(method = RequestMethod.GET, value = "/healthcheck")
  public String doHealthCheck() {
    return new Date().toString();
  }
}
