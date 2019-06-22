package spring;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class MainApp {

	public static void main(String[] args) {
		Resource res = new ClassPathResource("config.xml");
		BeanFactory factory = new XmlBeanFactory(res);

		Object obj = factory.getBean("beanId");
		Categories cat = (Categories)obj;

		cat.show();
	}
}
