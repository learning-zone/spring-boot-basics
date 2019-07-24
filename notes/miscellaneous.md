# Java, J2EE, Spring-Boot Questions


#### Q. Spring Boot program for file upload / download

* **Step 01: Configuring Server and File Storage Properties**

```
#src/main/resources/application.properties

## MULTIPART (MultipartProperties)
# Enable multipart uploads
spring.servlet.multipart.enabled=true
# Threshold after which files are written to disk.
spring.servlet.multipart.file-size-threshold=2KB
# Max file size.
spring.servlet.multipart.max-file-size=200MB
# Max Request Size
spring.servlet.multipart.max-request-size=215MB

## File Storage Properties
# All files uploaded through the REST API will be stored in this directory
file.upload-dir=/Users/files/uploads

```

* **Step 02: Automatically binding properties to a POJO class**

```
package com.example.filedemo.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {
    private String uploadDir;

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }
}

```

* **Step 03: Enable Configuration Properties**

```
#src/main/java/com/example/filedemo/FileDemoApplication.java

package com.example.filedemo;

import com.example.filedemo.property.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        FileStorageProperties.class
})
public class FileDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileDemoApplication.class, args);
    }
}

```

* **Step 04: Writing APIs for File Upload and Download**

```
package com.example.filedemo.controller;

import com.example.filedemo.payload.UploadFileResponse;
import com.example.filedemo.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService fileStorageService;
    
    @PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();

        return new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
    }

    @PostMapping("/uploadMultipleFiles")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        return Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file))
                .collect(Collectors.toList());
    }

    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}

```

* **Step 05: UploadFileResponse**

```
package com.example.filedemo.payload;

public class UploadFileResponse {
    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private long size;

    public UploadFileResponse(String fileName, String fileDownloadUri, String fileType, long size) {
        this.fileName = fileName;
        this.fileDownloadUri = fileDownloadUri;
        this.fileType = fileType;
        this.size = size;
    }

	// Getters and Setters (Omitted for brevity)
}

```

* **Step 06: Service for Storing Files in the FileSystem and retrieving them**

```
package com.example.filedemo.service;

import com.example.filedemo.exception.FileStorageException;
import com.example.filedemo.exception.MyFileNotFoundException;
import com.example.filedemo.property.FileStorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + fileName, ex);
        }
    }
}

```

* **Step 07: FileStorageException**

```
package com.example.filedemo.exception;

public class FileStorageException extends RuntimeException {
    public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}

```

* **Step 08: MyFileNotFoundException**

```
package com.example.filedemo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MyFileNotFoundException extends RuntimeException {
    public MyFileNotFoundException(String message) {
        super(message);
    }

    public MyFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

```

* **Step 09: Running the Application and Testing the APIs via Postman**

```
mvn spring-boot:run

```

#### Q. Spring Boot program for Send Mail

* **Step 01: pom.xml Settings**

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
	http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>spring-boot-send-email</artifactId>
    <packaging>jar</packaging>
    <name>Spring Boot Send Email</name>
    <url>https://www.springboot.com</url>
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
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

        <!-- send email -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-mail</artifactId>
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

* **Step 02: application.properties Settings**

```
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=pradeep.vwa@gmail.com
spring.mail.password=*****

# Other properties
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.connectiontimeout=5000
spring.mail.properties.mail.smtp.timeout=5000
spring.mail.properties.mail.smtp.writetimeout=5000

# TLS , port 587
spring.mail.properties.mail.smtp.starttls.enable=true

# SSL, post 465
#spring.mail.properties.mail.smtp.socketFactory.port = 465
#spring.mail.properties.mail.smtp.socketFactory.class = javax.net.ssl.SSLSocketFactory

```

* **Step 03: Application.java**

```
package com.springtutorial;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private JavaMailSender javaMailSender;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {

        System.out.println("Sending Email...");

        try {
		
            sendEmail();
            //sendEmailWithAttachment();

        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Done");

    }

    void sendEmail() {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("1@gmail.com", "2@yahoo.com");

        msg.setSubject("Testing from Spring Boot");
        msg.setText("Hello World \n Spring Boot Email");

        javaMailSender.send(msg);

    }

    void sendEmailWithAttachment() throws MessagingException, IOException {

        MimeMessage msg = javaMailSender.createMimeMessage();

        // true = multipart message
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        helper.setTo("1@gmail.com");

        helper.setSubject("Testing from Spring Boot");

        // default = text/plain
        //helper.setText("Check attachment for image!");

        // true = text/html
        helper.setText("<h1>Check attachment for image!</h1>", true);

        helper.addAttachment("my_photo.png", new ClassPathResource("android.png"));

        javaMailSender.send(msg);

    }
}

```


