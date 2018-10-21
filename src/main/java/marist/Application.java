package marist;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@SpringBootApplication
public class Application implements AsyncConfigurer {

  public static String POSTGRES_URL;
  public static String POSTGRES_USER;
  public static String POSTGRES_PASSWORD;
  public static String POSTGRES_DB;
  public static String POSTGRES_ANALYTICS_TABLE;
  public static String POSTGRES_REPORTS_TABLE;
  static Logger logger = LoggerFactory.getLogger(Application.class);

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
    loadConfigs();
  }

  @Bean(name = "asyncExecutor")
  public Executor asyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(2);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("AsyncSendAnalytics--");
    executor.initialize();
    return executor;
  }

  @Bean
  public CorsFilter corsFilter() {
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    final CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.setAllowedOrigins(Collections.singletonList("*"));
    config.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept"));
    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH"));
    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
  }

  private static void loadConfigs() {
    Properties properties = new Properties();
    InputStream inputStream;
    try {
      inputStream = new FileInputStream("db-configs.properties");
      properties.load(inputStream);
      POSTGRES_URL = properties.getProperty("POSTGRES_URL");
      POSTGRES_USER = properties.getProperty("POSTGRES_USER");
      POSTGRES_PASSWORD = properties.getProperty("POSTGRES_PASSWORD");
      POSTGRES_DB = properties.getProperty("POSTGRES_DB");
      POSTGRES_ANALYTICS_TABLE = properties.getProperty("POSTGRES_ANALYTICS_TABLE");
      POSTGRES_REPORTS_TABLE = properties.getProperty("POSTGRES_REPORT_TABLE");
      
    } catch (IOException ex) {
      logger.warn("Unable to load database properties for analytics", ex);
    }
  }
}
