package marist;

import java.net.MalformedURLException;
import java.net.URL;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutomationService {

  private String DEGREEWORKS_URL = "https://degreeworks.banner.marist.edu/dashboard/dashboard";
  private String username;
  private String password;
  RemoteWebDriver driver;
  private final Logger logger = LoggerFactory.getLogger(AutomationService.class);

  public AutomationService(String username, String password) {
    this.username = username;
    this.password = password;
    try {
      this.driver = new RemoteWebDriver(new URL("http://chrome:4444/wd/hub"), DesiredCapabilities.chrome());
    } catch (MalformedURLException ex) {
      logger.warn("Unable to instantiate Selenium remote web driver", ex);
    }
  }

  public String getText() {
    this.driver.get(DEGREEWORKS_URL);

    // authN
    WebElement usernameDiv = driver.findElement(By.name("username"));
    usernameDiv.sendKeys(username);
    WebElement passwordDiv = driver.findElement(By.name("password"));
    passwordDiv.sendKeys(password);
    passwordDiv.submit();

    // navigate to correct <frame>
    driver.switchTo().frame("frBodyContainer");
    driver.switchTo().frame("frBody");
    String degreeWorksText = driver.findElement(By.tagName("html")).getText();

    // close browser asynchronously
    new Thread(() -> {
      driver.quit();
    }).start();
    return degreeWorksText;
  }

}