#### Q. Spring Boot RESTful web services example

* **Step 01: pom.xml Settings**

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                        http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
 
    <groupId>com.springexample</groupId>
    <artifactId>SpringBootCrudRestful</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>jar</packaging>
    <name>SpringBootCrudRestful</name>
    <description>Spring Boot + Restful</description>
 
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.0.0.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
 
    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
    </properties>
 
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
         
        <dependency>
            <groupId>com.fasterxml.jackson.dataformat</groupId>
            <artifactId>jackson-dataformat-xml</artifactId>
        </dependency>
 
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
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
 
</project>

```

* **Step 02: SpringBootCrudRestfulApplication.java**

```
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
 
@SpringBootApplication
public class SpringBootCrudRestfulApplication {
 
    public static void main(String[] args) {
        SpringApplication.run(SpringBootCrudRestfulApplication.class, args);
    }
}

```

* **Step 03: Employee.java**

```
public class Employee {
 
    private String empNo;
    private String empName;
    private String position;
 
    public Employee() { }
 
    public Employee(String empNo, String empName, String position) {
        this.empNo = empNo;
        this.empName = empName;
        this.position = position;
    }
 
    public String getEmpNo() {
        return empNo;
    }
 
    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }
 
    public String getEmpName() {
        return empName;
    }
 
    public void setEmpName(String empName) {
        this.empName = empName;
    }
 
    public String getPosition() {
        return position;
    }
 
    public void setPosition(String position) {
        this.position = position;
    }
 
}

```
* **Step 04: EmployeeDAO.java**

```
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
import org.springexample.sbcrudrestful.model.Employee;
import org.springframework.stereotype.Repository;
 
@Repository
public class EmployeeDAO {
 
    private static final Map<String, Employee> empMap = new HashMap<String, Employee>();
 
    static {
        initEmps();
    }
 
    private static void initEmps() {
        Employee emp1 = new Employee("E01", "Smith", "Clerk");
        Employee emp2 = new Employee("E02", "Allen", "Salesman");
        Employee emp3 = new Employee("E03", "Jones", "Manager");
 
        empMap.put(emp1.getEmpNo(), emp1);
        empMap.put(emp2.getEmpNo(), emp2);
        empMap.put(emp3.getEmpNo(), emp3);
    }
 
    public Employee getEmployee(String empNo) {
        return empMap.get(empNo);
    }
 
    public Employee addEmployee(Employee emp) {
        empMap.put(emp.getEmpNo(), emp);
        return emp;
    }
 
    public Employee updateEmployee(Employee emp) {
        empMap.put(emp.getEmpNo(), emp);
        return emp;
    }
 
    public void deleteEmployee(String empNo) {
        empMap.remove(empNo);
    }
 
    public List<Employee> getAllEmployees() {
        Collection<Employee> c = empMap.values();
        List<Employee> list = new ArrayList<Employee>();
        list.addAll(c);
        return list;
    }
 
}


```
* **Step 05: MainRESTController.java**

```
import java.util.List;
 
import org.springexample.sbcrudrestful.dao.EmployeeDAO;
import org.springexample.sbcrudrestful.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
 
@RestController
 
public class MainRESTController {
 
    @Autowired
    private EmployeeDAO employeeDAO;
 
    @RequestMapping("/")
    @ResponseBody
    public String welcome() {
        return "Welcome to RestTemplate Example.";
    }
 
    // URL:
    // http://localhost:8080/SomeContextPath/employees
    // http://localhost:8080/SomeContextPath/employees.xml
    // http://localhost:8080/SomeContextPath/employees.json
    @RequestMapping(value = "/employees", //
            method = RequestMethod.GET, //
            produces = { MediaType.APPLICATION_JSON_VALUE, //
                    MediaType.APPLICATION_XML_VALUE })
    @ResponseBody
    public List<Employee> getEmployees() {
        List<Employee> list = employeeDAO.getAllEmployees();
        return list;
    }
 
