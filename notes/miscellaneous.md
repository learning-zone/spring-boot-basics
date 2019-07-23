# Java, J2EE, Spring-boot Questions


#### Q. Spring Boot program for file upload / download
#### Q. Spring Boot program for Send Mail
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
#### Q. How bootstrap class loader works in java?

Bootstrap **ClassLoader** is repsonsible for loading standard JDK classs files from **rt.jar** and it is parent of all class loaders in java.
There are three types of built-in ClassLoader in Java:

1. **Bootstrap Class Loader** – It loads JDK internal classes, typically loads rt.jar and other core classes for example java.lang.* package classes
1. **Extensions Class Loader** – It loads classes from the JDK extensions directory, usually $JAVA_HOME/lib/ext directory.
1. **System Class Loader** – It loads classes from the current classpath that can be set while invoking a program using -cp or -classpath command line options.

#### Q. Why string is immutable in java? 
#### Q. What is Java String Pool?  
#### Q. Explain how Garbage collector algorithm works? 
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
