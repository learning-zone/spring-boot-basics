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

```
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
```
public interface Interface_Name {

}
```
Example:
```
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
```
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

```
import java.util.ArrayList; 
import java.util.Collection; 
import java.util.HashMap; 
import java.util.Map.Entry; 
import java.util.Set; 
public class MapToListExamples {

    public static void main(String[] args) {

        //Creating a HashMap object 
        HashMap<String, String> performanceMap = new HashMap<String, String>(); 
         
        //Adding elements to HashMap 
        performanceMap.put("John Kevin", "Average");  
        performanceMap.put("Ladarious Fernandez", "Very Good"); 
        performanceMap.put("Ivan Jose", "Very Bad"); 
        performanceMap.put("Smith Jacob", "Very Good"); 
        performanceMap.put("Athena Stiltner", "Bad"); 
         
        //Getting Set of keys 
        Set<String> keySet = performanceMap.keySet(); 
         
        //Creating an ArrayList of keys 
        ArrayList<String> listOfKeys = new ArrayList<String>(keySet); 
         
        System.out.println("ArrayList Of Keys :"); 
         
        for (String key : listOfKeys) {
            System.out.println(key); 
        }
                  
        //Getting Collection of values 
        Collection<String> values = performanceMap.values(); 
         
        //Creating an ArrayList of values 
        ArrayList<String> listOfValues = new ArrayList<String>(values); 
         
        System.out.println("ArrayList Of Values :"); 
         
        for (String value : listOfValues) { 
            System.out.println(value); 
        } 
                  
        //Getting the Set of entries 
        Set<Entry<String, String>> entrySet = performanceMap.entrySet(); 
         
        //Creating an ArrayList Of Entry objects 
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
|:--------------------------------------------------------------------|---------------------------------------------------
| ArrayList internally uses a dynamic array to store the elements.  |	LinkedList internally uses a doubly linked list to                                                                           store the elements. |
| Manipulation with ArrayList is slow because it internally uses an array. If any element is removed from the array, all the bits are shifted in memory.|Manipulation with LinkedList is faster than ArrayList because it uses a doubly linked list, so no bit shifting is required in memory.|
| An ArrayList class can act as a list only because it implements List only.|	LinkedList class can act as a list and queue |both because it implements List and Deque interfaces.|
| ArrayList is better for storing and accessing data. 	          |LinkedList is better for manipulating data.|


#### Q. What is JSP Implicit Object? 

* **request**: This is the HttpServletRequest object associated with the request.

Example: index.jsp

```
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
```
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
```
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
```
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
```
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
```
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
```
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
```
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
```
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
```
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
```
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
```
void sendRedirect(String url)
```

* **Forward()**:
This method is declared in **RequestDispatcher** Interface. It is used to pass the request to another resource for further processing within the same server, another resource could be any servlet, jsp page any kind of file.

Signature:
```
forward(ServletRequest request, ServletResponse response)
```

|Forward()	                                          |SendRediret()
|:----------------------------------------------------|:--------------------------------------------------------
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
```
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
```
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
```
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
```
public void init(ServletConfig config) throws ServletException {
    // Initialization code...
}
```

* **The service() Method**
The servlet container calls the service() method to handle requests coming from the client and to write the formatted response back to the client. The service() method checks the HTTP request type (GET, POST, PUT, DELETE, etc.) and calls doGet, doPost, doPut, doDelete, etc. 

Syntax
```
public void service(ServletRequest request, ServletResponse response) 
   throws ServletException, IOException {
}
```

* **The doGet() Method**
A GET request results from a normal request for a URL or from an HTML form that has no METHOD specified and it should be handled by doGet() method.

Syntax
```
public void doGet(HttpServletRequest request, HttpServletResponse response)
   throws ServletException, IOException {
   // Servlet code
}
```

* **The doPost() Method**
A POST request results from an HTML form that specifically lists POST as the METHOD and it should be handled by doPost() method.

Syntax
```
public void doPost(HttpServletRequest request, HttpServletResponse response)
   throws ServletException, IOException {
   // Servlet code
}
```

* **The destroy() Method**
The web container calls the destroy method before removing the servlet instance from the service. It gives the servlet an opportunity to clean up any resource for example memory, thread etc.

Syntax
```
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
```
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
```
SomeClass someInstance = null;
```

2. **Using Class.forName(String)**:
```
 Class.forName("SomeClass");
```

3. **Using SystemClassLoader()**: 
```
ClassLoader.getSystemClassLoader().loadClass("SomeClass");
```

4. **Using Overloaded Class.forName()**:
```
Class.forName(String name, boolean initialize, ClassLoader loader);
```

#### Q. Java Program to Implement Singly Linked List

The singly linked list is a linear data structure in which each element of the list contains a pointer which points to the next element in the list. Each element in the singly linked list is called a node. Each node has two components: data and a pointer next which points to the next node in the list. 

Example:
```
public class SinglyLinkedList {    
    //Represent a node of the singly linked list    
    class Node{    
        int data;    
        Node next;    
            
        public Node(int data) {    
            this.data = data;    
            this.next = null;    
        }    
    }    
     
    //Represent the head and tail of the singly linked list    
    public Node head = null;    
    public Node tail = null;    
        
    //addNode() will add a new node to the list    
    public void addNode(int data) {    
        //Create a new node    
        Node newNode = new Node(data);    
            
        //Checks if the list is empty    
        if(head == null) {    
            //If list is empty, both head and tail will point to new node    
            head = newNode;    
            tail = newNode;    
        }    
        else {    
            //newNode will be added after tail such that tail's next will point to newNode    
            tail.next = newNode;    
            //newNode will become new tail of the list    
            tail = newNode;    
        }    
    }    
        
    //display() will display all the nodes present in the list    
    public void display() {    
        //Node current will point to head    
        Node current = head;    
            
        if(head == null) {    
            System.out.println("List is empty");    
            return;    
        }    
        System.out.println("Nodes of singly linked list: ");    
        while(current != null) {    
            //Prints each node by incrementing pointer    
            System.out.print(current.data + " ");    
            current = current.next;    
        }    
        System.out.println();    
    }    
        
    public static void main(String[] args) {    
            
        SinglyLinkedList sList = new SinglyLinkedList();    
            
        //Add nodes to the list    
        sList.addNode(10);    
        sList.addNode(20);    
        sList.addNode(30);    
        sList.addNode(40);    
            
        //Displays the nodes present in the list    
        sList.display();    
    }    
}  
```
**Output:**
```
Nodes of singly linked list: 
10 20 30 40
```

#### Q. Design patterns related question (Singleton, Adaptor, Factory, Strategy) 

* **Java Singleton Pattern**

1. **Eager initialization:**

In eager initialization, the instance of Singleton Class is created at the time of class loading.

Example:
```
public class EagerInitializedSingleton {
    
    private static final EagerInitializedSingleton instance = new EagerInitializedSingleton();
    
    //private constructor to avoid client applications to use constructor
    private EagerInitializedSingleton(){}

    public static EagerInitializedSingleton getInstance(){
        return instance;
    }
}
```

2. **Static block initialization:**

Static block initialization implementation is similar to eager initialization, except that instance of class is created in the static block that provides option for exception handling.

Example:
```
public class StaticBlockSingleton  {

    private static StaticBlockSingleton  instance;
    
    private StaticBlockSingleton (){}
    
    //static block initialization for exception handling
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

3. **Lazy Initialization**

Lazy initialization method to implement Singleton pattern creates the instance in the global access method.

Example:
```
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

4. **Thread Safe Singleton**

The easier way to create a thread-safe singleton class is to make the global access method synchronized, so that only one thread can execute this method at a time.

Example:
```
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

5. **Bill Pugh Singleton Implementation:**

Prior to Java5, memory model had a lot of issues and above methods caused failure in certain scenarios in multithreaded environment. So, Bill Pugh suggested a concept of inner static classes to use for singleton.

Example:
```
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

**MediaPlayer.java**
```
public interface MediaPlayer {
    void play(String filename);
}
```

**MediaPackage.java**
```
public interface MediaPackage {
    void playFile(String filename);
}
```

**MP3.java**
```
public class MP3 implements MediaPlayer {
 @Override
 public void play(String filename) {
    System.out.println("Playing MP3 File " + filename);
 }
}
```

**MP4.java**
```
public class MP4 implements MediaPackage {
    @Override
    public void playFile(String filename) {
        System.out.println("Playing MP4 File " + filename);
    }
}
```

**VLC.java**
```
public class VLC implements MediaPackage {
    @Override
    public void playFile(String filename) {
        System.out.println("Playing VLC File " + filename);
    }
}
```

**FormatAdapter.java**
```
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

**Main.java**
```
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
