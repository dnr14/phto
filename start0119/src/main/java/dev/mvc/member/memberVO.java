package dev.mvc.member;

public class memberVO {
	
	private String id;
	private String email;
	private String pwd;
	private String address1;
	private String address2;
	private String zipcode;
	private String phone;
	private String rdata;
	private String key;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getRdata() {
		return rdata;
	}
	public void setRdata(String rdata) {
		this.rdata = rdata;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	@Override
	public String toString() {
		return "memberVO [id=" + id + ", email=" + email + ", pwd=" + pwd + ", address1=" + address1 + ", address2="
				+ address2 + ", zipcode=" + zipcode + ", phone=" + phone + ", rdata=" + rdata + ", key=" + key + "]";
	}
	
}
