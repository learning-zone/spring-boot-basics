package spring;

import java.lang.reflect.Method;
import org.springframework.aop.AfterReturningAdvice;

public class AfterAdvisor implements AfterReturningAdvice {

    public void afterReturning(Object returnValue, Method m, Object args[], Object target) throws Exception {
        System.out.println("AfterAdvice Executed --> "+ m.getName());
    }
}
