## Spring Framework Annotations

|Sl.No| Annotation    | Description  |
|-----|---------------|--------------|
| 01. |@Autowired	  |Annotation @Autowired is used to inject object dependency implicitly for a constructor, field or method. This is known as “autowired by type” since the object to be injected is discovered by its type. The items declared @Autowired need not have to be public.|				
| 02. |@Configurable  |	Used on classes to inject properties of domain objects. Types whose properties are injected without being instantiated by Spring can be declared with @Configurable annotation.	|
| 03. |@Qualifier	  |It can be used to create more than one bean of the same type and wire only one of the types with a property. It provides greater control on the dependency injection process and can be used with @Autowired annotation.|
| 04. |@Required	  |Used to mark class members that are mandatory. The Spring auto-configuration fails if a particular property specified with this annotation cannot be injected.|
| 05.  |@ComponentScan|Make Spring scan the package for the @Configuration clases.|
| 06.  |@Configuration|It is used on classes that define beans.|
| 07.  |@Bean	      |It indicates that a method produces a bean which will be mananged by the Spring container.|			
| 08.  |@Lazy	      |Makes a @Bean or @Component to be initialized only if it is requested.|		
| 09.  |@Value	      |It is used to inject values into a bean’s attribute from a property file. @Value annotation indicates a default value expression for the field or parameter.|	


#### Spring-boot web Annotations

|Sl.No| Annotation            | Description  |
|-----|-----------------------|--------------|
| 01. |@SpringBootApplication |	This annotation is used to qualify the main class for a Spring Boot project. The class used with this annotation must be present in the base path. @SpringBootApplication scans for sub-packages by doing a component scan.|   			
| 02. |@EnableAutoConfiguration|Based on class path settings, property settings, new beans are added by Spring Boot by using this annotation.|					
| 03. |@Controller	           |Allows detection of component classes in the class path automatically and register bean definitions for the classes automatically.|					
| 04. |@RestController	       |Used in controllers that will behave as RESTful resources. @RestController is a convenience annotation that combines @Controller and @ResponseBody.	|				
| 05. |@ResponseBody	       |Makes Spring to convert the returned object to a response body. This is useful for classes exposed as RESTful resources.|				
| 06. |@RequestMapping	       |Used to map web requests to specific handler classes and methods, based on the URI.|
| 07. |@RequestParam	       |This annotation is used to bind request parameters to a method parameter in your controller.| 
| 08. |@PathVariable	       |This annotations binds the placeholder from the URI to the method parameter and can be used when the URI is dynamically created or the value of the URI itself acts as a parameter.	|	