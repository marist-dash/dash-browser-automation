package marist;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

public class Report {

  private final int degreeWorksHash;
  public String degreeWorksText;
  Logger logger = LoggerFactory.getLogger(Report.class);

  public Report(String degreeWorksText) {
    this.degreeWorksHash = degreeWorksText.hashCode();
    this.degreeWorksText = degreeWorksText;
    scrubPII();
  }

  // removes personally identifiable information from DegreeWorks text
  private void scrubPII() {
    String[] lines = this.degreeWorksText.split("\\n");

    // scrub name from line 3 (index 2)
    List<String> nameLine = Arrays.asList(lines[2].split(" "));
    StringBuilder sb = new StringBuilder("Student LASTNAME, FIRSTNAME Level ");
    sb.append(nameLine.get(nameLine.size() - 1));
    lines[2] = sb.toString();

    // scrub CWID from line 4 (index 3)
    List<String> CWIDLine = Arrays.asList(lines[3].split(" "));
    sb = new StringBuilder("ID 12345678 Degree ");
    sb.append(CWIDLine.get(CWIDLine.size() - 1));
    lines[3] = sb.toString();

    // recreate the String from String[]
    sb = new StringBuilder();
    for (String line : lines) {
      sb.append(line).append("\n");
    }
    this.degreeWorksText = sb.toString();
  }

  @Async("asyncExecutor")
  public void send() {
    if (this.degreeWorksText == null || this.degreeWorksHash == 0) {
      return;
    }

    // create new database connection
    DatabaseConnection postgres = new DatabaseConnection();

    // build query - check number of records with same DegreeWorks hash value
    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append("SELECT COUNT(1) FROM ")
            .append(Application.POSTGRES_REPORTS_TABLE)
            .append(" WHERE degreeworks_hash = ?");
    String query = queryBuilder.toString();

    int hashCount = 0;

    // submit query
    try (PreparedStatement statement = postgres.connection.prepareStatement(query)) {
      statement.setInt(1, degreeWorksHash);
      ResultSet result = statement.executeQuery();
      while (result.next()) {
        hashCount = result.getInt(1);
      }
    } catch (SQLException ex) {
      logger.warn("SQL error when querying DegreeWorks text hash value", ex);
    }

    if (hashCount > 0) {
      // DegreeWorks text already exists
      return;
    }
    // DegreeWorks text doesn't exist, insert new record

    // build SQL query
    queryBuilder.setLength(0);
    queryBuilder.append("INSERT INTO ")
            .append(Application.POSTGRES_REPORTS_TABLE)
            .append(" (degreeworks_hash, degreeworks_text) VALUES(?, ?);");
    query = queryBuilder.toString();

    // insert DegreeWorks text
    try (PreparedStatement statement = postgres.connection.prepareStatement(query)) {
      statement.setInt(1, this.degreeWorksHash);
      statement.setString(2, this.degreeWorksText);
      statement.executeUpdate();
    } catch (SQLException ex) {
      logger.warn("SQL error when adding DegreeWorks text and hash value", ex);
    }
  }

}
