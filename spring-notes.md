## Spring Boot Interview Questions and Answers

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
```java
// Student.java
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
```java
// StudentService.java
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
```java
// StudentController.java
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
```java
// SpringCacheApplication.java
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
Swagger2DemoRestController.java
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
* Student.java
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
* **JPA Entity**
Each entity instance represent an employee record in database.
EmployeeEntity.java  
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
EmployeeRepository.java  
```java
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import com.howtodoinjava.demo.entity.EmployeeEntity;
 
@Repository
public interface EmployeeRepository
        extends PagingAndSortingRepository<EmployeeEntity, Long> {
 
}
```
* **Accepting paging and sorting parameters**  
In below spring mvc controller, we are accepting paging and sorting parameters using pageNo, pageSize and sortBy query parameters. Also, by default '10' employees will be fetched from database in page number '0', and employee records will be sorted based on 'id' field.

EmployeeController.java  
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

EmployeeService.java
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
  * **Paging WITHOUT sorting**
To apply only pagination in result set, we shall create Pageable object without any Sort information.
```java
Pageable paging = PageRequest.of(pageNo, pageSize);
Page<EmployeeEntity> pagedResult = repository.findAll(paging);
```
  * **Paging WITH sorting**
To apply only pagination in result set, we shall create Pageable object with desired Sort column name.
```java
Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("email"));
Page<EmployeeEntity> pagedResult = repository.findAll(paging);
```
  * **Sorting only**
If there is no need to page, and only sorting is required, we can create Sort object for that.
```java
Sort sortOrder = Sort.by("email");
List<EmployeeEntity> list = repository.findAll(sortOrder);
```
#### Q. How to use schedulers with Spring Boot?
#### Q. How to provide security to spring boot application?
#### Q. Have you integrated Spring Boot and ActiveMQ?
#### Q. What is Spring Batch? How do you implement it using Spring Boot?
#### Q. What is FreeMarker Template? How do you implement it using Spring Boot?
#### Q. How to implement interceptors with Spring Boot?
#### Q. Which all starter maven dependencies have you used?
#### Q. What is CSRF attack? How to enable CSRF protection against it?
#### Q. How do you configure error logging/debugging in Spring Boot application?
#### Q. How to use Form Login Authentication using Spring Boot?
#### Q. What is OAuth2? How to implement it using Spring Boot?
#### Q. What is GZIP? How to implement it using Spring Boot?
#### Q. When will you use WebSockets? How tto implement it using Spring Boot?
#### Q. What is AOP? How to use it with Spring Boot?
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
#### Q. How do you Enable HTTP response compression in Spring Boot?
#### Q. What is the configuration file name, which is used by Spring Boot?
`application.properties`
#### Q. What is difference Between an Embedded Container and a War?
#### Q. What is Mockito?
#### Q. What is @SpringBootTest?
#### Q. What is TestRestTemplate?
#### Q. What is JavaConfig?
#### Q. Have you written Test cases using Spring Boot?
