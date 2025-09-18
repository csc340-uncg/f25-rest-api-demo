package com.csc340.rest_api_demo;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * REST API Controller
 * Handles all API endpoints/ CRUD operations.
 */

@RestController
@RequestMapping("/api")
public class RestApiController {
  Logger logger = Logger.getLogger(RestApiController.class.getName());

  Map<Integer, Student> studentDatabase = new HashMap<>();

  /**
   * Hello World API endpoint.
   *
   * @return response string.
   */
  @GetMapping("/hello")
  public String hello() {
    return "Hello, World!";
  }

  /**
   * Greeting API endpoint.
   *
   * @param name the request parameter
   * @return the response string.
   */
  @GetMapping("/greeting")
  public String greeting(@RequestParam(value = "name", defaultValue = "Dora") String name) {
    return "Hola, soy " + name;
  }

  /**
   * List all students.
   *
   * @return the list of students.
   */
  @GetMapping("students/all")
  public Object getAllStudents() {
    if (studentDatabase.isEmpty()) {
      studentDatabase.put(1, new Student(1, "sample1", "csc", 3.86));
    }
    return studentDatabase.values();
  }

  /**
   * Get one student by Id
   *
   * @param id the unique student id.
   * @return the student.
   */
  @GetMapping("students/{id}")
  public Student getStudentById(@PathVariable int id) {
    return studentDatabase.get(id);
  }

  /**
   * Create a new Student entry.
   *
   * @param student the new Student
   * @return the List of Students.
   */
  @PostMapping("students/create")
  public Object createStudent(@RequestBody Student student) {
    studentDatabase.put(student.getId(), student);
    return studentDatabase.values();
  }

  /**
   * Delete a Student by id
   *
   * @param id the id of student to be deleted.
   * @return the List of Students.
   */
  @DeleteMapping("students/delete/{id}")
  public Object deleteStudent(@PathVariable int id) {
    studentDatabase.remove(id);
    return studentDatabase.values();
  }

  /**
   * Get a list of breweries by city and make them available at our own API
   * endpoint.
   *
   * @return a list of Breweries.
   */
  @GetMapping("/breweries")
  public Object getBreweries(@RequestParam(value = "city", defaultValue = "greensboro") String city) {
    /*
     * The API endpoint is
     * https://api.openbrewerydb.org/v1/breweries?by_city=greensboro
     */
    try {
      // CONSUMING A RESTFUL WEB SERVICE (API)
      String url = "https://api.openbrewerydb.org/v1/breweries?by_city=" + city;
      RestTemplate restTemplate = new RestTemplate();
      ObjectMapper mapper = new ObjectMapper();

      String jsonListResponse = restTemplate.getForObject(url, String.class);
      JsonNode root = mapper.readTree(jsonListResponse);

      // The response from the above API is a JSON Array, which we loop through.
      for (JsonNode rt : root) {
        // Extract relevant info from the response and use it for what you want
        String name = rt.get("name").asText();
        String address = rt.get("address_1").asText();
        String type = rt.get("brewery_type").asText();

        logger.log(Level.INFO, "Name: " + name);
        logger.log(Level.INFO, "Address: " + address);
        logger.log(Level.INFO, "Type: " + type);
        logger.log(Level.INFO, "------------------------");

      }
      return root;
    } catch (JsonProcessingException ex) {
      Logger.getLogger(RestApiController.class.getName()).log(Level.SEVERE,
          null, ex);
      return "error in /brew";
    }
  }

  /**
   * Get info about a fruit from fruityVice.
   *
   * @param fruitName
   * @return a Fruit object.
   */
  @GetMapping("/fruit")
  public Object getFruit(@RequestParam(value = "name", defaultValue = "kiwi") String fruitName) {
    try {
      // CONSUMING A RESTFUL WEB SERVICE (API)
      String url = "https://www.fruityvice.com/api/fruit/" + fruitName;
      RestTemplate restTemplate = new RestTemplate();
      ObjectMapper mapper = new ObjectMapper();

      /**
       * The response from the above API is a 'single' JSON object that looks like
       * this:
       *
       * <pre>
       *     {
       *     "name": "Kiwi",
       *     "id": 66,
       *     "family": "Actinidiaceae",
       *     "order": "Struthioniformes",
       *     "genus": "Apteryx",
       *     "nutritions": {
       *         "calories": 61,
       *         "fat": 0.5,
       *         "sugar": 9.0,
       *         "carbohydrates": 15.0,
       *         "protein": 1.1
       *     }
       * }
       * </pre>
       *
       * I know this because I TESTED THE FRUITYVICE API IN THUNDERCLIENT
       */
      String jsonFruitResponse = restTemplate.getForObject(url, String.class);
      JsonNode root = mapper.readTree(jsonFruitResponse);

      String name = root.get("name").asText();
      String family = root.get("family").asText();
      // note the calries are nested in the nutritions objects.
      int calories = root.get("nutritions").get("calories").asInt();
      logger.log(Level.INFO, "Name: " + name);
      logger.log(Level.INFO, "Family: " + family);
      logger.log(Level.INFO, "Calories: " + calories);

      return root;

    } catch (JsonProcessingException ex) {
      Logger.getLogger(RestApiController.class.getName()).log(Level.SEVERE,
          null, ex);
      return "error in /fruit";
    }
  }

}
