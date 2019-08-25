## Java, J2EE, Spring-Boot Questions

#### Q. Spring Boot program for file upload / download

* **Step 01: Configuring Server and File Storage Properties**

```java
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

```java
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

```java
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

```java
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

```java
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

```java
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

```java
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

```java
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

```java
mvn spring-boot:run
```

#### Q. Spring Boot program for Send Mail

* **Step 01: pom.xml Settings**

```xml
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

```java
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

```java
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

```xml
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

```java
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

```java
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

```java
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

```java
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

```java
spring.datasource.url=jdbc:mysql://localhost:3306/springbootdb  
spring.datasource.username=root  
spring.datasource.password=mysql  
spring.jpa.hibernate.ddl-auto=create-drop  
```

* **Step 02: SpringBootJdbcApplication.java**

```java
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

```java
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
#### Q. What is difference between spring and spring boot?
*TODO*

#### Q. How bootstrap class loader works in java?

Bootstrap **ClassLoader** is repsonsible for loading standard JDK classs files from **rt.jar** and it is parent of all class loaders in java.
There are three types of built-in ClassLoader in Java:

1. **Bootstrap Class Loader** – It loads JDK internal classes, typically loads rt.jar and other core classes for example java.lang.* package classes
1. **Extensions Class Loader** – It loads classes from the JDK extensions directory, usually $JAVA_HOME/lib/ext directory.
1. **System Class Loader** – It loads classes from the current classpath that can be set while invoking a program using -cp or -classpath command line options.

