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
  private final Logger logger = LoggerFactory.getLogger(Analytics.class);

  @Async
  public void send() {
    // build the SQL query
    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append("INSERT INTO ")
            .append(Application.POSTGRES_ANALYTICS_TABLE)
            .append(" (instantiate_time, execute_time, timestamp) VALUES(?, ?, ?);");
    String query = queryBuilder.toString();

    // execute insert
    try {
      PreparedStatement statement = Application.CONNECTION.prepareStatement(query);
      statement.setLong(1, timeToInstantiate);
      statement.setLong(2, timeToExecute);
      statement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
      statement.executeUpdate();
      statement.close();
    } catch (SQLException ex) {
      logger.info("SQL error", ex);
    }
  }

}
