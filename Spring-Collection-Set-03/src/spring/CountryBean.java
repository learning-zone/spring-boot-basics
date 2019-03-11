package spring;

import java.util.Set;

public class CountryBean {
	
	private Set countries;
	public void setCountries(Set countries) {
		this.countries = countries;
	}
	public void show() {
		System.out.println(countries);
	}

}