    // URL:
    // http://localhost:8080/SomeContextPath/employee/{empNo}
    // http://localhost:8080/SomeContextPath/employee/{empNo}.xml
    // http://localhost:8080/SomeContextPath/employee/{empNo}.json
    @RequestMapping(value = "/employee/{empNo}", //
            method = RequestMethod.GET, //
            produces = { MediaType.APPLICATION_JSON_VALUE, //
                    MediaType.APPLICATION_XML_VALUE })
    @ResponseBody
    public Employee getEmployee(@PathVariable("empNo") String empNo) {
        return employeeDAO.getEmployee(empNo);
    }
 
    // URL:
    // http://localhost:8080/SomeContextPath/employee
    // http://localhost:8080/SomeContextPath/employee.xml
    // http://localhost:8080/SomeContextPath/employee.json
 
    @RequestMapping(value = "/employee", //
            method = RequestMethod.POST, //
            produces = { MediaType.APPLICATION_JSON_VALUE, //
                    MediaType.APPLICATION_XML_VALUE })
    @ResponseBody
    public Employee addEmployee(@RequestBody Employee emp) {
 
        System.out.println("(Service Side) Creating employee: " + emp.getEmpNo());
 
        return employeeDAO.addEmployee(emp);
    }
 
    // URL:
    // http://localhost:8080/SomeContextPath/employee
    // http://localhost:8080/SomeContextPath/employee.xml
    // http://localhost:8080/SomeContextPath/employee.json
    @RequestMapping(value = "/employee", //
            method = RequestMethod.PUT, //
            produces = { MediaType.APPLICATION_JSON_VALUE, //
                    MediaType.APPLICATION_XML_VALUE })
    @ResponseBody
    public Employee updateEmployee(@RequestBody Employee emp) {
 
        System.out.println("(Service Side) Editing employee: " + emp.getEmpNo());
 
        return employeeDAO.updateEmployee(emp);
    }
 
    // URL:
    // http://localhost:8080/SomeContextPath/employee/{empNo}
    @RequestMapping(value = "/employee/{empNo}", //
            method = RequestMethod.DELETE, //
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    @ResponseBody
    public void deleteEmployee(@PathVariable("empNo") String empNo) {
 
        System.out.println("(Service Side) Deleting employee: " + empNo);
 
        employeeDAO.deleteEmployee(empNo);
    }
 
}
```

* **Step 06: Run and Test the application**

```
// Get all the employees details
http://localhost:8080/employees
http://localhost:8080/employees.json
http://localhost:8080/employees.xml


// Get the employee based in employee-id
http://localhost:8080/employee/E01
http://localhost:8080/employee/E01.xml
http://localhost:8080/employee/E01.json

```

#### Q. Connecting Spring Boot with databases

* **Step 01: application.properties Settings**

```
spring.datasource.url=jdbc:mysql://localhost:3306/springbootdb  
spring.datasource.username=root  
spring.datasource.password=mysql  
spring.jpa.hibernate.ddl-auto=create-drop  

```

* **Step 02: SpringBootJdbcApplication.java**

```
package com.learningzone;  

import org.springframework.boot.SpringApplication;  
import org.springframework.boot.autoconfigure.SpringBootApplication;  
@SpringBootApplication  
public class SpringBootJdbcApplication {  
    public static void main(String[] args) {  
        SpringApplication.run(SpringBootJdbcApplication.class, args);  
    }  
}  

```

* **Step 02: SpringBootJdbcController.java**

```
package com.learningzone;
  
import org.springframework.web.bind.annotation.RequestMapping;  
import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.jdbc.core.JdbcTemplate;  
import org.springframework.web.bind.annotation.RestController;  
@RestController  
public class SpringBootJdbcController {  
    @Autowired  
    JdbcTemplate jdbc;    
    @RequestMapping("/insert")  
    public String index(){  
        jdbc.execute("insert into user(name,email)values('javatpoint','java@javatpoint.com')");  
        return"data inserted Successfully";  
    }  
}  

```

#### Q. How bootstrap class loader works in java?

Bootstrap **ClassLoader** is repsonsible for loading standard JDK classs files from **rt.jar** and it is parent of all class loaders in java.
There are three types of built-in ClassLoader in Java:

1. **Bootstrap Class Loader** – It loads JDK internal classes, typically loads rt.jar and other core classes for example java.lang.* package classes
1. **Extensions Class Loader** – It loads classes from the JDK extensions directory, usually $JAVA_HOME/lib/ext directory.
1. **System Class Loader** – It loads classes from the current classpath that can be set while invoking a program using -cp or -classpath command line options.

```
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Java program to demonstrate How ClassLoader works in Java
 * 
 */

public class ClassLoaderTest {
  