```java
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Java program to demonstrate How ClassLoader works in Java
 * 
 **/

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

```java
/**
* Java program to illustrate String Pool
*
**/
public class StringPool {

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


#### Q. How to create marker interface?

An interface with no methods is known as marker or tagged interface. It provides some useful information to JVM/compiler so that JVM/compiler performs some special operations on it. It is used for better readability of code.  Example: Serializable, Clonnable etc. 

Syntax:
```java
public interface Interface_Name {

}
```
Example:
```java
/**
* Java program to illustrate Maker Interface 
*
**/
interface Marker {    }

class A implements Marker {
      //do some task
}

class Main {
         public static void main(String[] args) {
            A obj = new A();
            if (obj instanceOf Marker){
                // do some task
            }
       }
}
```

#### Q. Explain how hashMap works in Java?

HashMap in Java works on **hashing** principle. It is a data structure which allows us to store object and retrieve it in constant time O(1). In hashing, hash functions are used to link key and value in HashMap. Objects are stored by calling put(key, value) method of HashMap and retrieved by calling **get(key)** method. When we call put method, **hashcode()** method of the key object is called so that hash function of the map can find a bucket location to store value object, which is actually an index of the internal array, known as the table. HashMap internally stores mapping in the form of **Map.Entry** object which contains both key and value object.

Since the internal array of HashMap is of fixed size, and if you keep storing objects, at some point of time hash function will return same bucket location for two different keys, this is called collision in HashMap. In this case, a linked list is formed at that bucket location and a new entry is stored as next node.

Example:
```java
/**
* Java program to illustrate internal working of HashMap 
*
**/
import java.util.HashMap; 
  
class Key { 
    String key; 
    Key(String key) { 
        this.key = key; 
    } 
  
    @Override
    public int hashCode() { 
        int hash = (int)key.charAt(0); 
        System.out.println("hashCode for key: "
                           + key + " = " + hash); 
        return hash; 
    } 
  
    @Override
    public boolean equals(Object obj) { 
        return key.equals(((Key)obj).key); 
    } 
} 
 
public class HashMapExample { 
    public static void main(String[] args) { 
        HashMap map = new HashMap(); 
        map.put(new Key("Hello"), 20); 
        map.put(new Key("World"), 30); 
        map.put(new Key("Java"), 40); 
  
        System.out.println(); 
        System.out.println("Value for key World: " + map.get(new Key("World"))); //hashCode for key: World = 118
        System.out.println("Value for key Java: " + map.get(new Key("Java")));   //hashCode for key: Java = 115
    } 
} 
```

#### Q. Write a code to convert HashMap to ArrayList.  

```java
import java.util.ArrayList; 
import java.util.Collection; 
import java.util.HashMap; 
import java.util.Map.Entry; 
import java.util.Set; 
public class MapToListExamples {

    public static void main(String[] args) {

        // Creating a HashMap object 
        HashMap<String, String> performanceMap = new HashMap<String, String>(); 
         
        // Adding elements to HashMap 
        performanceMap.put("John Kevin", "Average");  
        performanceMap.put("Ladarious Fernandez", "Very Good"); 
        performanceMap.put("Ivan Jose", "Very Bad"); 
        performanceMap.put("Smith Jacob", "Very Good"); 
        performanceMap.put("Athena Stiltner", "Bad"); 
         
        // Getting Set of keys 
        Set<String> keySet = performanceMap.keySet(); 
         
        // Creating an ArrayList of keys 
        ArrayList<String> listOfKeys = new ArrayList<String>(keySet); 
         
        System.out.println("ArrayList Of Keys :"); 
         
        for (String key : listOfKeys) {
            System.out.println(key); 
        }
                  
        // Getting Collection of values 
        Collection<String> values = performanceMap.values(); 
         
        // Creating an ArrayList of values 
        ArrayList<String> listOfValues = new ArrayList<String>(values); 
         
        System.out.println("ArrayList Of Values :"); 
         
        for (String value : listOfValues) { 
            System.out.println(value); 
        } 
                  
        // Getting the Set of entries 
        Set<Entry<String, String>> entrySet = performanceMap.entrySet(); 
         
        // Creating an ArrayList Of Entry objects 
        ArrayList<Entry<String, String>> listOfEntry = new ArrayList<Entry<String,String>>(entrySet); 
         
        System.out.println("ArrayList of Key-Values :"); 
         
        for (Entry<String, String> entry : listOfEntry) { 
            System.out.println(entry.getKey()+" : "+entry.getValue()); 
        } 
    } 
}
```
#### Q. Difference between arrayList and linkedList.

ArrayList and LinkedList both implements List interface and maintains insertion order. Both are non synchronized classes.

|ArrayList	                                                          |LinkedList                                         |
|--------------------------------------------------------------------|---------------------------------------------------
| ArrayList internally uses a dynamic array to store the elements.  |	LinkedList internally uses a doubly linked list to                                                                           store the elements. |
| Manipulation with ArrayList is slow because it internally uses an array. If any element is removed from the array, all the bits are shifted in memory.|Manipulation with LinkedList is faster than ArrayList because it uses a doubly linked list, so no bit shifting is required in memory.|
| An ArrayList class can act as a list only because it implements List only.|	LinkedList class can act as a list and queue |both because it implements List and Deque interfaces.|
| ArrayList is better for storing and accessing data. 	          |LinkedList is better for manipulating data.|


#### Q. What is JSP Implicit Object? 

* **request**: This is the HttpServletRequest object associated with the request.

Example: index.jsp

```jsp
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
  <head>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
  <title>Implicit Objects</title>
</head>
<body>
  <form action="request.jsp">
    <input type="text" name="username">
    <input type="submit" value="submit">
  </form>
</body>
</html>
```
request.jsp
```jsp
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
  <head>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
  <title>Implicit Objects</title>
</head>
<body>
  <%  String username = request.getParameter("username");
      out.println("Welcome "+ username);
   %>
</body>
</html>
```

* **response**: This is the HttpServletResponse object associated with the response to the client.

Example:
```jsp
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Implicit Objects</title>
  </head>
<body>
    <%response.setContentType("text/html"); %>
</body>
</html>
```

* **session**: This is the HttpSession object associated with the request.

Example: index.jsp
```jsp
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>Implicit Objects</title>
    </head>
<body>
    <% session.setAttribute("user","Pradeep"); %>
    <a href="session.jsp">Click here to get user name</a>
</body>
</html>
```
session.jsp
```jsp
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>Implicit Objects</title>
    </head>
<body>
    <% String name = (String)session.getAttribute("user");
        out.println("User Name is " +name);
    %>
</body>
</html>
```

* **out**: This is the PrintWriter object used to send output to the client.

Example:
```jsp
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>Implicit Objects</title>
    </head>
<body>
    <% int num1=10;int num2=20;
        out.println("num1 is " +num1);
        out.println("num2 is "+num2);
    %>
</body>
</html>
```

* **application**: This is the ServletContext object associated with the application context.

Example:
```jsp
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>Implicit Objects</title>
    </head>
<body>
    <% application.getContextPath(); %>
</body>
</html>
```
* **config**: This is the ServletConfig object associated with the page.

Example: web.xml
```xml
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
         http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1">
    <servlet>
        <servlet-name>comingsoon</servlet-name>
        <servlet-class>mysite.server.ComingSoonServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>comingsoon</servlet-name>
        <url-pattern>/*</url-pattern>
    </servlet-mapping>
</web-app>
```
index.jsp
```jsp
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>Implicit Objects</title>
    </head>
<body>
    <% String servletName = config.getServletName();
        out.println("Servlet Name is " +servletName);%>
</body>
</html>
```

* **pageContext**: It is used to get, set and remove the attributes from a particular scope.

* **page**: Page implicit variable holds the currently executed servlet object for the corresponding jsp. Acts as this object for current jsp page.

Example:
```jsp
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>Implicit Objects</title>
    </head>
<body>
    <% String pageName = page.toString();
        out.println("Page Name is " +pageName);
    %>
</body>
</html>
```

* **Exception**: It is used for exception handling in JSP.

Example:
```jsp
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" isErrorPage="true"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>Implicit Objects</title>
    </head>
<body>
    <%  int[] num1={1,2,3,4};
        out.println(num1[5]);
    %>
    <%= exception %>
</body>
</html>
```


#### Q. What is difference between sendRedirect() and forward() in Servlet?

* **SendRedirect()**:  
This method is declared in **HttpServletResponse** Interface. It is used to redirect client request to some other location for further processing, the new location is available on different server or different context.our web container handle this and transfer the request using  browser, and this request is visible in browser as a new request. 

Signature: 
```java
void sendRedirect(String url)
```

* **Forward()**:
This method is declared in **RequestDispatcher** Interface. It is used to pass the request to another resource for further processing within the same server, another resource could be any servlet, jsp page any kind of file.

Signature:
```java
forward(ServletRequest request, ServletResponse response)
```

|Forward()	                                          |SendRediret()                                           |
|-----------------------------------------------------|--------------------------------------------------------
|The forward() method is executed in the server side. |	The sendRedirect() method is executed in the client side.
|The request is transfer to other resource within same server.|	The request is transfer to other resource to different server.|
|It does not depend on the client’s request protocol since the forward ( ) method is provided by the servlet container.|	The sendRedirect() method is provided under HTTP so it can be used only with HTTP clients.|
|The request is shared by the target resource. | New request is created for the destination resource.|
|Only one call is consumed in this method. |Two request and response calls are consumed.
|It can be used within server. | It can be used within and outside the server.|
|The forward() method is faster than sendRedirect() method.	|The sendRedirect() method is slower because when new request is created old request object is lost.|
|It is declared in RequestDispatcher interface. |It is declared in HttpServletResponse.
|Signature: _forward(ServletRequest request, ServletResponse response)_ |Signature: _void sendRedirect(String url)_ |

Example: sendRedirect() method
```java
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RedirectServlet extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse res) 
        throws ServletException, IOException {

		res.setContentType("text/html");
		PrintWriter out = res.getWriter();

		res.sendRedirect("https://www.java.com/en/");
		out.close();
	}
}
```

Example: forward() method
```html
//index.html

<!DOCTYPE html>
<html>
 <head>
    <meta charset="ISO-8859-1">
    <title>Insert title here</title>
 </head>
<body>
    <form action="Simple" method="get">
        Name: <input type="text" name="username">
        password: <input type="password" name="password"><br />
    <input type="submit" value="Submit" />
    </form>
</body>
</html>
```
SimpleServlet.java
```java
package javaexample.net.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SimpleServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		String str = request.getParameter("username");
		String st = request.getParameter("password");
		
		if (st.equals("javaexample")) {
			RequestDispatcher rd = request.getRequestDispatcher("Welcome");
			rd.forward(request, response);
		} else {
			out.print("Sorry username or password incorrect!");
			RequestDispatcher rd = request.getRequestDispatcher("/index.html");
			rd.include(request, response);
		}
	}
}
```

#### Q. Explain Servlets Lifecycle?

The web container maintains the life cycle of a servlet instance. 

**Stages of the Servlet Life Cycle**: 
1. The servlet is initialized by calling the **init()** method.
1. The servlet calls **service()** method to process a client's request.
1. The servlet is terminated by calling the **destroy()** method.
1. Finally, servlet is garbage collected by the garbage collector of the JVM.

* **The init() Method**
The web container calls the init method only once after creating the servlet instance. The init method is used to initialize the servlet. It is the life cycle method of the javax.servlet.Servlet interface.

Syntax 
```java
public void init(ServletConfig config) throws ServletException {
    // Initialization code...
}
```

* **The service() Method**
The servlet container calls the service() method to handle requests coming from the client and to write the formatted response back to the client. The service() method checks the HTTP request type (GET, POST, PUT, DELETE, etc.) and calls doGet, doPost, doPut, doDelete, etc. 

Syntax
```java
public void service(ServletRequest request, ServletResponse response) 
   throws ServletException, IOException {
}
```

* **The doGet() Method**
A GET request results from a normal request for a URL or from an HTML form that has no METHOD specified and it should be handled by doGet() method.

Syntax
```java
public void doGet(HttpServletRequest request, HttpServletResponse response)
   throws ServletException, IOException {
   // Servlet code
}
```

* **The doPost() Method**
A POST request results from an HTML form that specifically lists POST as the METHOD and it should be handled by doPost() method.

Syntax
```java
public void doPost(HttpServletRequest request, HttpServletResponse response)
   throws ServletException, IOException {
   // Servlet code
}
```

* **The destroy() Method**
The web container calls the destroy method before removing the servlet instance from the service. It gives the servlet an opportunity to clean up any resource for example memory, thread etc.

Syntax
```java
public void destroy() {
   // Finalization code...
}
```


#### Q. What are the major additions for jdk from 1.7 to 1.8?

Java 8 provides following features for Java Programming:

* Lambda expressions
* Method references
* Functional interfaces
* Stream API
* Default methods
* Base64 Encode Decode
* Static methods in interface
* Optional class
* Collectors class
* ForEach() method
* Parallel array sorting
* Nashorn JavaScript Engine
* Parallel Array Sorting
* Type and Repating Annotations
* IO Enhancements
* Concurrency Enhancements
* JDBC Enhancements etc.

#### Q. How serialization works in java?

Serialization is a mechanism of converting the state of an object into a byte stream. Deserialization is the reverse process where the byte stream is used to recreate the actual Java object in memory. This mechanism is used to persist the object.

Example:
```java
/**
* Serialization and Deserialization  
* example of a Java object 
*
**/
import java.io.*; 
  
class Employee implements Serializable { 
private static final long serialversionUID = 
                                 129348938L; 
    transient int a; 
    static int b; 
    String name; 
    int age; 
  
    // Default constructor 
    public Employee(String name, int age, int a, int b) { 
        this.name = name; 
        this.age = age; 
        this.a = a; 
        this.b = b; 
    }
} 
  
public class SerialExample { 

    public static void printdata(Employee object1) { 
        System.out.println("name = " + object1.name); 
        System.out.println("age = " + object1.age); 
        System.out.println("a = " + object1.a); 
        System.out.println("b = " + object1.b); 
    } 
  
    public static void main(String[] args) { 
        Employee object = new Employee("ab", 20, 2, 1000); 
        String filename = "shubham.txt"; 
  
        // Serialization 
        try { 
            // Saving of object in a file 
            FileOutputStream file = new FileOutputStream(filename); 
            ObjectOutputStream out = new ObjectOutputStream(file); 
  
            // Method for serialization of object 
            out.writeObject(object); 
  
            out.close(); 
            file.close(); 
  
            System.out.println("Object has been serialized\n"
                              + "Data before Deserialization."); 
            printdata(object); 
            // value of static variable changed 
            object.b = 2000; 
        } 
        catch (IOException ex) { 
            System.out.println("IOException is caught"); 
        } 
  
        object = null; 
  
        // Deserialization 
        try { 
            // Reading the object from a file 
            FileInputStream file = new FileInputStream(filename); 
            ObjectInputStream in = new ObjectInputStream(file); 
  
            // Method for deserialization of object 
            object = (Employee)in.readObject(); 
  
            in.close(); 
            file.close(); 
            System.out.println("Object has been deserialized\n"
                                + "Data after Deserialization."); 
            printdata(object); 
            System.out.println("z = " + object1.z); 
        } 
        catch (IOException ex) { 
            System.out.println("IOException is caught"); 
        } 
        catch (ClassNotFoundException ex) { 
            System.out.println("ClassNotFoundException is caught"); 
        } 
    } 
}
```

#### Q. What are the various ways to load a class in Java?

1. **Creating a reference**:
```java
SomeClass someInstance = null;
```

2. **Using Class.forName(String)**:
```java
 Class.forName("SomeClass");
```

3. **Using SystemClassLoader()**: 
```java
ClassLoader.getSystemClassLoader().loadClass("SomeClass");
```

4. **Using Overloaded Class.forName()**:
```java
Class.forName(String name, boolean initialize, ClassLoader loader);
```

#### Q. Java Program to Implement Singly Linked List

The singly linked list is a linear data structure in which each element of the list contains a pointer which points to the next element in the list. Each element in the singly linked list is called a node. Each node has two components: data and a pointer next which points to the next node in the list. 

Example:
```java
public class SinglyLinkedList {    
    // Represent a node of the singly linked list    
    class Node{    
        int data;    
        Node next;    
            
        public Node(int data) {    
            this.data = data;    
            this.next = null;    
        }    
    }    
     
    // Represent the head and tail of the singly linked list    
    public Node head = null;    
    public Node tail = null;    
        
    // addNode() will add a new node to the list    
    public void addNode(int data) {    
        // Create a new node    
        Node newNode = new Node(data);    
            
        // Checks if the list is empty    
        if(head == null) {    
            // If list is empty, both head and tail will point to new node    
            head = newNode;    
            tail = newNode;    
        }    
        else {    
            // newNode will be added after tail such that tail's next will point to newNode    
            tail.next = newNode;    
            // newNode will become new tail of the list    
            tail = newNode;    
        }    
    }    
        
    // display() will display all the nodes present in the list    
    public void display() {    
        // Node current will point to head    
        Node current = head;    
            
        if(head == null) {    
            System.out.println("List is empty");    
            return;    
        }    
        System.out.println("Nodes of singly linked list: ");    
        while(current != null) {    
            // Prints each node by incrementing pointer    
            System.out.print(current.data + " ");    
            current = current.next;    
        }    
        System.out.println();    
    }    
        
    public static void main(String[] args) {    
            
        SinglyLinkedList sList = new SinglyLinkedList();    
            
        // Add nodes to the list    
        sList.addNode(10);    
        sList.addNode(20);    
        sList.addNode(30);    
        sList.addNode(40);    
            
        // Displays the nodes present in the list    
        sList.display();    
    }    
}  
```
**Output:**
```java
Nodes of singly linked list: 
10 20 30 40
```

#### Q. Design patterns related question (Singleton, Adaptor, Factory, Strategy) 

* **Java Singleton Pattern**

1. Eager initialization:

In eager initialization, the instance of Singleton Class is created at the time of class loading.

Example:
```java
public class EagerInitializedSingleton {
    
    private static final EagerInitializedSingleton instance = new EagerInitializedSingleton();
    
    // private constructor to avoid client applications to use constructor
    private EagerInitializedSingleton(){}

    public static EagerInitializedSingleton getInstance(){
        return instance;
    }
}
```

2. Static block initialization:

Static block initialization implementation is similar to eager initialization, except that instance of class is created in the static block that provides option for exception handling.

Example:
```java
public class StaticBlockSingleton  {

    private static StaticBlockSingleton  instance;
    
    private StaticBlockSingleton (){}
    
    // static block initialization for exception handling
    static{
        try{
            instance = new StaticBlockSingleton ();
        }catch(Exception e){
            throw new RuntimeException("Exception occured in creating Singleton instance");
        }
    }
    
    public static StaticBlockSingleton getInstance(){
        return instance;
    }
}
```

3. Lazy Initialization

Lazy initialization method to implement Singleton pattern creates the instance in the global access method.

Example:
```java
public class LazyInitializedSingleton  {

    private static LazyInitializedSingleton  instance;
    
    private LazyInitializedSingleton(){}
    
    public static LazyInitializedSingleton  getInstance(){
        if(instance == null){
            instance = new LazyInitializedSingleton ();
        }
        return instance;
    }
}
```

4. Thread Safe Singleton

The easier way to create a thread-safe singleton class is to make the global access method synchronized, so that only one thread can execute this method at a time.

Example:
```java
public class ThreadSafeSingleton {

    private static ThreadSafeSingleton instance;
    
    private ThreadSafeSingleton(){}
    
    public static synchronized ThreadSafeSingleton getInstance(){
        if(instance == null){
            instance = new ThreadSafeSingleton();
        }
        return instance;
    }
}
```

5. Bill Pugh Singleton Implementation:

Prior to Java5, memory model had a lot of issues and above methods caused failure in certain scenarios in multithreaded environment. So, Bill Pugh suggested a concept of inner static classes to use for singleton.

Example:
```java
public class BillPughSingleton {

    private BillPughSingleton(){}
    
    private static class SingletonHelper{
        private static final BillPughSingleton INSTANCE = new BillPughSingleton();
    }
    
    public static BillPughSingleton getInstance(){
        return SingletonHelper.INSTANCE;
    }
}
```

* **Adapter Design Pattern in Java**

Adapter design pattern is one of the structural design pattern and its used so that two unrelated interfaces can work together. The object that joins these unrelated interface is called an Adapter.

Example:

we have two incompatible interfaces: **MediaPlayer** and **MediaPackage**. MP3 class is an implementation of the MediaPlayer interface and we have VLC and MP4 as implementations of the MediaPackage interface. We want to use MediaPackage implementations as MediaPlayer instances. So, we need to create an adapter to help to work with two incompatible classes.

MediaPlayer.java
```java
public interface MediaPlayer {
    void play(String filename);
}
```

MediaPackage.java
```java
public interface MediaPackage {
    void playFile(String filename);
}
```

MP3.java
```java
public class MP3 implements MediaPlayer {
 @Override
 public void play(String filename) {
    System.out.println("Playing MP3 File " + filename);
 }
}
```

MP4.java
```java
public class MP4 implements MediaPackage {
    @Override
    public void playFile(String filename) {
        System.out.println("Playing MP4 File " + filename);
    }
}
```

VLC.java
```java
public class VLC implements MediaPackage {
    @Override
    public void playFile(String filename) {
        System.out.println("Playing VLC File " + filename);
    }
}
```

FormatAdapter.java
```java
public class FormatAdapter implements MediaPlayer {
    private MediaPackage media;
    public FormatAdapter(MediaPackage m) {
        media = m;
    }
    @Override
    public void play(String filename) {
        System.out.print("Using Adapter --> ");
        media.playFile(filename);
    }
}
```

Main.java
```java
public class Main {
    public static void main(String[] args) {
        MediaPlayer player = new MP3();
        player.play("file.mp3");
        player = new FormatAdapter(new MP4());
        player.play("file.mp4");
        player = new FormatAdapter(new VLC());
        player.play("file.avi");
    }
}
```

* **Java Factory Pattern**

A Factory Pattern or Factory Method Pattern says that just define an interface or abstract class for creating an object but let the subclasses decide which class to instantiate. In other words, subclasses are responsible to create the instance of the class.

Example: Calculate Electricity Bill
Plan.java
```java
import java.io.*;      
abstract class Plan {  
    protected double rate;  
    abstract void getRate();  

    public void calculateBill(int units){  
        System.out.println(units*rate);  
    }  
}  
```

DomesticPlan.java
```java
class  DomesticPlan extends Plan{  
    @override  
    public void getRate(){  
        rate=3.50;              
    }  
}
```

CommercialPlan.java
```java
class  CommercialPlan extends Plan{  
    @override   
    public void getRate(){   
        rate=7.50;  
    }   
}  
```

InstitutionalPlan.java
```java
class  InstitutionalPlan extends Plan{  
    @override  
    public void getRate(){   
        rate=5.50;  
   }   
} 
```

GetPlanFactory.java
```java
class GetPlanFactory {  
      
    // use getPlan method to get object of type Plan   
    public Plan getPlan(String planType){  
        if(planType == null){  
            return null;  
        }  
        if(planType.equalsIgnoreCase("DOMESTICPLAN")) {  
                return new DomesticPlan();  
            }   
        else if(planType.equalsIgnoreCase("COMMERCIALPLAN")){  
            return new CommercialPlan();  
        }   
        else if(planType.equalsIgnoreCase("INSTITUTIONALPLAN")) {  
            return new InstitutionalPlan();  
        }  
         return null;  
    }  
} 
```

GenerateBill.java
```java
import java.io.*;    
class GenerateBill {

    public static void main(String args[])throws IOException {  
      GetPlanFactory planFactory = new GetPlanFactory();  
        
      System.out.print("Enter the name of plan for which the bill will be generated: ");  
      BufferedReader br=new BufferedReader(new InputStreamReader(System.in));  
  
      String planName=br.readLine();  
      System.out.print("Enter the number of units for bill will be calculated: ");  
      int units=Integer.parseInt(br.readLine());  
  
      Plan p = planFactory.getPlan(planName);  
      // call getRate() method and calculateBill()method of DomesticPaln.  
  
       System.out.print("Bill amount for "+planName+" of  "+units+" units is: ");  
           p.getRate();  
           p.calculateBill(units);  
    }  
} 
```

* **Strategy Design Pattern in Java**

Strategy design pattern is one of the behavioral design pattern. Strategy pattern is used when we have multiple algorithm for a specific task and client decides the actual implementation to be used at runtime.

Example: Simple Shopping Cart where we have two payment strategies – using Credit Card or using PayPal.

PaymentStrategy.java
```java
public interface PaymentStrategy {
	public void pay(int amount);
}
```

CreditCardStrategy.java
```java
public class CreditCardStrategy implements PaymentStrategy {

	private String name;
	private String cardNumber;
	private String cvv;
	private String dateOfExpiry;
	
	public CreditCardStrategy(String nm, String ccNum, String cvv, String expiryDate){
		this.name=nm;
		this.cardNumber=ccNum;
		this.cvv=cvv;
		this.dateOfExpiry=expiryDate;
	}
	@Override
	public void pay(int amount) {
		System.out.println(amount +" paid with credit/debit card");
	}
}
```

PaypalStrategy.java
```java
public class PaypalStrategy implements PaymentStrategy {

	private String emailId;
	private String password;
	
	public PaypalStrategy(String email, String pwd){
		this.emailId=email;
		this.password=pwd;
	}
	@Override
	public void pay(int amount) {
		System.out.println(amount + " paid using Paypal.");
	}
}
```

Item.java
```java
public class Item {

	private String upcCode;
	private int price;
	
	public Item(String upc, int cost){
		this.upcCode=upc;
		this.price=cost;
	}
	public String getUpcCode() {
		return upcCode;
	}
	public int getPrice() {
		return price;
	}
}
```

ShoppingCart.java
```java
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {

	List<Item> items;
	
	public ShoppingCart(){
		this.items=new ArrayList<Item>();
	}
	public void addItem(Item item){
		this.items.add(item);
	}
	public void removeItem(Item item){
		this.items.remove(item);
	}
	public int calculateTotal(){
		int sum = 0;
		for(Item item : items){
			sum += item.getPrice();
		}
		return sum;
	}
	public void pay(PaymentStrategy paymentMethod){
		int amount = calculateTotal();
		paymentMethod.pay(amount);
	}
}
```

ShoppingCartTest.java
```java
public class ShoppingCartTest {

	public static void main(String[] args) {
		ShoppingCart cart = new ShoppingCart();
		
		Item item1 = new Item("1234",10);
		Item item2 = new Item("5678",40);
		
		cart.addItem(item1);
		cart.addItem(item2);
		
		// pay by paypal
		cart.pay(new PaypalStrategy("myemail@example.com", "mypwd"));
		
		// pay by credit card
		cart.pay(new CreditCardStrategy("Pankaj Kumar", "1234567890123456", "786", "12/15"));
	}
}
```

Output 
```java
500 paid using Paypal.
500 paid with credit/debit card
```

#### Q. Explain types of spring bean scopes. 

The core of spring framework is it’s bean factory and mechanisms to create and manage such beans inside Spring container. The beans in spring container can be created in six scopes i.e. singleton, prototype, request, session, application and websocket. They are called spring bean scopes.

|SCOPE	                | DESCRIPTION                                                                              |
|-----------------------|-----------------------------------------------------------------------------------------|
|singleton (default)	|Single bean object instance per spring IoC container                                      |
|prototype	            |Opposite to singleton, it produces a new instance each and every time a bean is requested.|
|request	            |A single instance will be created and available during complete lifecycle of an HTTP request. Only valid in web-aware Spring ApplicationContext.|
|session	            |A single instance will be created and available during complete lifecycle of an HTTP Session. Only valid in web-aware Spring ApplicationContext.|
|application	        |A single instance will be created and available during complete lifecycle of ServletContext. Only valid in web-aware Spring ApplicationContext.|
|websocket	            |A single instance will be created and available during complete lifecycle of WebSocket. Only valid in web-aware Spring ApplicationContext.|

1. singleton scope

singleton is default bean scope in spring container. It tells the container to create and manage only one instance of bean class, per container. This single instance is stored in a cache of such singleton beans, and all subsequent requests and references for that named bean return the cached instance.

Example of singleton scope bean using Java config –
```java
@Component
// This statement is redundant - singleton is default scope
@Scope("singleton")  // This statement is redundant
public class BeanClass {
 
}
```
Example of singleton scope bean using XML config –
```xml
<!-- To specify singleton scope is redundant -->
<bean id="beanId" class="com.springexample.BeanClass" scope="singleton" />
// or
<bean id="beanId" class="com.springexample.BeanClass" />
```

2. prototype scope

prototype scope results in the creation of a new bean instance every time a request for the bean is made by application code.

Java config example of prototype bean scope –
```java
@Component
@Scope("prototype")
public class BeanClass {
}
```

XML config example of prototype bean scope –
```xml
<bean id="beanId" class="com.springexample.BeanClass" scope="prototype" />
```

3. request scope

In request scope, container creates a new instance for each and every HTTP request. So, if server is currently handling 5 requests, then container can have at most 5 individual instances of bean class. 

Java config example of request bean scope –
```java
@Component
@Scope("request")
public class BeanClass {
}
 
// or
 
@Component
@RequestScope
public class BeanClass {
}
```

XML config example of request bean scope –
```xml
<bean id="beanId" class="com.springexample.BeanClass" scope="request" />
```

4. session scope

In session scope, container creates a new instance for each and every HTTP session. So, if server has 10 active sessions, then container can have at most 10 individual instances of bean class. All HTTP requests within single session lifetime will have access to same single bean instance in that session scope.

Java config example of session bean scope –
```java
@Component
@Scope("session")
public class BeanClass {
}
 
// or
 
@Component
@SessionScope
public class BeanClass {
}
```

XML config example of session bean scope –
```xml
<bean id="beanId" class="com.springexample.BeanClass" scope="session" />
```

5. application scope

In application scope, container creates one instance per web application runtime. It is almost similar to singleton scope, with only two differences i.e.

* application scoped bean is singleton per ServletContext, whereas singleton scoped bean is singleton per ApplicationContext. Please note that there can be multiple application contexts for single application.
* application scoped bean is visible as a ServletContext attribute.

Java config example of application bean scope –
```java
@Component
@Scope("application")
public class BeanClass {
}
 
// or
 
@Component
@ApplicationScope
public class BeanClass {
}
```

XML config example of application bean scope –
```xml
<bean id="beanId" class="com.springexample.BeanClass" scope="application" />
```

6. websocket scope

The WebSocket Protocol enables two-way communication between a client and a remote host that has opted-in to communication with client. WebSocket Protocol provides a single TCP connection for traffic in both directions. 

Java config example of websocket bean scope –
```java
@Component
@Scope("websocket")
public class BeanClass {
}
```

XML config example of websocket bean scope –
```xml
<bean id="beanId" class="com.springexample.BeanClass" scope="websocket" />
```

#### Q. What is AOP? what does spring AOP provide?

Spring AOP enables Aspect-Oriented Programming in spring applications. In AOP, aspects enable the modularization of concerns such as transaction management, logging or security that cut across multiple types and objects (often termed crosscutting concerns).

AOP provides the way to dynamically add the cross-cutting concern before, after or around the actual logic using simple pluggable configurations. It makes easy to maintain code in the present and future as well.

* **Aspect Oriented Programming Core Concepts**

1. **Aspect**: An aspect is a class that implements enterprise application concerns that cut across multiple classes, such as transaction management. 

2. **Join Point**: A join point is the specific point in the application such as method execution, exception handling, changing object variable values etc. In Spring AOP a join points is always the execution of a method.

3. **Advice**: Advices are actions taken for a particular join point. In terms of programming, they are methods that gets executed when a certain join point with matching pointcut is reached in the application.

4. **Pointcut**: Pointcut are expressions that is matched with join points to determine whether advice needs to be executed or not. Pointcut uses different kinds of expressions that are matched with the join points and Spring framework uses the AspectJ pointcut expression language.

5. **Weaving**: It is the process of linking aspects with other objects to create the advised proxy objects. This can be done at compile time, load time or at runtime. Spring AOP performs weaving at the runtime.

* **Types of Advices**

1. **Before Advice**: These advices runs before the execution of join point methods. We can use @Before annotation to mark an advice type as Before advice.

2. **After returning advice**: Advice to be executed after a join point completes normally: for example, if a method returns without throwing an exception.

3. **After throwing advice**: Advice to be executed if a method exits by throwing an exception.

4. **After advice**: Advice to be executed regardless of the means by which a join point exits.

5. **Around advice**: Around advice can perform custom behavior before and after the method invocation. This type of advice is used where we need frequent access to a method or database like- caching.

Example: Types of Advices 
```java
/**
* AOP program to illustrate types of Advices
*
*// 
@Aspect
class Logging { 
    
    // **Before** 
    @Before("execution(public void com.aspect.ImplementAspect.aspectCall())") 
    public void loggingAdvice1() { 
        System.out.println("Before advice is executed"); 
    } 
  
    // **After** 
    @After("execution(public void com.aspect.ImplementAspect.aspectCall())") 
    public void loggingAdvice2() { 
        System.out.println("Running After Advice."); 
    } 
  
    // **Around** 
    @Around("execution(public void com.aspect.ImplementAspect.myMethod())") 
    public void loggingAdvice3() { 
        System.out.println("Before and After invoking method myMethod"); 
    } 
  
    // **AfterThrowing** 
    @AfterThrowing("execution(" public void com.aspect.ImplementAspect.aspectCall())") 
    public void loggingAdvice4() { 
        System.out.println("Exception thrown in method"); 
    } 
  
    // **AfterRunning** 
    @AfterReturning("execution(public void com.aspect.ImplementAspect.myMethod())") 
    public void loggingAdvice5() { 
        System.out.println("AfterReturning advice is run"); 
    } 
}
```

Example: JoinPoints
```java
/**
* AOP program to illustrate JoinPoints
*
**/
  
@Aspect
class Logging { 
  
    // Passing a JoinPoint Object into parameters of the method 
    // with the annotated advice enables to print the information 
  
    @Before("execution(public void com.aspect.ImplementAspect.aspectCall())") 
    public void loggingAdvice1(JoinPoint joinpoint) { 
        System.out.println("Before advice is executed"); 
        System.out.println(joinpoint.toString()); 
    } 
} 
```
Example: PointCuts 
```java
/**
* AOP program to illustrate PointCuts 
*
**/
@Aspect
class Logging { 

    @Pointcut("execution(public void com.aspect.ImplementAspect.aspectCall())") 
    public void pointCut() { 
    } 
  
    // pointcut() is used to avoid repeatition of code 
    @Before("pointcut()") 
    public void loggingAdvice1() { 
        System.out.println("Before advice is executed"); 
    } 
} 
```

#### Q. What is ServletContext Interface?

ServletContext is a configuration Object which is created when web application is started. It contains different initialization parameter that can be configured in web.xml.

* **ServletContext Interface Methods**

1. **public String getInitParameter(String name)**: Returns the parameter value for the specified parameter name.
2. **public Enumeration getInitParameterNames()**: Returns the names of the context's initialization parameters.
3. **public void setAttribute(String name,Object object)**: sets the given object in the application scope.
4. **public Object getAttribute(String name)**: Returns the attribute for the specified name.
5. **public Enumeration getInitParameterNames()**: Returns the names of the context's initialization parameters as an Enumeration of String objects.
6. **public void removeAttribute(String name)**: Removes the attribute with the given name from the servlet context.

Example: DemoServlet.java
```java
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
public class DemoServlet extends HttpServlet{
   public void doGet(HttpServletRequest request,HttpServletResponse response)
   throws ServletException,IOException {

       response.setContentType("text/html");
       PrintWriter pwriter=response.getWriter();

       // ServletContext object creation
       ServletContext scontext=getServletContext();

       // fetching values of initialization parameters and printing it
       String userName=scontext.getInitParameter("uname");
       pwriter.println("User name is="+userName);
       String userEmail=scontext.getInitParameter("email");
       pwriter.println("Email Id is="+userEmail);
       pwriter.close();
   }
}
```
web.xml
```xml
<web-app>
    <servlet>
        <servlet-name>UserDetails</servlet-name>
        <servlet-class>DemoServlet</servlet-class>
    </servlet>
    <context-param>
        <param-name>uname</param-name>
        <param-value>Pradeep Kumar</param-value>
    </context-param>
    <context-param>
        <param-name>email</param-name>
        <param-value>pradeep.vwa@gmail.com</param-value>
    </context-param>
    <servlet-mapping>
        <servlet-name>UserDetails</servlet-name>
    <url-pattern>/context</url-pattern>
    </servlet-mapping>
</web-app>
```

#### Q. How Set/HashSet implement unique values?

Java HashSet class is used to create a collection that uses a hash table for storage. It inherits the AbstractSet class and implements Set interface.

* HashSet stores the elements by using a mechanism called hashing.
* HashSet contains unique elements only.
* HashSet allows null value.
* HashSet class is non synchronized.
* HashSet doesn't maintain the insertion order. Here, elements are inserted on the basis of their hashcode.
* HashSet is the best approach for search operations.
* The initial default capacity of HashSet is 16, and the load factor is 0.75.

Example:
```java
import java.util.*;  
class HashSetExample {  

 public static void main(String args[]){  

    // Creating HashSet and adding elements  
    HashSet<String> set=new HashSet();  
           set.add("10");    
           set.add("20");    
           set.add("30");   
           set.add("40");  
           set.add("50");  
           Iterator<String> i=set.iterator();  
           while(i.hasNext()) {  
             System.out.println(i.next());  
           }  
    }  
}  
```

When we create a HashSet, it internally creates a HashMap and if we insert an element into this HashSet using add() method, it actually call put() method on internally created HashMap object with element you have specified as it’s key and constant Object called **PRESENT** as it’s value. So we can say that a Set achieves uniqueness internally through HashMap. 

#### Q. What is Comparable and Comparator Interface in java?

Comparable and Comparator both are interfaces and can be used to sort collection elements.

|Comparable	                |Comparator                                                                                |
|---------------------------|------------------------------------------------------------------------------------------|
|1) Comparable provides a single sorting sequence. In other words, we can sort the collection on the basis of a single element such as id, name, and price. |The Comparator provides multiple sorting sequences. In other words, we can sort the collection on the basis of multiple elements such as id, name, and price etc.|
|2) Comparable affects the original class, i.e., the actual class is modified.|Comparator doesn't affect the original class, i.e., the actual class is not modified.|
|3) Comparable provides compareTo() method to sort elements. | Comparator provides compare() method to sort elements.
|4) Comparable is present in java.lang package.|A Comparator is present in the java.util package.|
5) We can sort the list elements of Comparable type by Collections.sort(List) method.|We can sort the list elements of Comparator type by Collections.sort(List, Comparator) method.|

