package org.terrence.testapp.rest;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.ibm.watson.assistant.v1.Assistant;
import com.ibm.watson.assistant.v1.model.CreateIntentOptions;
import com.ibm.watson.assistant.v1.model.CreateWorkspaceOptions;
import com.ibm.watson.assistant.v1.model.DeleteWorkspaceOptions;
import com.ibm.watson.assistant.v1.model.Example;
import com.ibm.watson.assistant.v1.model.GetWorkspaceOptions;
import com.ibm.watson.assistant.v1.model.Intent;
import com.ibm.watson.assistant.v1.model.ListWorkspacesOptions;
import com.ibm.watson.assistant.v1.model.MessageInput;
import com.ibm.watson.assistant.v1.model.MessageOptions;
import com.ibm.watson.assistant.v1.model.MessageResponse;
import com.ibm.watson.assistant.v1.model.RuntimeIntent;
import com.ibm.watson.assistant.v1.model.Workspace;
import com.ibm.watson.assistant.v1.model.WorkspaceCollection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRestController {

  @Autowired
  protected Assistant assistant;

  // Test Assistant by creating a workspace with and intent and then analyze text

  @RequestMapping(value = "/test", produces = "text/plain")
  public String runTest() {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);

    try {
      pw.println("Beginning test...");

      // create workspace

      pw.println("Creating Workspace...");

      String workspaceName = "API test";
      String workspaceDescription = "Example workspace created via API";

      CreateWorkspaceOptions createWorkspaceOptions = new CreateWorkspaceOptions.Builder().name(workspaceName)
          .description(workspaceDescription).build();

      Workspace response = assistant.createWorkspace(createWorkspaceOptions).execute().getResult();
      System.out.println("Create Workspace Response: " + response);
      String workspaceId = response.getWorkspaceId();
      pw.println("Workspace created with WorkspaceId: " + workspaceId);

      // create intent

      pw.println("Creating Intent...");
      String intent = "Hello";

      List<Example> examples = new ArrayList<Example>();
      examples.add(new Example.Builder("Good morning").build());
      examples.add(new Example.Builder("Hi there").build());
      examples.add(new Example.Builder("Hello").build());

      CreateIntentOptions createIntentOptions = new CreateIntentOptions.Builder(workspaceId, intent).examples(examples)
          .build();

      Intent intentResponse = assistant.createIntent(createIntentOptions).execute().getResult();
      System.out.println("Create Intent Response: " + intentResponse);
      String intentName = intentResponse.getIntent();
      pw.println("Intent Created with name: " + intentName);

      // create and set RuntimeIntent

      RuntimeIntent runtimeIntent = new RuntimeIntent();
      runtimeIntent.setIntent(intent);

      List<RuntimeIntent> intents = new ArrayList<RuntimeIntent>();
      intents.add(runtimeIntent);

      // list workspaces

      ListWorkspacesOptions listWorkspaceOptions = new ListWorkspacesOptions.Builder().build();

      WorkspaceCollection workspaces = assistant.listWorkspaces(listWorkspaceOptions).execute().getResult();

      System.out.println("List Workspaces: " + workspaces);

      // workspace info

      GetWorkspaceOptions getWorkspaceOptions = new GetWorkspaceOptions.Builder(workspaceId).build();

      Workspace getWorkspaceResponse = assistant.getWorkspace(getWorkspaceOptions).execute().getResult();

      System.out.println("Workspace info for workspace: " + workspaceId + ": " + getWorkspaceResponse);

      // analyze text

      String testText = "Hi";
      pw.println("Analyzing text of: '" + testText + "'");

      MessageInput input = new MessageInput();
      input.setText(testText);

      MessageOptions messageOptions = new MessageOptions.Builder(workspaceId).input(input).intents(intents).build();

      MessageResponse messageResponse = assistant.message(messageOptions).execute().getResult();

      System.out.println("Watson Analysis of the test text of: '" + testText + "' is: " + messageResponse);

      if (messageResponse.toString().contains(intentName)) {
        pw.println("PASS: Found the expected intent: '" + intentName + "' in the response");
      }

      // delete workspace

      DeleteWorkspaceOptions deleteWorkspaceOptions = new DeleteWorkspaceOptions.Builder(workspaceId).build();

      assistant.deleteWorkspace(deleteWorkspaceOptions).execute();

    } catch (Exception e) {
      pw.println("FAIL: Unexpected error during test.");
      e.printStackTrace();
    }
    pw.flush();
    return sw.toString();
  }
}