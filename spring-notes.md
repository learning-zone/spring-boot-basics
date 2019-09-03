## Spring Boot Interview Questions and Answers

|Sl.No|Spring Boot Questions|
|------|--------------------|
|01. |[What are the advantages of using Spring Boot?](#q-what-are-the-advantages-of-using-spring-boot)|
|02. |[Write a program in Spring-Boot to get employees details based on employee id?](#q-write-a-program-in-spring-boot-to-get-employees-details-based-on-employee-id)|
|03. |[What does the @RestController, @RequestMapping, @RequestParam, @ContextConfiguration, @ResponseBody, @pathVariable, @ResponseEntity, @Qualifier annotation do?](#q-what-does-the-restcontroller-requestmapping-requestparam-contextconfiguration-responsebody-pathvariable-responseentity-qualifier-annotation-do)|
|04. |[What are the different components of a Spring Boot application?](#q-what-are-the-different-components-of-a-spring-boot-application)|
|05. |[What does @SpringBootApplication and @EnableAutoConfiguration do?](#q-what-does-springbootapplication-and-enableautoconfiguration-do)|
|06. |[What is Spring Boot initializr?](#q-what-is-spring-boot-initializr)|
|07. |[What is a profile? How do you create application configuration for a specific profile?](#q-what-is-a-profile-how-do-you-create-application-configuration-for-a-specific-profile)|
|08. |[What is Spring Boot Actuator? How do you monitor web services using Spring Boot Actuator?](#q-what-is-spring-boot-actuator-how-do-you-monitor-web-services-using-spring-boot-actuator)|
|09. |[What is a CommandLineRunner and ApplicationRunner?](#q-what-is-a-commandlinerunner-and-applicationrunner)|
|10. |[What is Docker? How to deploy Spring Boot Application to Docker?](#q-what-is-docker-how-to-deploy-spring-boot-application-to-docker)|
|11. |[How to implement Exception Handling in Spring Boot?](#q-how-to-implement-exception-handling-in-spring-boot)|
|12. |[What is caching? Have you used any caching framework with Spring Boot?](#q-what-is-caching-have-you-used-any-caching-framework-with-spring-boot)|
|13. |[What is Swagger? Have you implemented it using Spring Boot?](#q-what-is-swagger-have-you-implemented-it-using-spring-boot)|
|14. |[How to implement Pagination and Sorting in Spring Boot?](#q-how-to-implement-pagination-and-sorting-in-spring-boot)|
|15. |[How to use schedulers in Spring Boot?](#q-how-to-use-schedulers-in-spring-boot)|
|16. |[How to provide security to spring boot application?](#q-how-to-provide-security-to-spring-boot-application)|
|17. |[What is CORS in Spring Boot? How to enable CORS in Spring Boot?](#q-what-is-cors-in-spring-boot-how-to-enable-cors-in-spring-boot)|
|18. |[What is CSRF attack? How to enable CSRF protection against it?](#q-what-is-csrf-attack-how-to-enable-csrf-protection-against-it)|
|19. |[How do you configure error logging/debugging in Spring Boot application?](#q-how-do-you-configure-error-logging-debugging-in-spring-boot-application)|
|20. |[What is Spring Batch? How do you implement it using Spring Boot?](#q-what-is-spring-batch-how-do-you-implement-it-using-spring-boot)|
|21. |[How to implement interceptor with Spring Boot?](#q-how-to-implement-interceptor-with-spring-boot)|
|22. |[How to use Form Login Authentication using Spring Boot?](#q-how-to-use-form-login-authentication-using-spring-boot)|
|23. |[What is Spring Boot transaction management?](#q-what-is-spring-boot-transaction-management)|
|24. |[What is FreeMarker Template? How do you implement it using Spring Boot?](#q-what-is-freemarker-template-how-do-you-implement-it-using-spring-boot)|
|25. |[Which all starter maven dependencies have you used?](#q-which-all-starter-maven-dependencies-have-you-used)|
|26. |[What is GZIP? How to implement it using Spring Boot?](#q-what-is-gzip-how-to-implement-it-using-spring-boot)|
|27. |[When will you use WebSockets? How to implement it using Spring Boot?](#q-when-will-you-use-websockets-how-to-implement-it-using-spring-boot)|
|28. |[What is Spring Boot devtools?](#q-what-is-spring-boot-devtools)|
|29. |[What is best way to expose custom application configuration with Spring Boot?](#q-what-is-best-way-to-expose-custom-application-configuration-with-spring-boot)|
|30. |[How do you Enable HTTP response compression in Spring Boot?](#q-how-do-you-enable-http-response-compression-in-spring-boot)|
|31. |[What is the configuration file name, which is used by Spring Boot?](#q-what-is-the-configuration-file-name-which-is-used-by-spring-boot)|
|32. |[What is difference Between an Embedded Container and a War?](#q-what-is-difference-between-an-embedded-container-and-a-war)|
|33. |[What is Mockito?](#q-what-is-mockito)|
|34. |[What is @SpringBootTest?](#q-what-is-springboottest)|
|35. |[What is TestRestTemplate?](#q-what-is-testresttemplate)|
|36. |[What is JavaConfig?](#q-what-is-javaconfig)|
|37. |[Have you written Test cases using Spring Boot?](#q-have-you-written-test-cases-using-spring-boot)|


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
#### Q. What does the @RestController, @RequestMapping, @RequestParam, @ContextConfiguration, @ResponseBody, @pathVariable, @ResponseEntity, @Qualifier annotation do?
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

* **@ResponseBody**: The @ResponseBody annotation tells a controller that the object returned is automatically serialized into JSON and passed back into the HttpResponse object.
```java
@Controller
@RequestMapping("/post")
public class ExamplePostController {
 
    @Autowired
    ExampleService exampleService;
 
    @PostMapping("/response")
    @ResponseBody
    public ResponseTransfer postResponseController(
      @RequestBody LoginForm loginForm) {
        return new ResponseTransfer("Thanks For Posting!!!");
     }
}
```
Output
```
{"text":"Thanks For Posting!!!"}
```

* **@pathVariable**: @PathVariable is a Spring annotation which indicates that a method parameter should be bound to a URI template variable.
It has the following optional elements:

  * **name**: name of the path variable to bind to
  * **required**: tells whether the path variable is required
  * **value**: alias for name
```java
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    @RequestMapping(path="/{name}/{age}")
    public String getMessage(@PathVariable("name") String name, 
            @PathVariable("age") String age) {
        
        var msg = String.format("%s is %s years old", name, age);
        return msg;
    }
}
```
* **@ResponseEntity**: ResponseEntity represents an HTTP response, including headers, body, and status. While `@ResponseBody` puts the return value into the body of the response, ResponseEntity also allows us to add headers and status code.
```java
@GetMapping("/customHeader")
ResponseEntity<String> customHeader() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Custom-Header", "foo");
         
    return new ResponseEntity<>(
      "Custom header set", headers, HttpStatus.OK);
}
```
* **@Qualifier**: Spring Boot `@Qualifier` shows how to differentiate beans of the same type with @Qualifier. It can also be used to annotate other custom annotations that can then be used as qualifiers.
```java
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("manager")
public class Manager implements Person {

    @Override
    public String info() {
        return "Manager";
    }
}
```
#### Q. What are the different components of a Spring Boot application?
Spring Boot Framework has mainly four major components.

* **Spring Boot Starters**: The main responsibility of Spring Boot Starter is to combine a group of common or related dependencies into single dependencies. Spring Boot starters can help to reduce the number of manually added dependencies just by adding one dependency. So instead of manually specifying the dependencies just add one starter. Examples are spring-boot-starter-web, spring-boot-starter-test, spring-boot-starter-data-jpa, etc.

* **Spring Boot AutoConfigurator**: One of the common complaint with Spring is, we need to make lot of XML based configurations. Spring Boot AutoConfigurator will simplify all these XML based configurations. It also reduces the number of annotations.

* **Spring Boot CLI**: Spring Boot CLI(Command Line Interface) is a Spring Boot software to run and test Spring Boot applications from command prompt. When we run Spring Boot applications using CLI, then it internally uses Spring Boot Starter and Spring Boot AutoConfigurate components to resolve all dependencies and execute the application.

* **Spring Boot Actuator**: Spring Boot Actuator is a sub-project of Spring Boot. It adds several production grade services to your application with little effort on your part. Actuators enable production-ready features to a Spring Boot application, without having to actually implement these things yourself. The Spring Boot Actuator is mainly used to get the internals of running application like health, metrics, info, dump, environment, etc. which is similar to your production environment monitoring setup.

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
#### Q. What is Spring Boot initializr?
The Spring Initializr is ultimately a web application that can generate a Spring Boot project structure for you. It doesn’t generate any application code, but it will give you a basic project structure and either a Maven or a Gradle build specification to build your code with. All you need to do is write the application code.

Spring Initializr can be used several ways, including:

1. A web-based interface.
1. Via Spring ToolSuite.
1. Using the Spring Boot CLI.

#### Q. What is a profile? How do you create application configuration for a specific profile?
Spring Profiles helps to segregating application configurations, and make them available only in certain environments. Any `@Component` or `@Configuration` can be marked with `@Profile` to limit when it is loaded. You can define default configuration in application.properties. Environment specific overrides can be configured in specific files:

* application-dev.properties
* application-qa.properties
* application-stage.properties
* application-prod.properties

**Using Profiles In Code**
```java
@Configuration
@Profile("dev")
public class DevConfigurations {
    // DEV Configurations
}
@Configuration
@Profile("prod")
public class ProdConfigurations {
    // Production Configurations
}
```

#### Q. What is Spring Boot Actuator? How do you monitor web services using Spring Boot Actuator?
Spring Boot Actuator module use to monitor and manage Spring Boot application by providing production-ready features like health check-up, auditing, metrics gathering, HTTP tracing etc. All of these features can be accessed over JMX or HTTP endpoints.

**Adding Spring Boot Actuator**
```xml
<dependencies>
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-actuator</artifactId>
	</dependency>
</dependencies>
```

**Monitoring**: Actuator creates several so-called **endpoints** that can be exposed over HTTP or JMX to let you monitor and interact with application.

For example, There is a `/health` endpoint that provides basic information about the application's health. The `/metrics` endpoint shows several useful metrics information like JVM memory used, system CPU usage, open files, and much more. The `/loggers` endpoint shows application's logs and also lets you change the log level at runtime.
```
http://localhost:8080/actuator

----
{
  "_links": {
    "self": {
      "href": "http://localhost:8080/actuator",
      "templated": false
    },
    "health": {
      "href": "http://localhost:8080/actuator/health",
      "templated": false
    },
    "info": {
      "href": "http://localhost:8080/actuator/info",
      "templated": false
    }
  }
}
```

|Endpoint	             |Description                         |
|------------------------|------------------------------------| 
|health	                 | Application health info            |
|info	                 | Info about the application         |
|env	                 | Properties from environment        |
|metrics	             | Various metrics about the app      |
|mappings	             | @RequestMapping Controller mappings|
|shutdown	             | Triggers application shutdown      |
|httptrace	             | HTTP request/response log          |
|loggers	             | Display and configure logger info  |
|logfile	             | Contents of the log file           |
|threaddump	             | Perform thread dump                |
|heapdump	             | Obtain JVM heap dump               |
|caches	                 | Check available caches             |
|integrationgraph	     | Graph of Spring Integration components|

**Enabling / Disabling endpoints**
```
# Disable an endpoint
management.endpoint.[endpoint-name].enabled=false

# Specific example for 'health' endpoint
management.endpoint.health.enabled=false

# Instead of enabled by default, you can change to mode
# where endpoints need to be explicitly enabled
management.endpoints.enabled-by-default=false
```

#### Q. What is a CommandLineRunner and ApplicationRunner?
`ApplicationRunner` and `CommandLineRunner` interfaces use to execute the code after the Spring Boot application is started. These interfaces can be used to perform any actions immediately after the application has started.

* **CommandLineRunner**
This interface provides access to application arguments as string array. 
```java
@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(CommandLineAppStartupRunner.class);
    @Override
    public void run(String...args) throws Exception {
        logger.info("Application started with command-line arguments: {} . 
        \n To kill this application, press Ctrl + C.", Arrays.toString(args));
    }
}
```
* **ApplicationRunner**
ApplicationRunner wraps the raw application arguments and exposes the ApplicationArguments interface, which has many convinent methods to get arguments, like getOptionNames() to return all the arguments' names, getOptionValues() to return the agrument value, and raw source arguments with method getSourceArgs(). 
```java
@Component
public class AppStartupRunner implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(AppStartupRunner.class);
    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("Your application started with option names : {}", args.getOptionNames());
    }
}
```
#### Q. What is Docker? How to deploy Spring Boot Application to Docker?
A Docker is a tool that makes it very easy to deploy and run an application using **containers**. A container allows a developer to create an all-in-one package of the developed application with all its dependencies. For example, a Java application requires Java libraries, and when we deploy it on any system or VM, we need to install Java first. But, in a container, everything is kept together and shipped as one package, such as in a Docker container.

Step 01: **Create a simple Spring Boot Application**
```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Application {

    @RequestMapping("/")
    public String home() {
        return "Hello Docker World";
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```
To run the application, use the following Maven command from the project root folder:
```
cmd> mvn spring-boot:run
```
Step 02: **Dockerizing using Dockerfile**  

* **Dockerfile** – Specifying a file that contains native Docker commands to build the image
* **Maven** – Using a Maven plugin to build the image  

A Dockerfile is just a regular `.txt` file that includes native Docker commands that are used to specify the layers of an image. The content of the file itself can look something like this:
```
FROM java:8-jdk-alpine

COPY ./target/demo-docker-0.0.1-SNAPSHOT.jar /usr/app/

WORKDIR /usr/app

RUN sh -c 'touch demo-docker-0.0.1-SNAPSHOT.jar'

ENTRYPOINT ["java","-jar","demo-docker-0.0.1-SNAPSHOT.jar"]
```

* **FROM** – The keyword FROM tells Docker to use a given base image as a build base. We have used 'java' with tag '8-jdk-alpine'. Think of a tag as a version. The base image changes from project to project. You can search for images on docker-hub.
* **COPY** - This tells Docker to copy files from the local file-system to a specific folder inside the build image. Here, we copy our .jar file to the build image (Linux image) inside /usr/app.
* **WORKDIR** - The WORKDIR instruction sets the working directory for any RUN, CMD, ENTRYPOINT, COPY and ADD instructions that follow in the Dockerfile. Here we switched the workdir to /usr/app so as we don't have to write the long path again and again.
* **RUN** - This tells Docker to execute a shell command-line within the target system. Here we practically just "touch" our file so that it has its modification time updated (Docker creates all container files in an "unmodified" state by default).
* **ENTRYPOINT** - This allows you to configure a container that will run as an executable. It's where you tell Docker how to run your application. We know we run our spring-boot app as java -jar <app-name>.jar, so we put it in an array.

Step 03: **Create Docker image**  

Generate a Spring Boot `.jar` file using `mvn clean install` command. This file will be used to create the Docker image.
Let's build the image using this Dockerfile. To do so, move to the root directory of the application and run this command:
```
cmd> docker build -t greeting-app 
```
We built the image using `docker build`. We gave it a name with the `-t` flag and specified the current directory where the Dockerfile is. The image is built and stored in our local docker registry.  

Let's check our image:  
```
cmd> docker images
```
And finally, let's run our image:
```
cmd> docker run -p 8090:8080 greeting-app 
```
We can run Docker images using the `docker run` command.  

Each container is an isolated environment in itself and we have to map the port of the host operating system - 8090 and the port inside the container - 8080, which is specified as the -p 8090:8080 argument.
Now, we can access the endpoint on `http://localhost:8080/greet/Pradeep`

#### Q. How to implement Exception Handling in Spring Boot?
Spring Boot provides a number of options for error/exception handling.
1. **@ExceptionHandler Annotation**: This annotation works at the `@Controller` class level. The issue with the approach is only active for the given controller. The annotation is not global, so we need to implement in each and every controller.
```java
@RestController
public class WelcomeController {

    @GetMapping("/greeting")
    String greeting() throws Exception {
      //
    }

    @ExceptionHandler({Exception.class})
    public  handleException(){
       //
    }
}
```
2. **@ControllerAdvice Annotation**: This annotation supports global Exception handler mechanism. So we can implement the controller exception handling events in a central location.
```java
@ControllerAdvice
public class GlobalRestExceptionHandler extends ResponseEntityExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    public void defaultExceptionHandler() {
        // Nothing to do
    }
}
```
3. **@ResponseEntityExceptionHandler**: This method can be used with `@ControllerAdvice` classes. It allows the developer to specify some specific templates of ResponseEntity and return values.

4. **@RestControllerAdvice**: Spring Boot 1.4 introduced the `@RestControllerAdvice` annotation for easier exception handling. It is a convenience annotation that is itself annotated with `@ControllerAdvice` and `@ResponseBody`.
```java
@RestControllerAdvice
public class RestExceptionHandler {

@ExceptionHandler(CustomNotFoundException.class)
public ApiErrorResponse handleNotFoundException(CustomNotFoundException ex) {

ApiErrorResponse response = new ApiErrorResponse.ApiErrorResponseBuilder()
      .withStatus(HttpStatus.NOT_FOUND)
      .withError_code("NOT_FOUND")
      .withMessage(ex.getLocalizedMessage()).build();
      
    return responseMsg;
    }
}
```
#### Q. What is caching? Have you used any caching framework with Spring Boot?
Caching is a mechanism to enhance the performance of a system. It is a temporary memory that lies between the application and the persistent database. Cache memory stores recently used data items in order to reduce the number of database hits as much as possible.

**Types of cache**
* **In-memory caching**: This is the most frequently used area where caching is used extensively to increase performance of the application. In-memory caches such as `Memcached` and `Radis` are key-value stores between your application and your data storage. Since the data is held in RAM, it is much faster than typical databases where data is stored on disk.
* **Database caching**: Database usually includes some level of caching in a default configuration, optimized for a generic use case. Tweaking these settings for specific usage patterns can further boost performance. One popular in this area is first level cache of `Hibernate` or any `ORM frameworks`.
* **Web server caching**: Reverse proxies and caches such as Varnish can serve static and dynamic content directly. Web servers can also cache requests, returning responses without having to contact application servers. In today’s API age, this option is a viable if we want to cache API responses in web server level.
* **CDN caching**: Caches can be located on the client side (OS or browser), server side, or in a distinct cache layer.

**Spring Boot Cache Annotations**
* **@EnableCaching**: It can be added to the boot application class annotated with `@SpringBootApplication`. Spring provides one concurrent hashmap as default cache, but we can override CacheManager to register external cache providers as well easily.
* **@Cacheable**: It is used on the method level to let spring know that the response of the method are cacheable. Spring manages the request/response of this method to the cache specified in annotation attribute. 
```java
@Cacheable(value="books", key="#isbn")
public Book findStoryBook(ISBN isbn, boolean checkWarehouse, boolean includeUsed)
```
* **@CachePut**: It allow us to update the cache and will also allow the method to be executed. It supports the same options as `@Cacheable` and should be used for cache population rather then method flow optimization.
* **@CacheEvict**: It is used when we need to evict (remove) the cache previously loaded of master data. When CacheEvict annotated methods will be executed, it will clear the cache.
* **@Caching**: This annotation is required when we need both `@CachePut` and `@CacheEvict` at the same time.

**Spring Boot Caching Example**  
* **Create HTTP GET REST API**

**Student.java**
```java
public class Student {
 
    String id;
    String name;
    String clz;
 
    public Student(String id, String name, String clz) {
        super();
        this.id = id;
        this.name = name;
        this.clz = clz;
    } 
    //Setters and getters
}
```

**StudentService.java**
```java 
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.example.springcache.domain.Student;
 
@Service
public class StudentService {

    @Cacheable("student")
    public Student getStudentByID(String id) {

        try {
            System.out.println("Going to sleep for 5 Secs.. to simulate backend call.");
            Thread.sleep(1000*5);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Student(id, "Pradeep", "V");
    }
}
```

**StudentController.java**
```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.example.springcache.domain.Student;
import com.example.springcache.service.StudentService;
 
@RestController
public class StudentController {
 
    @Autowired
    StudentService studentService;
 
    @GetMapping("/student/{id}")
    public Student findStudentById(@PathVariable String id) {

        System.out.println("Searching by ID  : " + id);
        return studentService.getStudentByID(id);
    }
}
```
Enable Spring managed Caching

**SpringCacheApplication.java**
```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
 
@SpringBootApplication
@EnableCaching
public class SpringCacheApplication {
 
    public static void main(String[] args) {
        SpringApplication.run(SpringCacheApplication.class, args);
    }
}
```
Output
```
Searching by ID  : 1
Going to sleep for 5 Secs.. to simulate backend call.
 
Searching by ID  : 1
Searching by ID  : 1
Searching by ID  : 1
Searching by ID  : 1
Searching by ID  : 1
 
Searching by ID  : 2
Going to sleep for 5 Secs.. to simulate backend call.
 
Searching by ID  : 2
Searching by ID  : 2
```
#### Q. What is Swagger? Have you implemented it using Spring Boot?
Swagger is widely used for visualizing APIs, and with Swagger UI it provides online sandbox for frontend developers. 
Swagger is a tool, a specification and a complete framework implementation for producing the visual representation of RESTful Web Services. It enables documentation to be updated at the same pace as the server. When properly defined via Swagger, a consumer can understand and interact with the remote service with a minimal amount of implementation logic. 

**Create REST APIs**  
* Open `application.properties` and add below property. This will start the application in /swagger2-demo context path.
```
server.contextPath=/swagger2-demo
```
* Add one REST controller `Swagger2DemoRestController` which will provide basic REST based functionalities on Student entity.

**Swagger2DemoRestController.java**
```java
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.springbootswagger2.model.Student;
 
@RestController
public class Swagger2DemoRestController {
 
    List<Student> students = new ArrayList<Student>();
    {
        students.add(new Student("Sajal", "IV", "India"));
        students.add(new Student("Lokesh", "V", "India"));
        students.add(new Student("Kajal", "III", "USA"));
        students.add(new Student("Sukesh", "VI", "USA"));
    }
 
    @RequestMapping(value = "/getStudents")
    public List<Student> getStudents() {
        return students;
    }
 
    @RequestMapping(value = "/getStudent/{name}")
    public Student getStudent(@PathVariable(value = "name") String name) {
        return students.stream().filter(x -> x.getName().equalsIgnoreCase(name)).collect(Collectors.toList()).get(0);
    }
 
    @RequestMapping(value = "/getStudentByCountry/{country}")
    public List<Student> getStudentByCountry(@PathVariable(value = "country") String country) {
        System.out.println("Searching Student in country : " + country);
        List<Student> studentsByCountry = students.stream().filter(x -> x.getCountry().equalsIgnoreCase(country))
                .collect(Collectors.toList());
        System.out.println(studentsByCountry);
        return studentsByCountry;
    }
 
    @RequestMapping(value = "/getStudentByClass/{cls}")
    public List<Student> getStudentByClass(@PathVariable(value = "cls") String cls) {
        return students.stream().filter(x -> x.getCls().equalsIgnoreCase(cls)).collect(Collectors.toList());
    }
}
```

**Student.java**
```java
public class Student {
     
    private String name;
    private String cls;
    private String country;
 
    public Student(String name, String cls, String country) {
        super();
        this.name = name;
        this.cls = cls;
        this.country = country;
    }
 
    public String getName() {
        return name;
    }
 
    public String getCls() {
        return cls;
    }
 
    public String getCountry() {
        return country;
    }
 
    @Override
    public String toString() {
        return "Student [name=" + name + ", cls=" + cls + ", country=" + country + "]";
    }
}
```
* Start the application as Spring boot application. Test couple of REST Endpoints to check if they are working fine:
```
http://localhost:8080/swagger2-demo/getStudents
http://localhost:8080/swagger2-demo/getStudent/sajal
http://localhost:8080/swagger2-demo/getStudentByCountry/india
http://localhost:8080/swagger2-demo/getStudentByClass/v
```
**Swagger2 Configuration**
* **Add Swagger2 Maven Dependencies**: Open pom.xml file of the spring-boot-swagger2 project and add below two swagger related dependencies i.e. springfox-swagger2 and springfox-swagger-ui.
```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.6.1</version>
</dependency>

<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>2.6.1</version>
</dependency>
```
* **Add Swagger2 Configuration**: Add the below configuration in the code base. To help you understand the configuration, I have added inline comments.
```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import com.google.common.base.Predicates;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
 
@Configuration
@EnableSwagger2
public class Swagger2UiConfiguration extends WebMvcConfigurerAdapter
{
    @Bean
    public Docket api() {
        // @formatter:off
        //Register the controllers to swagger
        //Also it is configuring the Swagger Docket
        return new Docket(DocumentationType.SWAGGER_2).select()
                // .apis(RequestHandlerSelectors.any())
                .apis(Predicates.not(RequestHandlerSelectors.basePackage("org.springframework.boot")))
                // .paths(PathSelectors.any())
                // .paths(PathSelectors.ant("/swagger2-demo"))
                .build();
        // @formatter:on
    }
 
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        //enabling swagger-ui part for visual documentation
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
```
* **Verify Swagger2 UI Docs**
```
 http://localhost:8080/swagger2-demo/swagger-ui.html
```
![alt text](https://github.com/learning-zone/Spring/blob/spring/assets/Swagger2-UI-Docs-without-Annotations.png)

#### Q. How to implement Pagination and Sorting in Spring Boot?
* **JPA Entity**: 

**EmployeeEntity.java**  
```java
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
 
@Entity
@Table(name="TBL_EMPLOYEES")
public class EmployeeEntity {
 
    @Id
    @GeneratedValue
    private Long id;
     
    @Column(name="first_name")
    private String firstName;
     
    @Column(name="last_name")
    private String lastName;
     
    @Column(name="email", nullable=false, length=200)
    private String email;
     
    //Setters and getters
 
    @Override
    public String toString() {
        return "EmployeeEntity [id=" + id + ", firstName=" + firstName +
                ", lastName=" + lastName + ", email=" + email   + "]";
    }
}
```
* **PagingAndSortingRepository**   
PagingAndSortingRepository is an extension of CrudRepository to provide additional methods to retrieve entities using the pagination and sorting abstraction. It provides two methods :

  * **Page findAll(Pageable pageable)** – returns a Page of entities meeting the paging restriction provided in the Pageable object.
  * **Iterable findAll(Sort sort)** – returns all entities sorted by the given options. No paging is applied here.

**EmployeeRepository.java**  
```java
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import com.springbatchexample.demo.entity.EmployeeEntity;
 
@Repository
public interface EmployeeRepository
        extends PagingAndSortingRepository<EmployeeEntity, Long> {
 
}
```
* **Accepting paging and sorting parameters**  
In below spring mvc controller, we are accepting paging and sorting parameters using pageNo, pageSize and sortBy query parameters. Also, by default '10' employees will be fetched from database in page number '0', and employee records will be sorted based on 'id' field.

**EmployeeController.java**  
```java
@RestController
@RequestMapping("/employees")
public class EmployeeController
{
    @Autowired
    EmployeeService service;
 
    @GetMapping
    public ResponseEntity<List<EmployeeEntity>> getAllEmployees(
                        @RequestParam(defaultValue = "0") Integer pageNo,
                        @RequestParam(defaultValue = "10") Integer pageSize,
                        @RequestParam(defaultValue = "id") String sortBy)
    {
        List<EmployeeEntity> list = service.getAllEmployees(pageNo, pageSize, sortBy);
 
        return new ResponseEntity<List<EmployeeEntity>>(list, new HttpHeaders(), HttpStatus.OK);
    }
}
```
To perform pagination and/or sorting, we must create org.springframework.data.domain.Pageable or org.springframework.data.domain.Sort instances are pass to the findAll() method.

**EmployeeService.java**
```java
@Service
public class EmployeeService
{
    @Autowired
    EmployeeRepository repository;
     
    public List<EmployeeEntity> getAllEmployees(Integer pageNo, Integer pageSize, String sortBy)
    {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
 
        Page<EmployeeEntity> pagedResult = repository.findAll(paging);
         
        if(pagedResult.hasContent()) {
            return pagedResult.getContent();
        } else {
            return new ArrayList<EmployeeEntity>();
        }
    }
}
```
* **Pagination and sorting techniques**
  * **Paging WITHOUT sorting**: To apply only pagination in result set, we shall create Pageable object without any Sort information.
```java
Pageable paging = PageRequest.of(pageNo, pageSize);
Page<EmployeeEntity> pagedResult = repository.findAll(paging);
```
   * **Paging WITH sorting**: To apply only pagination in result set, we shall create Pageable object with desired Sort column name.
```java
Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("email"));
Page<EmployeeEntity> pagedResult = repository.findAll(paging);
```
   * **Sorting only**: If there is no need to page, and only sorting is required, we can create Sort object for that.
```java
Sort sortOrder = Sort.by("email");
List<EmployeeEntity> list = repository.findAll(sortOrder);
```
#### Q. How to use schedulers in Spring Boot?
Spring Boot internally uses the `TaskScheduler` interface for scheduling the annotated methods for execution. The @Scheduled annotation is added to a method along with some information about when to execute it, and Spring Boot takes care of the rest.
**Enable Scheduling**
```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SchedulerDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchedulerDemoApplication.class, args);
	}
}
```
**Scheduling a Task with Fixed Rate**
```java
@Scheduled(fixedRate = 2000)
public void scheduleTaskWithFixedRate() {
    logger.info("Fixed Rate Task :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()) );
}
```
Sample Output
```
Fixed Rate Task :: Execution Time - 10:26:58
Fixed Rate Task :: Execution Time - 10:27:00
Fixed Rate Task :: Execution Time - 10:27:02
....
....
```
**Scheduling a Task using Cron Expression**
```java
@Scheduled(cron = "0 * * * * ?")
public void scheduleTaskWithCronExpression() {
    logger.info("Cron Task :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
}
```
Sample Output
```
Cron Task :: Execution Time - 11:03:00
Cron Task :: Execution Time - 11:04:00
Cron Task :: Execution Time - 11:05:00
```

#### Q. How to provide security to spring boot application?
pom.xml
```xml
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```
create a MVC configuration file that extends WebMvcConfigurerAdapter.
```java
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {
   @Override
   public void addViewControllers(ViewControllerRegistry registry) {
      registry.addViewController("/home").setViewName("home");
      registry.addViewController("/").setViewName("home");
      registry.addViewController("/hello").setViewName("hello");
      registry.addViewController("/login").setViewName("login");
   }
}
```
create a Web Security Configuration file, that is used to secure your application to access the HTTP Endpoints by using basic authentication.
```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
   @Override
   protected void configure(HttpSecurity http) throws Exception {
      http
         .authorizeRequests()
            .antMatchers("/", "/home").permitAll()
            .anyRequest().authenticated()
            .and()
         .formLogin()
            .loginPage("/login")
            .permitAll()
            .and()
            .logout()
            .permitAll();
   }
   @Autowired
   public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
      auth
         .inMemoryAuthentication()
         .withUser("user").password("password").roles("USER");
   }
}
```
#### Q. What is CORS in Spring Boot? How to enable CORS in Spring Boot?
Cross-Origin Resource Sharing (CORS) is a security concept that allows restricting the resources implemented in web browsers. It prevents the JavaScript code producing or consuming the requests against different origin.

* **Enable CORS in Controller Method**
```java
@RequestMapping(value = "/products")
@CrossOrigin(origins = "http://localhost:8080")
public ResponseEntity<Object> getProduct() {
   return null;
}
```
* **Global CORS Configuration**
We need to define the shown `@Bean` configuration to set the CORS configuration support globally to your Spring Boot application.
```java
@Bean
public WebMvcConfigurer corsConfigurer() {
   return new WebMvcConfigurerAdapter() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
         registry.addMapping("/products").allowedOrigins("http://localhost:9000");
      }    
   };
}
```
To code to set the CORS configuration globally in main Spring Boot application is given below.
```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
public class DemoApplication {
   public static void main(String[] args) {
      SpringApplication.run(DemoApplication.class, args);
   }
   @Bean
   public WebMvcConfigurer corsConfigurer() {
      return new WebMvcConfigurerAdapter() {
         @Override
         public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/products").allowedOrigins("http://localhost:8080");
         }
      };
   }
}
```
#### Q. What is CSRF attack? How to enable CSRF protection against it?
**CSRF**: CSRF stands for Cross-Site Request Forgery. It is an attack that forces an end user to execute unwanted actions on a web application in which they are currently authenticated. CSRF attacks specifically target state-changing requests, not theft of data, since the attacker has no way to see the response to the forged request.

In order to use the Spring Security CSRF protection, we'll first need to make sure we use the proper HTTP methods for anything that modifies state (PATCH, POST, PUT, and DELETE – not GET).

* **Java Configuration**  
CSRF protection is **enabled by default** in the Java configuration. We can still disable it if we need to:
```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http
      .csrf().disable();
}
```
* **XML Configuration**  
Starting from Spring Security 4.x – the CSRF protection is enabled by default in the XML configuration as well; we can of course still disable it if we need to:
```xml
<http>
    ...
    <csrf disabled="true"/>
</http>
```
* **Extra Form Parameters**  
With CSRF protection enabled on the server side, we'll need to include the CSRF token in our requests on the client side as well:
```html
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
```
* **Using JSON**  
We can't submit the CSRF token as a parameter if we're using JSON; instead we can submit the token within the header.
We'll first need to include the token in our page – and for that we can use meta tags:
```html
<meta name="_csrf" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
```
Then we'll construct the header:
```javascript
var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");
 
$(document).ajaxSend(function(e, xhr, options) {
    xhr.setRequestHeader(header, token);
});
```
#### Q. How do you configure error logging/debugging in Spring Boot application?
In Spring Boot, Logback is the default logging framework, just add spring-boot-starter-web, it will pull in the logback dependencies.  
**pom.xml** 
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
		 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>spring-boot-slf4j</artifactId>
    <packaging>jar</packaging>
    <name>Spring Boot SLF4j</name>
    <version>1.0</version>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.2.RELEASE</version>
    </parent>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <!-- Package as an executable jar/war -->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>2.22.0</version>
            </plugin>
        </plugins>
    </build>
</project>
```
* **application.properties**
```
# logging level
logging.level.org.springframework=ERROR
logging.level.com.mkyong=DEBUG

# output to a file
logging.file=app.log

# temp folder example
#logging.file=${java.io.tmpdir}/app.log

logging.pattern.file=%d %p %c{1.} [%t] %m%n

logging.pattern.console=%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n

## if no active profile, default is 'default'
##spring.profiles.active=prod

# root level
#logging.level.=INFO
```
* **logback.xml**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <property name="HOME_LOG" value="logs/app.log"/>

    <appender name="FILE-ROLLING" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${HOME_LOG}</file>

        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>logs/archived/app.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <!-- each archived file, size max 10MB -->
            <maxFileSize>10MB</maxFileSize>
            <!-- total size of all archive files, if total size > 20GB, 
				it will delete old archived file -->
            <totalSizeCap>20GB</totalSizeCap>
            <!-- 60 days to keep -->
            <maxHistory>60</maxHistory>
        </rollingPolicy>

        <encoder>
            <pattern>%d %p %c{1.} [%t] %m%n</pattern>
        </encoder>
    </appender>

    <logger name="com.mkyong" level="debug" additivity="false">
        <appender-ref ref="FILE-ROLLING"/>
    </logger>

    <root level="error">
        <appender-ref ref="FILE-ROLLING"/>
    </root>

</configuration>
```

**HelloController.java**
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Arrays;
import java.util.List;

@Controller
public class HelloController {

    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);

    @GetMapping("/")
    public String hello(Model model) {

        List<Integer> data = Arrays.asList(1, 2, 3, 4, 5);

        logger.debug("Hello from Logback {}", data);
        model.addAttribute("num", data);

        return "index"; // index.html
    }
}
```
#### Q. What is Spring Batch? How do you implement it using Spring Boot?
Spring Batch is a lightweight, comprehensive batch framework that is designed for use in developing robust batch applications.  
**Why Is Spring Batch Useful**  
* Restartability
* Different readers and writers
* Chunk Processing
* Ease Of Transaction Management
* Ease of parallel processing

**Project Structure**  
In this project, we will create a simple job with 2 step tasks and execute the job to observe the logs. Job execution flow will be –

1. Start job
1. Execute task one
1. Execute task two
1. Finish job

* **Maven Dependencies**

**pom.xml**  
```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd;">
    <modelVersion>4.0.0</modelVersion>
 
    <groupId>com.springbatchexample</groupId>
    <artifactId>App</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>jar</packaging>
 
    <name>App</name>
    <url>http://maven.apache.org</url>
 
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.0.3.RELEASE</version>
    </parent>
 
    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
 
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-batch</artifactId>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
    </dependencies>
 
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
 
    <repositories>
        <repository>
            <id>repository.spring.release</id>
            <name>Spring GA Repository</name>
            <url>http://repo.spring.io/release</url>
        </repository>
    </repositories>
</project>
```

* **Add Tasklets**

**TaskOne.java**
```java
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
 
public class TaskOne implements Tasklet {
 
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception
    {
        System.out.println("TaskOne start..");
        // ... some code
        System.out.println("TaskOne done..");
        return RepeatStatus.FINISHED;
    }   
}
```

**TaskTwo.java**
```java
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
 
public class TaskTwo implements Tasklet {
 
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception 
    {
        System.out.println("TaskTwo start..");
        // ... some code
        System.out.println("TaskTwo done..");
        return RepeatStatus.FINISHED;
    }   
}
```
* **Spring Batch Configuration**  
This is major step where you define all the job related configurations and it’s execution logic. 

**BatchConfig.java** 
```java
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
 
import com.springbatchexample.demo.tasks.TaskOne;
import com.springbatchexample.demo.tasks.TaskTwo;
 
@Configuration
@EnableBatchProcessing
public class BatchConfig {
     
    @Autowired
    private JobBuilderFactory jobs;
 
    @Autowired
    private StepBuilderFactory steps;
     
    @Bean
    public Step stepOne(){
        return steps.get("stepOne")
                .tasklet(new TaskOne())
                .build();
    }
     
    @Bean
    public Step stepTwo() {
        return steps.get("stepTwo")
                .tasklet(new TaskTwo())
                .build();
    }  
     
    @Bean
    public Job demoJob() {
        return jobs.get("demoJob")
                .incrementer(new RunIdIncrementer())
                .start(stepOne())
                .next(stepTwo())
                .build();
    }
}
```
* **Demo**  
Now our simple job 'demoJob' is configured and ready to be executed. I am using CommandLineRunner interface to execute the job automatically, with JobLauncher, when the application is fully started.

**App.java**
```java
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
 
@SpringBootApplication
public class App implements CommandLineRunner {
    @Autowired
    JobLauncher jobLauncher;
     
    @Autowired
    Job job;
     
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
 
    @Override
    public void run(String... args) throws Exception {
        JobParameters params = new JobParametersBuilder()
                    .addString("JobID", String.valueOf(System.currentTimeMillis()))
                    .toJobParameters();
        jobLauncher.run(job, params);
    }
}
```
Console Logs
```
o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=demoJob]] launched with
the following parameters: [{JobID=1530697766768}]
 
o.s.batch.core.job.SimpleStepHandler     : Executing step: [stepOne]
TaskOne start..
TaskOne done..
 
o.s.batch.core.job.SimpleStepHandler     : Executing step: [stepTwo]
TaskTwo start..
TaskTwo done..
 
o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=demoJob]] completed with
the following parameters: [{JobID=1530697766768}] and the following status: [COMPLETED]
```
#### Q. How to implement interceptor with Spring Boot?
Interceptor can be used to perform operations in the following situations −    
* Before sending the request to the controller
* Before sending the response to the client

For example, interceptor can be used to add the request header before sending the request to the controller and add the response header before sending the response to the client.  

Interceptors support three methods −  

* **preHandle()** − This is used to perform operations before sending the request to the controller. This method should return true to return the response to the client.
* **postHandle()** − This is used to perform operations before sending the response to the client.
* **afterCompletion()** − This is used to perform operations after completing the request and response.

```java
@Component
public class ProductServiceInterceptor implements HandlerInterceptor {
   @Override
   public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
      
       log.info("[preHandle][" + request + "]" + "[" + request.getMethod()
      + "]" + request.getRequestURI() + getParameters(request));
      return true;
   }
   @Override
   public void postHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler, 
      ModelAndView modelAndView) throws Exception {
          log.info("[postHandle][" + request + "]");
      }
   
   @Override
   public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
      Object handler, Exception ex) throws Exception {
           if (ex != null) {
              ex.printStackTrace();
           }
           log.info("[afterCompletion][" + request + "][exception: " + ex + "]");
      }
}
```
#### Q. How to use Form Login Authentication using Spring Boot?
**Include spring security 5 dependencies**

**pom.xml**
```xml
<properties>
        <failOnMissingWebXml>false</failOnMissingWebXml>
        <spring.version>5.0.7.RELEASE</spring.version>
</properties>
 
<!-- Spring MVC Dependency -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>${spring.version}</version>
</dependency>
 
<!-- Spring Security Core -->
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-core</artifactId>
    <version>${spring.version}</version>
</dependency>
 
<!-- Spring Security Config -->
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-config</artifactId>
    <version>${spring.version}</version>
</dependency>
 
<!-- Spring Security Web -->
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-web</artifactId>
    <version>${spring.version}</version>
</dependency>
```
* **Configure Authentication and URL Security**

**SecurityConfig.java**
```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
 
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
 
    @Autowired
    PasswordEncoder passwordEncoder;
 
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
        .passwordEncoder(passwordEncoder)
        .withUser("user").password(passwordEncoder.encode("123456")).roles("USER")
        .and()
        .withUser("admin").password(passwordEncoder.encode("123456")).roles("USER", "ADMIN");
    }
 
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
 
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
        .antMatchers("/login")
            .permitAll()
        .antMatchers("/**")
            .hasAnyRole("ADMIN", "USER")
        .and()
            .formLogin()
            .loginPage("/login")
            .defaultSuccessUrl("/home")
            .failureUrl("/login?error=true")
            .permitAll()
        .and()
            .logout()
            .logoutSuccessUrl("/login?logout=true")
            .invalidateHttpSession(true)
            .permitAll()
        .and()
            .csrf()
            .disable();
    }
}
```
* **Bind spring security to web application**

**SpringSecurityInitializer.java**
```java
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
public class SpringSecurityInitializer extends AbstractSecurityWebApplicationInitializer {
    //no code needed
}
```

**AppInitializer.java**
```java
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
 
public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
 
   @Override
   protected Class<?>[] getRootConfigClasses() {
      return new Class[] { HibernateConfig.class, SecurityConfig.class };
   }
 
   @Override
   protected Class<?>[] getServletConfigClasses() {
      return new Class[] { WebMvcConfig.class };
   }
 
   @Override
   protected String[] getServletMappings() {
      return new String[] { "/" };
   }
}
```
* **Login Controller**
```java
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
 
@Controller
public class LoginController
{
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        String errorMessge = null;
        if(error != null) {
            errorMessge = "Username or Password is incorrect !!";
        }
        if(logout != null) {
            errorMessge = "You have been successfully logged out !!";
        }
        model.addAttribute("errorMessge", errorMessge);
        return "login";
    }
  
    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logoutPage (HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){   
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout=true";
    }
}
```

**login.jsp**
```jsp
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<body onload='document.loginForm.username.focus();'>
    <h1>Spring Security 5 - Login Form</h1>
 
    <c:if test="${not empty errorMessge}"><div style="color:red; font-weight: bold; margin: 30px 0px;">${errorMessge}</div></c:if>
 
    <form name='login' action="/login" method='POST'>
        <table>
            <tr>
                <td>UserName:</td>
                <td><input type='text' name='username' value=''></td>
            </tr>
            <tr>
                <td>Password:</td>
                <td><input type='password' name='password' /></td>
            </tr>
            <tr>
                <td colspan='2'><input name="submit" type="submit" value="submit" /></td>
            </tr>
        </table>
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
    </form>
</body>
</html>
```
Output
```
// Run
--------

http://localhost:8080/login
```
#### Q. What is Spring Boot transaction management?
*TODO*
#### Q. What is FreeMarker Template? How do you implement it using Spring Boot?
*TODO*
#### Q. Which all starter maven dependencies have you used?
*TODO*
#### Q. What is GZIP? How to implement it using Spring Boot?
*TODO*
#### Q. When will you use WebSockets? How to implement it using Spring Boot?
*TODO*
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
*TODO*
#### Q. How do you Enable HTTP response compression in Spring Boot?
*TODO*
#### Q. What is the configuration file name, which is used by Spring Boot?
`application.properties`
#### Q. What is difference Between an Embedded Container and a War?
*TODO*
#### Q. What is Mockito?
*TODO*
#### Q. What is @SpringBootTest?
*TODO*
#### Q. What is TestRestTemplate?
*TODO*
#### Q. What is JavaConfig?
*TODO*
#### Q. Have you written Test cases using Spring Boot?
*TODO*