Example:
```java
/**
* Java Program to demonstrate the use of Java Comparable.
*
**/
import java.util.*;  
import java.io.*;  

class Student implements Comparable<Student>{  
    int rollno;  
    String name;  
    int age;  
    Student(int rollno,String name,int age){  
        this.rollno=rollno;  
        this.name=name;  
        this.age=age;  
    }  
    public int compareTo(Student st){  
        if(age==st.age)  
          return 0;  
        else if(age>st.age)  
          return 1;  
        else  
          return -1;  
    }  
}  

// Creating a test class to sort the elements  
public class ComparableMain {  
    public static void main(String args[]) {  
        ArrayList<Student> al=new ArrayList<Student>();  
        al.add(new Student(101,"Ryan Frey",23));  
        al.add(new Student(106,"Kenna Bean",27));  
        al.add(new Student(105,"Jontavius Herrell",21));  

        Collections.sort(al);  
        for(Student st:al){  
            System.out.println(st.rollno+" "+st.name+" "+st.age);  
        }  
    }  
}  
```
Example: Java Comparator 
Student.java
```java
class Student {  
    int rollno;  
    String name;  
    int age;  
    Student(int rollno,String name,int age) {  
      this.rollno=rollno;  
      this.name=name;  
      this.age=age;  
    }  
}
```
AgeComparator.java
```java
import java.util.*;  

class AgeComparator implements Comparator<Student> {  
    public int compare(Student s1,Student s2) {  
    if(s1.age==s2.age)  
      return 0;  
    else if(s1.age>s2.age)  
      return 1;  
    else  
      return -1;  
    } 
}  
```
NameComparator.java
```java
import java.util.*;  

class NameComparator implements Comparator<Student> {  
    public int compare(Student s1,Student s2) {  
        return s1.name.compareTo(s2.name);  
    }  
}  
```
TestComparator.java
```java
/**
* Java Program to demonstrate the use of Java Comparator  
*
**/
import java.util.*;  
import java.io.*; 

class TestComparator {  

    public static void main(String args[]) {  
        // Creating a list of students  
        ArrayList<Student> al=new ArrayList<Student>();  
        al.add(new Student(101,"Caelyn Romero",23));  
        al.add(new Student(106,"Olivea Gold",27));  
        al.add(new Student(105,"Courtlyn Kilgore",21));  
        
        System.out.println("Sorting by Name");  
        // Using NameComparator to sort the elements  
        Collections.sort(al,new NameComparator());  
        // Traversing the elements of list  
        for(Student st: al){  
          System.out.println(st.rollno+" "+st.name+" "+st.age);  
        }  
        
        System.out.println("sorting by Age");  
        // Using AgeComparator to sort the elements  
        Collections.sort(al,new AgeComparator());  
        // Travering the list again  
        for(Student st: al){  
          System.out.println(st.rollno+" "+st.name+" "+st.age);  
        }
    }  
}  
```

