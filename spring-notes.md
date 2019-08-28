## Spring-Boot Notes

#### Q. What are the advantages of using Spring Boot?
* It is very easy to develop Spring Based applications with Java or Groovy.
* It reduces lots of development time and increases productivity.
* It avoids writing lots of boilerplate Code, Annotations and XML Configuration.
* It is very easy to integrate Spring Boot Application with its Spring Ecosystem like Spring JDBC, Spring ORM, Spring Data, * Spring Security etc.
* It follows “Opinionated Defaults Configuration” Approach to reduce Developer effort
* It provides Embedded HTTP servers like Tomcat, Jetty etc. to develop and test our web applications very easily.
* It provides CLI (Command Line Interface) tool to develop and test Spring Boot (Java or Groovy) Applications from command * prompt very easily and quickly.
* It provides lots of plugins to develop and test Spring Boot Applications very easily using Build Tools like Maven and Gradle
* It provides lots of plugins to work with embedded and in-memory Databases very easily.

#### Q. Write a program in Spring-Boot to get employees details based on employee id?
*TODO*
#### Q. What does the @RestController, @RequestMapping, @RequestParam, @ContextConfiguration, @ResponseBody, @Configuration, @pathVariable, @ResponseEntity, @Qualifier, @Required annotation do?
* **@RestController**: The @RestController is a stereotype annotation. It adds `@Controller` and `@ResponseBody` annotations to the class. It requires to import `org.springframework.web.bind.annotation` package.
The @RestController annotation informs to the Spring to render the result back to the caller.
```java
import org.springframework.web.bind.annotation.RestController;  
@RestController  // using @RestController annotation  
public class HomeController {  
    // controller body  
}  
```
* **@RequestMapping**: The @RequestMapping annotation is used to provide routing information. It tells to the Spring that any HTTP request should map to the corresponding method. It requires to import `org.springframework.web.annotation` package.
Example: Here method index() should map with `/index` url
```java
import org.springframework.web.bind.annotation.RequestMapping;  
import org.springframework.web.bind.annotation.RestController;  
@RestController  
public class HomeController {  
    @RequestMapping(value = "/index", method = "GET")  
    public String index() {  
        return "Dashboard Page!";  
    }  
}  
```
* **@RequestParam**: @RequestParam is a Spring annotation used to bind a web request parameter to a method parameter.
It has the following optional elements:  

  * **defaultValue**: used as a fallback when the request parameter is not provided or has an empty value
  * **name**: name of the request parameter to bind to
  * **required**: tells whether the parameter is required
  * **value**: alias for name

1. A Simple Mapping
```java
@GetMapping("/api/foos")
@ResponseBody
public String getFoos(@RequestParam String id) {
    return "ID: " + id;
}
```
Output
```
http://localhost:8080/api/foos?id=abc
----
ID: abc
```
2. Specifying the Request Parameter Name
```java
@PostMapping("/api/foos")
@ResponseBody
public String addFoo(@RequestParam(name = "id") String fooId, @RequestParam String name) { 
    return "ID: " + fooId + " Name: " + name;
}
```
3. Making an Optional Request Parameter
```java
@GetMapping("/api/foos")
@ResponseBody
public String getFoos(@RequestParam(required = false) String id) { 
    return "ID: " + id;
}
```
Output
```
http://localhost:8080/api/foos?id=abc
----
ID: abc


http://localhost:8080/api/foos
----
ID: null
```
4. A Default Value for the Request Parameter
```java
@GetMapping("/api/foos")
@ResponseBody
public String getFoos(@RequestParam(defaultValue = "test") String id) {
    return "ID: " + id;
}
```
Output
```
http://localhost:8080/api/foos
----
ID: test


http://localhost:8080/api/foos?id=abc
----
ID: abc
```
5. Mapping All Parameters
```java
@PostMapping("/api/foos")
@ResponseBody
public String updateFoos(@RequestParam Map<String,String> allParams) {
    return "Parameters are " + allParams.entrySet();
}
```
Output
```
curl -X POST -F 'name=abc' -F 'id=123' http://localhost:8080/api/foos
-----
Parameters are {[name=abc], [id=123]}
```
6. Mapping a Multi-Value Parameter
```java
@GetMapping("/api/foos")
@ResponseBody
public String getFoos(@RequestParam List<String> id) {
    return "IDs are " + id;
}
```
Output
```
http://localhost:8080/api/foos?id=1,2,3
----
IDs are [1,2,3]

http://localhost:8080/api/foos?id=1&id=2
----
IDs are [1,2]
```

