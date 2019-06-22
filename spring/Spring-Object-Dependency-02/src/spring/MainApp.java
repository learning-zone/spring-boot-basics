package spring;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class MainApp {

	public static void main(String[] args) {
		Resource res1 = new ClassPathResource("spconfig1.xml");
		BeanFactory factory1 = new XmlBeanFactory(res1);

		Resource res2 = new ClassPathResource("spconfig2.xml");
		BeanFactory factory2 = new XmlBeanFactory(res2,factory1);

		Object o = factory2.getBean("beanId03");
		Journey j = (Journey)o;

		j.startJourney();
	}
}