Output:
```java
Sorting by Name
106 Caelyn Romero 23
105 Courtlyn Kilgore 21
101 Olivea Gold 27

Sorting by Age       
105 Courtlyn Kilgore 21
101 Caelyn Romero 23
106 Olivea Gold 27
```

#### Q. What is DAO factory design pattern in Java?

Data Access Object Pattern or DAO pattern is used to separate low level data accessing API or operations from high level business services.

DAO pattern is based on abstraction and encapsulation design principles and shields rest of application from any change in the persistence layer e.g. change of database from Oracle to MySQL, change of persistence technology e.g. from File System to Database.

Step 1: Create Value Object [ Student.java ]
```java
public class Student {
   private String name;
   private int rollNo;

   Student(String name, int rollNo){
      this.name = name;
      this.rollNo = rollNo;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public int getRollNo() {
      return rollNo;
   }

   public void setRollNo(int rollNo) {
      this.rollNo = rollNo;
   }
}
```

Step 2: Create Data Access Object Interface [ StudentDao.java ]
```java
import java.util.List;

public interface StudentDao {
   public List<Student> getAllStudents();
   public Student getStudent(int rollNo);
   public void updateStudent(Student student);
   public void deleteStudent(Student student);
}
```

Step 3: Create concrete class implementing above interface [ StudentDaoImpl.java ] 
```java
import java.util.ArrayList;
import java.util.List;

public class StudentDaoImpl implements StudentDao {
	
   // list is working as a database
   List<Student> students;

   public StudentDaoImpl(){
      students = new ArrayList<Student>();
      Student student1 = new Student("Robert",0);
      Student student2 = new Student("John",1);
      students.add(student1);
      students.add(student2);		
   }
   @Override
   public void deleteStudent(Student student) {
      students.remove(student.getRollNo());
      System.out.println("Student: Roll No " + student.getRollNo() + ", deleted from database");
   }

   // retrive list of students from the database
   @Override
   public List<Student> getAllStudents() {
      return students;
   }

   @Override
   public Student getStudent(int rollNo) {
      return students.get(rollNo);
   }

   @Override
   public void updateStudent(Student student) {
      students.get(student.getRollNo()).setName(student.getName());
      System.out.println("Student: Roll No " + student.getRollNo() + ", updated in the database");
   }
}
```
Step 4: Use the StudentDao to demonstrate Data Access Object pattern usage [ DaoPatternDemo.java ]
```java
public class DaoPatternDemo {
   public static void main(String[] args) {
      StudentDao studentDao = new StudentDaoImpl();

      // print all students
      for (Student student : studentDao.getAllStudents()) {
         System.out.println("Student: [RollNo : " + student.getRollNo() + ", Name : " + student.getName() + " ]");
      }

      // update student
      Student student =studentDao.getAllStudents().get(0);
      student.setName("Michael");
      studentDao.updateStudent(student);

      // get the student
      studentDao.getStudent(0);
      System.out.println("Student: [RollNo : " + student.getRollNo() + ", Name : " + student.getName() + " ]");		
   }
}
```
Output:
```java
Student: [RollNo : 0, Name : Robert ]
Student: [RollNo : 1, Name : John ]
Student: Roll No 0, updated in the database
Student: [RollNo : 0, Name : Michael ]
```

