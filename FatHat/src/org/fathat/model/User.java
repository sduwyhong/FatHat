package org.fathat.model;
/**
 * @author wyhong
 * @date 2018-5-4
 */
public class User {

	private int id;
	private String name;
	private String gender;
	private int age;
	
	public User(){}
	public User(int id, String name, String gender, int age) {
		super();
		this.id = id;
		this.name = name;
		this.gender = gender;
		this.age = age;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", gender=" + gender
				+ ", age=" + age + "]";
	}
	
}
