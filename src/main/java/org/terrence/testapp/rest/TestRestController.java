package org.terrence.testapp.rest;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechRecognitionResults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRestController {

  @Autowired
  protected SpeechToText speechToText;

  StringWriter sw = new StringWriter();
  PrintWriter pw = new PrintWriter(sw);

  // run the test

  @RequestMapping(value = "/test", produces = "text/plain")
  public String runTest() {
    try {
      pw.println("Beginning test...");

      InputStream inputSteam = this.getClass().getResourceAsStream("/audio-file.flac");
      RecognizeOptions recognizeOptions = new RecognizeOptions.Builder().audio(inputSteam).contentType("audio/flac")
          .model("en-US_BroadbandModel").maxAlternatives(3).build();

      SpeechRecognitionResults speechRecognitionResults = speechToText.recognize(recognizeOptions).execute();
      String results = speechRecognitionResults.toString();
      pw.println("Recognition results: '" + results + "'");

      // check to see if query exists in the results
      String keyword = "Colorado";
      if (results.toLowerCase().contains(keyword.toLowerCase())) {
        pw.println("PASS: Recognition results contain keyword: '" + keyword + "'");
      } else {
        pw.println("FAIL: Recognition results do not contain keyword: '" + keyword + "'");
      }

    } catch (Exception e) {
      pw.println("FAIL: Unexpected error during test.");
      e.printStackTrace();
    }
    pw.flush();
    return sw.toString();
  }
}