package org.terrence.testapp.rest;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;

import com.ibm.watson.developer_cloud.discovery.v1.Discovery;
import com.ibm.watson.developer_cloud.discovery.v1.model.QueryOptions;
import com.ibm.watson.developer_cloud.discovery.v1.model.QueryResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.terrence.testapp.domain.Status;

@RestController
public class TestRestController {

  @Autowired
  private Discovery discovery;

  StringWriter sw = new StringWriter();
  PrintWriter pw = new PrintWriter(sw);

  // create test object with random id and fixed message
  String id = UUID.randomUUID().toString();
  Status test = new Status();

  // run the test
  @RequestMapping(value = "/test", produces = "text/plain")
  public String runTest() {
    try {
      pw.println("Beginning test...");
      pw.println(discovery.listEnvironments());
    } catch (Exception e) {
      pw.println("FAIL: Unexpected error during test.");
      e.printStackTrace();
    }
    pw.flush();
    return sw.toString();
  }

  // String discover(@RequestParam String query) {
  // QueryOptions options =
  // new QueryOptions.Builder( environmentId: "system", collectionId: "news")
  // .naturalLanguageQuery(query)
  // .build();
  // QueryResponse queryResponse = discovery.query(options).execute();

  // String titles = queryResponse.getResults().stream().map(r -> (String)
  // r.get("title")).collect(Collector.joining( delimiter: "\n<p>"));
  // return titles;

  // }

}