#### Q. How to stop a Thread in Java?

A thread is automatically destroyed when the run() method has completed. But it might be required to kill/stop a thread before it has completed its life cycle. Modern ways to suspend/stop a thread are by using a **boolean flag** and **Thread.interrupt()** method.

Example: Stop a thread Using a boolean variable
```java
/**
* Java program to illustrate 
* stopping a thread using boolean flag 
*
**/
class MyThread extends Thread {

    // Initially setting the flag as true 
    private volatile boolean flag = true;
     
    // This method will set flag as false
    public void stopRunning() {
        flag = false;
    }
     
    @Override
    public void run() {
                 
        // This will make thread continue to run until flag becomes false 
        while (flag) {
            System.out.println("I am running....");
        }
        System.out.println("Stopped Running....");
    }
}
 
public class MainClass {

    public static void main(String[] args) {

        MyThread thread = new MyThread();
        thread.start();
         
        try {
            Thread.sleep(100);
        } 
        catch (InterruptedException e) {
            e.printStackTrace();
        }
         
        // call stopRunning() method whenever you want to stop a thread
        thread.stopRunning();
    }   
}
```
Output:
```java
I am running….
I am running….
I am running….
I am running….
I am running….
Stopped Running….
```

Example: Stop a thread Using interrupt() Method
```java
/**
* Java program to illustrate 
* stopping a thread using interrupt() method 
*
**/
class MyThread extends Thread {

    @Override
    public void run() {

        while (!Thread.interrupted()) {
            System.out.println("I am running....");
        }
        System.out.println("Stopped Running.....");
    }
}
 
public class MainClass {

    public static void main(String[] args) {

        MyThread thread = new MyThread(); 
        thread.start();
         
        try {
            Thread.sleep(100);
        } 
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        // interrupting the thread         
        thread.interrupt();
    }   
}
```
Output:
```java
I am running….
I am running….
I am running….
I am running….
I am running….
Stopped Running….
```

