package pt.unl.fct.apdc.utils;

public class RegisterData {
	public String username;
	public String email;
	public String password;
	public String passwordVerif;
	public String mobilePhone;
	public String homePhone;
	public String address;
	public String addressComp;
	public String locality;
	public String profile;
	
	public RegisterData() {
		
	}
	
	public RegisterData(String username, String email, String password, String passwordVerif,
			String mobilePhone, String homePhone, String address, String addressComp,
			String locality, String profile) {
		this.username = username;
		this.password = password;
		this.passwordVerif = passwordVerif;
		this.email = email;
		this.mobilePhone = mobilePhone;
		this.homePhone = homePhone;
		this.address = address;
		this.addressComp = address;
		this.locality = locality;
		this.profile = profile;
	}

	public boolean validRegistration() {
		if(! email.matches("^([a-zA-Z0-9-.]+)@([a-zA-Z0-9-.]+).([a-zA-Z]{2,5})$"))
			return false;
		if (password.length() < 5)
			return false;
		if (! mobilePhone.matches("^(\\+351 )([9])([136])([0-9]{7})$")) 
			return false;
		if (! homePhone.matches("^(\\+351 )([0-9]{9})$"))
			return false;
		if (!profile.equals("Privado") && !profile.equals("Publico"))
			return false;
		return true;
	}
	public boolean validPassword() {
		return password.equals(passwordVerif);
	}
}
