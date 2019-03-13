package spring;
import java.lang.reflect.Method;
import org.springframework.aop.MethodBeforeAdvice;

public class BeforeAdvisor implements MethodBeforeAdvice {

    public void before(Method m, Object args[], Object target)throws Exception {
        System.out.println("BeforeAdvice Executed --> "+ m.getName());
    }
}