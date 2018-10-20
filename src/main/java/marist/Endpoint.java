package marist;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class Endpoint {

  Logger logger = LoggerFactory.getLogger(Endpoint.class);

  @RequestMapping(method = RequestMethod.POST, value = "/")
  public ResponseEntity getDegreeWorksText(@RequestParam String username,
          @RequestParam String password,
          @RequestParam(value = "analytics", required = false, defaultValue = "true") boolean sendAnalytics) {

    Analytics analytics = new Analytics();

    // Instantiate WebDriver
    Instant startTime = Instant.now();
    AutomationService automationService = new AutomationService(username, password);
    Instant endTime = Instant.now();
    analytics.timeToInstantiate = Duration.between(startTime, endTime).toMillis();

    // fetch DegreeWorks text
    startTime = Instant.now();
    automationService.getDegreeWorksText();
    endTime = Instant.now();
    analytics.timeToExecute = Duration.between(startTime, endTime).toMillis();

    // return 401 if CAS failed
    if (automationService.degreeWorksText == null) {
      return new ResponseEntity<>("Invalid Marist credentials", HttpStatus.UNAUTHORIZED);
    }

    if (sendAnalytics) {
      // asynchronously send analytics
      new Thread(() -> {
        analytics.degreeWorksText = automationService.degreeWorksText;
        analytics.send();
      }).start();
    }

    return new ResponseEntity(automationService.degreeWorksText, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/healthcheck")
  public String doHealthCheck() {
    return new Date().toString();
  }
}
