package org.terrence.testapp.rest;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.ibm.watson.language_translator.v3.LanguageTranslator;
import com.ibm.watson.language_translator.v3.model.TranslateOptions;
import com.ibm.watson.language_translator.v3.model.TranslationResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRestController {

  @Autowired
  protected LanguageTranslator languageTranslator;

  // Test Translation by translating test text

  @RequestMapping(value = "/test", produces = "text/plain")
  public String runTest() {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);

    try {
      pw.println("Beginning test...");
      String testText = "Hello";
      String expectedTranslationTest = "Hola";
      pw.println("Translating text: " + testText);
      TranslateOptions translateOptions = new TranslateOptions.Builder().addText(testText).modelId("en-es").build();

      TranslationResult result = languageTranslator.translate(translateOptions).execute().getResult();

      System.out.println(result);

      if (result.toString().contains(expectedTranslationTest)) {
        pw.println("PASS: Test text: '" + testText + "' translation contains '" + expectedTranslationTest + "'");
      }

    } catch (Exception e) {
      pw.println("FAIL: Unexpected error during test.");
      e.printStackTrace();
    }
    pw.flush();
    return sw.toString();
  }
}