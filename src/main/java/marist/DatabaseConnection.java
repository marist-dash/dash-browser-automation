package marist;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseConnection {

  public Connection connection;
  Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);

  public DatabaseConnection() {
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException ex) {
      logger.warn("Unable to find Postgres driver", ex);
    }

    try {
      connection = DriverManager.getConnection(Application.POSTGRES_URL, Application.POSTGRES_USER, Application.POSTGRES_PASSWORD);
    } catch (SQLException ex) {
      logger.warn("Unable to connect to database", ex);
    }
  }

}