* **@ContextConfiguration**: This annotation specifies how to load the application context while writing a unit test for the Spring environment. Here is an example of using @ContextConfiguration along with @RunWith annotation of JUnit to test a Service class in Spring Boot.
```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=PaymentConfiguration.class)
public class PaymentServiceTests {

  @Autowired
  private PaymentService paymentService;

  @Test
  public void testPaymentService() {
      // code to test PaymentService class
  }
}
```
Here, `@ContextConfiguration` class instructs to load the Spring application context defined in the PaymentConfiguration class.
#### Q. What are the different components of a Spring Boot application?
#### Q. What does @SpringBootApplication and @EnableAutoConfiguration do? 
* **@SpringBootApplication**: annotation is used to annotate the main class of our Spring Boot application. It also enables the auto-configuration feature of Spring Boot.
```java
@SpringBootApplication
public class SpringBootDemo {
   public static void main(String args[]) {
      SpringApplication.run(SpringBootDemo.class, args);
   }
}
```
* **@EnableAutoConfiguration**: The auto-configuration feature automatically configures things if certain classes are present in the Classpath e.g. if thymeleaf.jar is present in the Classpath then it can automatically configure Thymeleaf `TemplateResolver` and `ViewResolver`.
```java
@Configuration
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class SpringBootDemo {
  //.. Java code
}
```
#### Q. How to reload changes on Spring Boot without having to restart server?  
#### Q. What is Auto Configuration?
#### Q. How does Spring Boot enforce common dependency management for all its Starter projects?
#### Q. What is Spring Initializr?
#### Q. What is a profile? How do you create application configuration for a specific profile?
#### Q. How do you have different configuration for different environments?
#### Q. What is Spring Boot Actuator? How do you monitor web services using Spring Boot Actuator?
#### Q. What is a CommandLineRunner?
#### Q. What is Spring JDBC? How is different from JDBC?
#### Q. What is Mockito?
#### Q. What is @SpringBootTest?
#### Q. What is TestRestTemplate?
#### Q. What is JavaConfig?
#### Q. How to depoy Spring Boot application as a WAR?
#### Q. What is Docker? How to deploy Spring Boot Applications to Docker?
#### Q. How to disable Actuator endpoint security in Spring Boot?
#### Q. What is ELK stack?How to use it with Spring Boot?
#### Q. Have you written Test cases using Spring Boot?
#### Q. How to implement security for Spring Boot application?
#### Q. Have you integrated Spring Boot and ActiveMQ?
#### Q. How to implement Pagination and Sorting with Spring Boot?
#### Q. What is Swagger? Have you implemented it using Spring Boot?
#### Q. What is Spring Batch? How do you implement it using Spring Boot?
#### Q. What is FreeMarker Template? How do you implement it using Spring Boot?
#### Q. How to implement Exception Handling using Spring Boot?
#### Q. What is caching? Have you used any caching framework with Spring Boot?
#### Q. How did you perform database operations using Spring Boot?
#### Q. How to develop a full stack application using Spring Boot and Angular?
#### Q. How to implement interceptors with Spring Boot?
#### Q. How to use schedulers with Spring Boot?
#### Q. Which all starter maven dependencies have you used?
#### Q. What is CSRF attack? How to enable CSRF protection against it?
#### Q. How to use Form Login Authentication using Spring Boot?
#### Q. What is OAuth2? How to implement it using Spring Boot?
#### Q. What is GZIP? How to implement it using Spring Boot?
#### Q. When will you use WebSockets? How tto implement it using Spring Boot?
#### Q. What is AOP? How to use it with Spring Boot?
#### Q. How can we monitor all the Spring Boot Microservices?
#### Q. What is Spring Boot transaction management?
#### Q. What is CORS in Spring Boot? How to enable CORS in Spring Boot?
#### Q. What is Spring Boot devtools?
The aim of this module is to try and improve the development-time experience when working on Spring Boot applications.
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
    </dependency>
</dependencies>
```
#### Q. What is best way to expose custom application configuration with Spring Boot?
#### Q. How do you configure error logging/debugging in Spring Boot application?
#### Q. How do you Enable HTTP response compression in Spring Boot?
#### Q. What is the configuration file name, which is used by Spring Boot?
`application.properties`
#### Q. What is difference Between an Embedded Container and a War?