    public static void main(String args[]) {
        try {          
            //printing ClassLoader of this class
            System.out.println("ClassLoaderTest.getClass().getClassLoader() : "
                                 + ClassLoaderTest.class.getClassLoader());

          
            //trying to explicitly load this class again using Extension class loader
            Class.forName("test.ClassLoaderTest", true 
                            ,  ClassLoaderTest.class.getClassLoader().getParent());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClassLoaderTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

```

#### Q. Why string is immutable in java? 

The string is Immutable in Java because String objects are cached in String pool. Since cached String literals are shared between multiple clients there is always a risk, where one client's action would affect all another client. 

Since string is immutable it can safely share between many threads and avoid any synchronization issues in java.

#### Q. What is Java String Pool?  

String Pool in java is a pool of Strings stored in Java Heap Memory. String pool helps in saving a lot of space for Java Runtime although it takes more time to create the String.

When we use double quotes to create a String, it first looks for String with the same value in the String pool, if found it just returns the reference else it creates a new String in the pool and then returns the reference. However using new operator, we force String class to create a new String object in heap space.

```
public class StringPool {

    /**
     * Java String Pool example
     * @param args
     */
    public static void main(String[] args) {
        String s1 = "Hello";
        String s2 = "Hello";
        String s3 = new String("Hello");
        
        System.out.println("s1 == s2 :" +(s1==s2)); // true
        System.out.println("s1 == s3 :" +(s1==s3)); // false
    }
}

```


#### Q. Explain how Garbage collector algorithm works? 

Garbage collection works on **Mark** and **Sweep** algorithm. In Mark phase it detects all the unreachable objects and Sweep phase it reclaim the heap space used by the garbage objects and make the space available again to the program.

There are methods like <code>System.gc()</code> and <code>Runtime.gc()</code> which is used to send request of Garbage collection to JVM but it’s not guaranteed that garbage collection will happen. If there is no memory space for creating a new object in Heap Java Virtual Machine throws <code>OutOfMemoryError</code> or <code>java.lang.OutOfMemoryError</code> heap space


#### Q. Explain JoinPoint and Pointcut in spring 
#### Q. Why do we need to Wrapper classes?
#### Q. How website caching works? 
#### Q. How to create marker interface?
#### Q. Explain how hashMap works?
#### Q. Write a code to implement hashMap, arrayList 
#### Q. Difference between arrayList and linkedList 
#### Q. What are jsp implicit objects? 
#### Q. Difference between sendRedirect() and forward() 
#### Q. What happens when a servlet is invoked, when is init invoked and when is sevice invoked?
#### Q. Explain servlet and jsp lifecycle 
#### Q. What are the major additions for jdk from 1.7 to 1.8?
#### Q. How serialization works in java? 
#### Q. What is better way to manage transactions.
#### Q. What are the various ways to load a class?
#### Q. Java Program to Implement Singly Linked List
#### Q. Design patterns related question(Singleton, Adaptor, Factory, Strategy) 
#### Q. Spring bean scope. 
#### Q. What is AOP? what does spring AOP provide?
#### Q. What is servlet context, parameter, argument?
#### Q. How set implement unique values?
#### Q. What is Comparable and Comparator interface in java?
#### Q. DAO factory pattern. 
#### Q. How to stop thread? 
#### Q. Insert a uppercase value into map without using toUpperCase() of string class. 
#### Q. While overriding a method can you throw another exception or broader exception? 
#### Q. Difference between entrySet(), keySet() and values() in HashMap. 
#### Q. A java program in windows, how will you run it on linux machine?
#### Q. What is checked, unchecked exception and errors? 
#### Q. What are the different ways to load the Spring container? 
#### Q. How to implement webservices with spring using custom annotation?
#### Q. How spring interceptors work?
#### Q. Difference between @Service and @Component tags 
#### Q. Implement your own String class 
#### Q. How do you identify deadlock has happened in prod environment?
#### Q. How do you find third highest salary? 
#### Q. Difference between procedures and triggers 
#### Q. What is the difference between Array and arrayList datastructure? 
#### Q. How microservices communicate with each other? 
#### Q. Array or ArrayList which one is faster?  [ Array is faster ]
#### Q. How to prevent from database attacks/SQL Injection? 
#### Q. How to do SSO implementation using Spring Boot?
#### Q. Various ways that you implement JAAS.
#### Q. Assume you have a singly linked list, now you need to find reverse of it without using new operator. 
#### Q. Name design patterns used in collection framework.
#### Q. Object oriented feature like: Association, Composition, Aggregation
#### Q. Difference between DI and IOC in spring.
#### Q. Optimistic vs pessimistic locking in table level and row level.
#### Q. Main advantage of restful implementation over SOAP
#### Q. How do you provide security features in REST and SOAP requests?
#### Q. How do you implement secondary level cache? Clustering?
#### Q. What is SQL injection attack?
#### Q. Difference between symmetric, public and private keys, what are the procedure to implement the same using spring security.
#### Q. What code coverage tools are you using for your project? (we use cobetura, you can use it with ant, maven, gradle)
#### Q. Implement thread-safe code without using the `synchronized` keyword? 
#### Q. Design parking lot with 100 parking space.
#### Q. Scenario of browser’s browsing history, where you need to store the browsing history, what data structure will you use.? [ Ans: use stack ]
#### Q. Scenario where in we have to download a big file by clicking on a link, how will you make sure that connections is reliable throughout. [ Ans: use persistent MQueues ]
#### Q. If you store Employee object as key say: Employee emp = new Employee(“name1”,20); store it in a HashMap as key, now if we add a new parameter emp.setMarriedStatus(true) and try to override it what will happen? [ Ans: new instance of Employee will be inserted to HashMap ]
#### Q. Given a string "abc" or any other string print all possible combinations of it.
#### Q. Spring MVC flow (what design patterns do it use).
#### Q. Given a requirement where in we have a html file and textbox:
        -> user can enter number( find sum of digits)
        -> user can enter string (print the string)
        -> user can enter JSON object (print the attributes)
        -> user can enter function name: add(2,3)  (invoke the respective function.)
              Based on user input perform the indicated task in parenthesis.
#### Q. Given table Employee and Department write a sql statement to find count of employees department
#### Q. Design patterns used in Javascript.
#### Q. Difference between classnotfound and noclassdeffound
#### Q. What do we mean by weak reference?
#### Q. Difference between hashset and linkedhashset
#### Q. What do you mean Run time Polymorphism?
#### Q. How will you create thread?
#### Q. Why implementing Runnable is better than extending thread?
#### Q. What is the use of Synchronized keyword?
#### Q. What is the difference between HashTable and HashMap?
#### Q. What will happen if I insert duplicate key-pair value inserted in to HashTable?
#### Q. Differentiate ArrayList from Vector?
#### Q. How do you sort out items in ArrayList in forward and reverse directions?
#### Q. Tell me about join() and wait() methods?
#### Q. If Parent Class have a method add() throws Exception, and child class overrides same method without Exception, will that compile and run? Is it overridden or overloaded? 
#### Q. What are two uses of the equals() method? 
#### Q. If I don't have Explicit constructor in parent class and having in child class, while calling the child's constructor jvm automatically calls Implicit Constructor of parent class? Yes OR No ?
#### Q. What are the different types of JDBC Driver?
#### Q. How Encapsulation concept implemented in JAVA?
#### Q. Create a ordered collection, which allows duplicates? Which collection is used for this?
#### Q. Create a class, which have the behavior of Hash-map? 
#### Q. What hashCode() and equals() does in HashMap?
#### Q. Do you know Generics? What is it? Why we need that? How did you used in your coding?
#### Q. Difference between String and StringBuilder and StringBuffer ?
#### Q. How can we create a object of a class without using new operator?
#### Q. what are upcasting and downcasting?
