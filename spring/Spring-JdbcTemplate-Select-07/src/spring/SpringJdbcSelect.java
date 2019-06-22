package spring;
import java.util.Iterator;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;

public class SpringJdbcSelect {
	
	JdbcTemplate jdbcTemplate;
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	public void loadAll() {
		List list = jdbcTemplate.queryForList("Select * from users");
		Iterator itr = list.iterator();
		while(itr.hasNext()) {
			Object obj = itr.next();
			System.out.println(obj.toString());
		}
	}
}
