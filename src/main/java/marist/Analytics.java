package marist;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

public class Analytics {

  public long timeToInstantiate;
  public long timeToExecute;

  Logger logger = LoggerFactory.getLogger(Analytics.class);

  @Async("asyncExecutor")
  public void send() {
    // build SQL query
    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append("INSERT INTO ")
            .append(Application.POSTGRES_ANALYTICS_TABLE)
            .append(" (instantiate_time, execute_time, timestamp) VALUES(?, ?, ?);");
    String query = queryBuilder.toString();

    // create new database connection
    DatabaseConnection postgres = new DatabaseConnection();

    // insert WebDriver analytics into database
    try (PreparedStatement statement = postgres.connection.prepareCall(query)) {
      statement.setLong(1, timeToInstantiate);
      statement.setLong(2, timeToExecute);
      statement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
      statement.executeUpdate();
    } catch (SQLException ex) {
      logger.warn("SQL error when adding analytics", ex);
    }
  }

}
