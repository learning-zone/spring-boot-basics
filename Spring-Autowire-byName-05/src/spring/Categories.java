package spring;

public class Categories {
	private String name;
	private Book book;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Book getBook() {
		return book;
	}
	public void setBook(Book book) {
		this.book = book;
	}
	public void show() {
		System.out.println("Category: " + name);
		System.out.println("Book Name: " + book.getBookname());
		System.out.println("Book Price: "+ book.getBookprice());
	}

}
