package org.terrence.testapp.rest;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.ibm.watson.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.natural_language_understanding.v1.model.CategoriesOptions;
import com.ibm.watson.natural_language_understanding.v1.model.Features;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRestController {

  @Autowired
  protected NaturalLanguageUnderstanding naturalLanguageUnderstanding;

  // Test analysis with test text

  @RequestMapping(value = "/test", produces = "text/plain")
  public String runTest() {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);

    try {
      pw.println("Beginning test...");
      String testText = "IBM Cloud is a suite of cloud computing services from IBM that offers both platform as a service (PaaS) and infrastructure as a service (IaaS).";
      String expectedCategoryText = "technology";
      pw.println("Translating text: " + testText);

      CategoriesOptions categories = new CategoriesOptions.Builder().limit(3).build();

      Features features = new Features.Builder().categories(categories).build();

      AnalyzeOptions parameters = new AnalyzeOptions.Builder().text(testText).features(features).build();

      AnalysisResults response = naturalLanguageUnderstanding.analyze(parameters).execute().getResult();
      System.out.println(response);

      if (response.toString().contains(expectedCategoryText)) {
        pw.println("PASS: Test text: '" + testText + "' analysis result contains '" + expectedCategoryText + "'");
      }

    } catch (Exception e) {
      pw.println("FAIL: Unexpected error during test.");
      e.printStackTrace();
    }
    pw.flush();
    return sw.toString();
  }
}