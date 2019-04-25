package org.terrence.testapp.rest;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.stream.Collectors;
import com.ibm.watson.developer_cloud.discovery.v1.Discovery;
import com.ibm.watson.developer_cloud.discovery.v1.model.ListCollectionsOptions;
import com.ibm.watson.developer_cloud.discovery.v1.model.ListCollectionsResponse;
import com.ibm.watson.developer_cloud.discovery.v1.model.ListEnvironmentsOptions;
import com.ibm.watson.developer_cloud.discovery.v1.model.ListEnvironmentsResponse;
import com.ibm.watson.developer_cloud.discovery.v1.model.QueryOptions;
import com.ibm.watson.developer_cloud.discovery.v1.model.QueryResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRestController {

  @Autowired
  protected Discovery discovery;

  StringWriter sw = new StringWriter();
  PrintWriter pw = new PrintWriter(sw);

  // run the test

  @RequestMapping(value = "/test", produces = "text/plain")
  public String runTest() {
    try {
      pw.println("Beginning test...");

      // list existing environments, should be a "system" environment by default

      ListEnvironmentsOptions listEnvironmentOptions = new ListEnvironmentsOptions.Builder().build();
      ListEnvironmentsResponse listEnvironmentResponse = discovery.listEnvironments(listEnvironmentOptions).execute();
      pw.println("Environments: ");
      pw.println(listEnvironmentResponse);

      // list existing collections, should be a "news-en" collection by default

      String environmentId = "system";
      ListCollectionsOptions listCollectionOptions = new ListCollectionsOptions.Builder(environmentId).build();
      ListCollectionsResponse listCollectionsResponse = discovery.listCollections(listCollectionOptions).execute();
      pw.println("Collections: ");
      pw.println(listCollectionsResponse);

      // query collection

      String query = "President";
      pw.println("Query is: '" + query + "'");
      QueryOptions options = new QueryOptions.Builder("system", "news-en").naturalLanguageQuery(query).build();
      QueryResponse queryResponse = discovery.query(options).execute();

      // get results from query
      String results = queryResponse.getResults().stream().map(r -> (String) r.get("title"))
          .collect(Collectors.joining());
      pw.println("Query results: '" + results + "'");

      // check to see if query exists in the results
      if (results.toLowerCase().contains(query.toLowerCase())) {
        pw.println("PASS: Query results contain query");
      } else {
        pw.println("FAIL: Query results do not contain the query");
      }

    } catch (Exception e) {
      pw.println("FAIL: Unexpected error during test.");
      e.printStackTrace();
    }
    pw.flush();
    return sw.toString();
  }
}