#### Q. How Convert lower to upper case without using toUppercase() in java?

```java
/**
* Java program to Convert lower to upper case
* 
**/
public class toLowerCase {

    public static void main(String[] args) {
        toLowerCase(args[0]);
    }

    public static void toLowerCase(String a) {

        for (int i = 0; i< a.length(); i++) {
            char aChar = a.charAt(i);
            if (65 <= aChar && aChar<=90) {
                aChar = (char)( (aChar + 32) ); 
            }
            System.out.print(aChar);
         }
     }
}    
```
#### Q. While overriding a method can you throw another exception or broader exception? 

If a method declares to throw a given exception, the overriding method in a subclass can only declare to throw that exception or its subclass. This is because of polymorphism.

Example:
```java
class A {
   public void message() throws IOException {..}
}

class B extends A {
   @Override
   public void message() throws SocketException {..} // allowed

   @Override
   public void message() throws SQLException {..} // NOT allowed

   public static void main(String args[]) {
        A a = new B();
        try {
            a.message();
        } catch (IOException ex) {
            // forced to catch this by the compiler
        }
   }
}
```

#### Q. Difference between containsKey(), keySet() and values() in HashMap. 

* **The keySet() method**:
This method returns a Set view of all the keys in the map. The set is backed by the map, so changes to the map are reflected in the set, and vice-versa. 

