package org.terrence.testapp.rest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.SynthesizeOptions;
import com.ibm.watson.developer_cloud.text_to_speech.v1.util.WaveUtils;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRestController {

  @Autowired
  protected TextToSpeech textToSpeech;

  // transcribe sample text and save as wav file

  @RequestMapping(value = "/test", produces = "text/plain")
  public String runTest() {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);

    try {
      pw.println("Beginning test...");
      SynthesizeOptions synthesizeOptions = new SynthesizeOptions.Builder().text("One two one two this is just a test")
          .accept("audio/wav").voice("en-US_AllisonVoice").build();
      InputStream inputStream = textToSpeech.synthesize(synthesizeOptions).execute();
      pw.println("Text translated");
      InputStream in = WaveUtils.reWriteWaveHeader(inputStream);
      OutputStream out = new FileOutputStream("/tmp/test.wav");
      byte[] buffer = new byte[1024];
      int length;
      while ((length = in.read(buffer)) > 0) {
        out.write(buffer, 0, length);
      }

      out.close();
      in.close();
      inputStream.close();
      pw.println("Translation File test.wav created");

    } catch (Exception e) {
      pw.println("FAIL: Unexpected error during test.");
      e.printStackTrace();
    }
    pw.flush();
    return sw.toString();
  }

  // get the wav file and download it to client system

  @GetMapping(value = "/get", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public @ResponseBody byte[] getFile() throws IOException {
    FileInputStream in = new FileInputStream("/tmp/test.wav");
    if (in != null) {
      return IOUtils.toByteArray(in);
    } else {
      throw new FileNotFoundException("/tmp/test.wav");
    }
  }
}