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