# dash-browser-automation
Exposes a REST endpoint for fetching text on a DegreeWorks page

## Instructions
1. Create the Dash network (if not created already) `docker network create dash-net`
2. To enable analytics, configure the database connection properties in `db-configs.properties`
    * Ensure the database is running: [https://github.com/marist-dash/dash-database](https://github.com/marist-dash/dash-database)
3. Build and start the service
    * For development (Maven is installed)
      * `mvn clean package`
      * `docker-compose up -d`
      * For rebuilding the image: `docker-compose up -d --build`
    * For production
      * `docker-compose -f production.yml up -d`
      * For rebuilding the image: `docker-compose -f production.yml up -d --build`

## Notes
* When developing/testing locally, it may be worthwhile to create a separate test project without Spring Boot and Docker.
See [http://chromedriver.chromium.org/getting-started](http://chromedriver.chromium.org/getting-started) for an example.

## Endpoints

### Get DegreeWorks text
Automates the process of fetching an audit from Marist DegreeWorks.
Uses the [Selenium Webdriver](https://www.seleniumhq.org/projects/webdriver/) for automating a human-browser interaction.
The analytical data collected is the webdriver instantiation time and the brower automation execution time.
A typical response time is around **eight seconds**.

**URL**: `/`

**Method**: `POST`

**Port**: `8080`

**Body Parameters**:

| Name | Type | Required | Examples | Notes
| --- | --- | --- | --- | --- |
| username | String | True | george.washington1 | Marist username for authN |
| password | String | True | p@ssw0rd | Marist password for authN |
| analytics | boolean | False | `true`| Send analytics, defaults to `true` |

**Returns**: `String`

```
DWPROD
Student View    AB218SdT as of 28-Aug-2018 at 22:11
Student Washington, George Level Undergraduate
ID 20012345 Degree B.S.
```

### Healthcheck
Returns the date. Useful for checking service availability

**URL**: `/healthcheck`

**Method**: `GET`

**Returns**: `String`
