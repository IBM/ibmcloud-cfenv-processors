package org.terrence.testapp.rest;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;

import com.cloudant.client.api.CloudantClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.terrence.testapp.domain.Status;

@RestController
public class TestRestController {

  @Autowired
  private CloudantClient repo;

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
      pw.println(repo.getAllDbs());
    } catch (Exception e) {
      pw.println("FAIL: Unexpected error during test.");
      e.printStackTrace();
    }
    pw.flush();
    return sw.toString();
  }
}