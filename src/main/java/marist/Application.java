package marist;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
public class Application {

  public static String POSTGRES_USER;
  public static String POSTGRES_PASSWORD;
  public static String POSTGRES_DB;
  public static String POSTGRES_ANALYTICS_TABLE;
  public static Connection CONNECTION;
  static final Logger logger = LoggerFactory.getLogger(Application.class);

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
    loadConfigs();
    connectToDatabase();
  }

  @Bean
  public Executor asyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(2);
    executor.setQueueCapacity(500);
    executor.setThreadNamePrefix("SendAnalytics--");
    executor.initialize();
    return executor;
  }

  private static void loadConfigs() {
    Properties properties = new Properties();
    InputStream inputStream;
    try {
      inputStream = new FileInputStream("db-configs.properties");
      properties.load(inputStream);
      POSTGRES_USER = properties.getProperty("POSTGRES_USER");
      POSTGRES_PASSWORD = properties.getProperty("POSTGRES_PASSWORD");
      POSTGRES_DB = properties.getProperty("POSTGRES_DB");
      POSTGRES_ANALYTICS_TABLE = properties.getProperty("POSTGRES_ANALYTICS_TABLE");
    } catch (IOException ex) {
      logger.warn("Unable to load database properties for analytics", ex);
    }
  }

  private static void connectToDatabase() {
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException ex) {
      logger.warn("Unable to find Postgres driver", ex);
    }

    String DB_URL = "jdbc:postgresql://db:5432/" + POSTGRES_DB;
    try {
      CONNECTION = DriverManager.getConnection(DB_URL,
              POSTGRES_USER, POSTGRES_PASSWORD);
    } catch (SQLException ex) {
      logger.warn("Unable to connect to database", ex);
    }
  }
}
