package spring;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class NumberBean {
	
	private Map data;
	public void setData(Map data) {
		this.data = data;
	}
	public void show() {
		Set s = data.entrySet();
		Iterator itr = s.iterator();
		while(itr.hasNext()) {
			Map.Entry me = (Map.Entry)itr.next();
			System.out.println(me.getKey() + "-" + me.getValue());
		}
	}
}
