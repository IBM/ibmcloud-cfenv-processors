package org.terrence.testapp.rest;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.terrence.testapp.domain.Status;
import org.terrence.testapp.repositories.StatusRepository;

@RestController
public class TestRestController {
  @Autowired
  private StatusRepository repo;

  // repo methods
  private Status create(Status status) {
    repo.add(status);
    return status;
  }

  private void delete(String id) {
    repo.remove(repo.get(id));
  }

  // test creating a Status object, storing it in the repo and retrieving it
  @GetMapping("/test")
  public String runTest() {
    try {
      Status test = new Status();
      String id = UUID.randomUUID().toString(); // use a random repo id
      System.out.println("Using random repo id: " + id);
      String message = String.format("Message for the object: %s", id);
      test.setId(id);
      test.setMsg(message);

      // verify there is nothing in the repo with the id and then create the test
      // object
      try {
        Status exist = repo.get(id); // this should throw FileNotFoundException if nothing exists
        System.out.println("object already exists, deleting it and then creating new object");
        delete(id);
        create(test);
      } catch (org.ektorp.DocumentNotFoundException d) {
        System.out.println("object does not exist, creating new object");
        create(test);
      }

      // validate the test obj was retrieved

      Status check = repo.get(id);
      if (((check.getId() == null && test.getId() == null)
          || (check.getId() != null && check.getId().equals(test.getId())))
          && ((check.getMsg() == null && test.getMsg() == null)
              || (check.getMsg() != null && check.getMsg().equals(test.getMsg())))) {
        delete(id);
        return "test passed: objects matched!";
      } else {
        delete(id);
        return "test failed: ojects do not match";
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(e);
    }
    return "done";
  }
}