* **The containsKey() method**:
It returns true if this map maps one or more keys to the specified value.

* **The values() methods**:
It returns a Collection view of the values contained in this map. The collection is backed by the map, so changes to the map are reflected in the collection, and vice-versa.

Example:
```java
/**
* Java program illustrating usage of HashMap class methods 
* keySet(), values(), containsKey() 
*
**/
import java.util.*; 
public class HashMapExample {

    public static void main(String args[]) {

        // Creation of HashMap 
        HashMap<String, String> map = new HashMap<>(); 
  
        // Adding values to HashMap as ("keys", "values") 
        map.put("Language", "Java"); 
        map.put("Platform", "Window"); 
        map.put("Code", "HashMap"); 
        map.put("Learn", "More"); 
  
        // containsKey() method is to check the presence of a particluar key
        if (map.containsKey("Code")) 
            System.out.println("Testing .containsKey : " + map.get("Code")); 
  
        // keySet() method returns all the keys in HashMap 
        Set<String> mapKeys = map.keySet(); 
        System.out.println("Initial keys  : " + mapKeys); 
  
  
        // values() method return all the values in HashMap 
        Collection<String> mapValues = map.values(); 
        System.out.println("Initial values : " + mapValues); 
  
        // Adding new set of key-value 
        map.put("Search", "JavaArticle"); 
  
        // Again using .keySet() and .values() methods 
        System.out.println("New Keys : " + mapKeys); 
        System.out.println("New Values: " + mapValues); 
    } 
} 
```

#### Q. What is checked, unchecked exception and errors? 

1. **Checked Exception**:

* These are the classes that extend <code>Throwable</code> except <code>RuntimeException</code> and <code>Error</code>.
* They are also known as compile time exceptions because they are checked at **compile time**, meaning the compiler forces us to either handle them with try/catch or indicate in the function signature that it <code>throws</code> them and forcing us to deal with them in the caller.
* They are programmatically recoverable problems which are caused by unexpected conditions outside the control of the code (e.g. database down, file I/O error, wrong input, etc).
* Example: IOException, SQLException, etc.

```java
import java.io.*; 
  
class Main { 
    public static void main(String[] args) { 
        FileReader file = new FileReader("C:\\assets\\file.txt"); 
        BufferedReader fileInput = new BufferedReader(file); 
          
        for (int counter = 0; counter < 3; counter++)  
            System.out.println(fileInput.readLine()); 
          
        fileInput.close(); 
    } 
} 
```
output:
```java
Exception in thread "main" java.lang.RuntimeException: Uncompilable source code - 
unreported exception java.io.FileNotFoundException; must be caught or declared to be 
thrown
    at Main.main(Main.java:5)
```
After adding IOException
```java
import java.io.*; 
  
class Main { 
    public static void main(String[] args) throws IOException { 
        FileReader file = new FileReader("C:\\assets\\file.txt"); 
        BufferedReader fileInput = new BufferedReader(file); 
           
        for (int counter = 0; counter < 3; counter++)  
            System.out.println(fileInput.readLine()); 
          
        fileInput.close(); 
    } 
} 
```
output:
```java
Output: First three lines of file “C:\assets\file.txt”
```

2. **Unchecked Exception**:

* The classes that extend <code>RuntimeException</code> are known as unchecked exceptions.
* Unchecked exceptions are not checked at compile-time, but rather at **runtime**, hence the name.
* They are also programmatically recoverable problems but unlike checked exception they are caused by faults in code flow or configuration.
* Example:  ArithmeticException,NullPointerException, ArrayIndexOutOfBoundsException, etc.

```java
class Main { 
   public static void main(String args[]) { 
      int x = 0; 
      int y = 10; 
      int z = y/x; 
  } 
} 
```
Output:
```java
Exception in thread "main" java.lang.ArithmeticException: / by zero
    at Main.main(Main.java:5)
Java Result: 1
```

3. **Error**:

* <code>Error</code> refers to an irrecoverable situation that is not being handled by a <code>try/catch</code>.
* Example: OutOfMemoryError, VirtualMachineError, AssertionError, etc.

#### Q. What's the difference between @Component, @Repository & @Service annotations in Spring?

* **@Component**
This is a general-purpose stereotype annotation indicating that the class is a spring component.
```java
@Component
public @interface Service {
    ….
}
```
* **@Repository**
This is to indicate that the class defines a database repository.
```xml
<bean class="org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor"/>
```
* **@Controller**
This indicate that the annotate classes at presentation layers level, mainly used in Spring MVC.

* **@Service**
<code>@Service</code> beans hold the business logic and call methods in the repository layer.

