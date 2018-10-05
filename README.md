# dash-browser-automation
Exposes a REST endpoint for fetching text on a DegreeWorks page

## Instructions
1. Create the Dash network (if not created already) `docker network create dash-net`
2. Edit the database connection properties in `db-configs.properties`
3. Build and start the service
    * For development (Maven is installed)
      * `mvn clean package`
      * `docker-compose up -d`
    * For production
      * `docker-compose -f production.yml up -d`
      
## Endpoints

### Get DegreeWorks text
Automates the process of going to iLearn ➡ DegreeWorks ➡ Authenticating ➡ Copying+Pasting text.
Uses [Selenium Webdriver](https://www.seleniumhq.org/projects/webdriver/) for automating a human-browser interaction.
The analytical data collected is the webdriver instantiation time and the brower automation execution time.

**URL**: `/`

**Method**: `POST`

**Body Parameters**:

| Name | Type | Required | Examples | Notes
| --- | --- | --- | --- | --- |
| username | String | True | george.washington1 | Marist username for authN |
| password | String | True | p@ssw0rd | Marist password for authN |
| analytics | boolean | False | `true`| Send analytics, defaults to `true` |

**Returns**:

```
{
  "degreeWorksText": "DWPROD\nStudent View..."
}
```

### Healthcheck
Returns the date. Useful for checking service availability

**URL**: `/healthcheck`

**Method**: `GET`