![alt text](https://github.com/learning-zone/Spring/blob/spring/assets/spring-component.png)

#### Q. Implement your own String class 
TODO
#### Q. Explain deadlock condition in-between two threads with example?

```java
public class DeadLockSimulator {
     
    public static Object Lock1 = new Object();
    public static Object Lock2 = new Object();

    private static class FirstThread extends Thread {
        public void run() {
            synchronized (Lock1) {
            System.out.println("Thread 1: Holding lock 1...");
            try { Thread.sleep(10); } catch (Exception e) {}
            System.out.println("Thread 1: Waiting for lock 2...");
            synchronized (Lock2) {
                System.out.println("Thread 1: Holding lock 1 & 2...");
            }
            }
        }
    }
    
    private static class SecondThread extends Thread {
        public void run() {
            synchronized (Lock2) {
            System.out.println("Thread 2: Holding lock 2...");
            try { Thread.sleep(10); } catch (Exception e) {}
            System.out.println("Thread 2: Waiting for lock 1...");
            synchronized (Lock1) {
                System.out.println("Thread 2: Holding lock 1 & 2...");
            }
            }
        }
    }
     
    public static void main(String args[]) {
         
        new FirstThread().start();
        new SecondThread().start();
    }
}
```
Output:
```java
"Thread-1" prio=6 tid=0x0000000007319000 nid=0x7cd3c waiting for monitor entry [0x0000000008a3f000]
   java.lang.Thread.State: BLOCKED (on object monitor)
    at com.tier1app.DeadLockSimulator$SecondThread.run(DeadLockSimulator.java:29)
    - waiting to lock 0x00000007ac3b1970 (a java.lang.Object)
    - locked 0x00000007ac3b1980 (a java.lang.Object)
 
   Locked ownable synchronizers:
    - None
 
"Thread-0" prio=6 tid=0x0000000007318800 nid=0x7da14 waiting for monitor entry [0x000000000883f000]
   java.lang.Thread.State: BLOCKED (on object monitor)
    at com.tier1app.DeadLockSimulator$FirstThread.run(DeadLockSimulator.java:16)
    - waiting to lock 0x00000007ac3b1980 (a java.lang.Object)
    - locked 0x00000007ac3b1970 (a java.lang.Object)
 
   Locked ownable synchronizers:
    - None
```

#### Q. What is the difference between Array and ArrayList data-structure? 

* **Resizable**: Implementation of array is simple fixed sized array but Implementation of ArrayList is dynamic sized array.
* **Primitives**: Array can contain both primitives and objects but ArrayList can contain only object elements
* **Generics**: We can’t use generics along with array but ArrayList allows us to use generics to ensure type safety.
* **Length**: We can use length variable to calculate length of an array but size() method to calculate size of ArrayList.
* **Store**: Array use assignment operator to store elements but ArrayList use add() to insert elements.

Example:
```java
/*
* A Java program to demonstrate differences between array 
* and ArrayList 
*
**/
import java.util.ArrayList; 
import java.util.Arrays; 

class ArrayExample {

    public static void main(String args[]) { 
        
        /* ........... Normal Array............. */
        // Need to specify the size for array  
        int[] arr = new int[3]; 
        arr[0] = 10; 
        arr[1] = 20; 
        arr[2] = 30; 
        // We cannot add more elements to array arr[] 
  
        /*............ArrayList..............*/
        // Need not to specify size  
        ArrayList<Integer> arrL = new ArrayList<Integer>(); 
        arrL.add(10); 
        arrL.add(20); 
        arrL.add(30); 
        arrL.add(40); 
        // We can add more elements to arrL 
  
        System.out.println(arrL); 
        System.out.println(Arrays.toString(arr)); 
    } 
} 
```

#### Q. How microservices communicate with each other? 
*TODO*
#### Q. Array or ArrayList which one is faster?  
Array is faster
#### Q. How to prevent from database attacks/SQL Injection? 
*TODO*
#### Q. How to do SSO implementation using Spring Boot?
*TODO*
#### Q. Various ways that you implement JAAS.
*TODO*
#### Q. Assume you have a singly linked list, now you need to find reverse of it without using new operator.
*TODO* 
#### Q. Name design patterns used in collection framework.
*TODO*
#### Q. Object oriented feature like: Association, Composition, Aggregation
*TODO*
#### Q. Difference between DI and IOC in spring.
*TODO*
#### Q. Optimistic vs pessimistic locking in table level and row level.
*TODO*
#### Q. What is main advantage of RESTful implementation over SOAP?
*TODO*
#### Q. How do you implement secondary level cache? Clustering?
*TODO*
#### Q. Difference between symmetric, public and private keys, what are the procedure to implement the same using spring security.
*TODO*
#### Q. What code coverage tools are you using for your project? 
we use `cobetura`
#### Q. Implement thread-safe code without using the `synchronized` keyword? 
*TODO*
#### Q. Design parking lot with 100 parking space.
*TODO*
#### Q. Scenario of browser’s browsing history, where you need to store the browsing history, what data structure will you use.? 
use `stack`

#### Q. Scenario where in we have to download a big file by clicking on a link, how will you make sure that connections is reliable throughout. 
use `persistent MQueues`

#### Q. If you store Employee object as key say: Employee emp = new Employee(“name1”,20); store it in a HashMap as key, now if we add a new parameter emp.setMarriedStatus(true) and try to override it what will happen? 
new instance of Employee will be inserted to HashMap 

#### Q. Given a string "abc" or any other string print all possible combinations of it.
*TODO*
#### Q. Given table Employee and Department write a sql statement to find count of employees department
*TODO*
#### Q. Difference between classnotfound and noclassdeffound
*TODO*
#### Q. What do we mean by weak reference?
*TODO*
#### Q. Difference between hashset and linkedhashset
*TODO*
#### Q. What do you mean Run time Polymorphism?
*TODO*
#### Q. How will you create thread?
*TODO*
#### Q. Why implementing Runnable is better than extending thread?
*TODO*
#### Q. What is the use of Synchronized keyword?
*TODO*
#### Q. What is the difference between HashTable and HashMap?
*TODO*
#### Q. What will happen if I insert duplicate key-pair value inserted in to HashTable?
*TODO*
#### Q. Differentiate ArrayList from Vector?
*TODO*
#### Q. How do you sort out items in ArrayList in forward and reverse directions?
*TODO*
#### Q. Tell me about join() and wait() methods?
*TODO*
#### Q. If Parent Class have a method add() throws Exception, and child class overrides same method without Exception, will that compile and run? Is it overridden or overloaded? 
*TODO*
#### Q. If I don't have Explicit constructor in parent class and having in child class, while calling the child's constructor jvm automatically calls Implicit Constructor of parent class? Yes OR No ?
*TODO*
#### Q. What are the different types of JDBC Driver?
*TODO*
#### Q. How Encapsulation concept implemented in JAVA?
*TODO*
#### Q. Create a ordered collection, which allows duplicates? Which collection is used for this?
*TODO*
#### Q. Create a class, which have the behavior of Hash-map? 
*TODO*
#### Q. What hashCode() and equals() does in HashMap?
*TODO*
#### Q. Do you know Generics? How did you used in your coding?
*TODO*
#### Q. What is difference between String, StringBuilder and StringBuffer?
*TODO*
#### Q. How can we create a object of a class without using new operator?
Different ways to create an object in Java
* **Using new Keyword**
```java
class ObjectCreationExample{
	String Owner;
}
public class MainClass {
	public static void main(String[] args) {
		// Here we are creating Object of JBT using new keyword
		ObjectCreationExample obj = new ObjectCreationExample();
	}
}

```
* **Using New Instance (Reflection)**
```java
class CreateObjectClass {
	static int j = 10;
	CreateObjectClass() {
		i = j++;
	}
	int i;
	@Override
	public String toString() {
		return "Value of i :" + i;
	}
}

class MainClass {
	public static void main(String[] args) {
		try {
			Class cls = Class.forName("CreateObjectClass");
			CreateObjectClass obj = (CreateObjectClass) cls.newInstance();
			CreateObjectClass obj1 = (CreateObjectClass) cls.newInstance();
			System.out.println(obj);
			System.out.println(obj1);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}

```
* **Using Clone**
```java
 class CreateObjectWithClone implements Cloneable {
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	int i;
	static int j = 10;
	CreateObjectWithClone() {
		i = j++;
	}
	@Override
	public String toString() {
		return "Value of i :" + i;
	}
}

class MainClass {
	public static void main(String[] args) {
		CreateObjectWithClone obj1 = new CreateObjectWithClone();
		System.out.println(obj1);
		try {
			CreateObjectWithClone obj2 = (CreateObjectWithClone) obj1.clone();
			System.out.println(obj2);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}
}
```
* **Using ClassLoader**
```java
class CreateObjectWithClassLoader {
	static int j = 10;
	CreateObjectWithClassLoader() {
		i = j++;
	}
	int i;
	@Override
	public String toString() {
		return "Value of i :" + i;
	}
}

public class MainClass {
	public static void main(String[] args) {
		CreateObjectWithClassLoader obj = null;
		try {
			obj = (CreateObjectWithClassLoader) new MainClass().getClass()
					.getClassLoader().loadClass("CreateObjectWithClassLoader").newInstance();
        // Fully qualified classname should be used.
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println(obj);
	}
}
```


