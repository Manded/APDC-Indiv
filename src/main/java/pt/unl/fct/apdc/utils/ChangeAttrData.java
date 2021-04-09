package pt.unl.fct.apdc.utils;

public class ChangeAttrData {
	public String username;
	public String tokenID;
	public String mobilePhone;
	public String homePhone;
	public String address;
	public String addressComp;
	public String locality;
	public String profile;
	
	public ChangeAttrData() {
		
	}
	
	public ChangeAttrData(String username, String tokenID,String mobilePhone, String homePhone, String address,
			String addressComp, String locality, String profile) {
		this.username = username;
		this.tokenID = tokenID;
		this.mobilePhone = mobilePhone;
		this.homePhone = homePhone;
		this.address = address;
		this.addressComp = address;
		this.locality = locality;
		this.profile = profile;
	}

	public boolean validRegistration() {
		if (! mobilePhone.matches("^(+351 )([9])([6]|[3]|[1])([0-9]{7})$")) 
			return false;
		if (! homePhone.matches("^(+351 )([0-9]{9})$"))
			return false;
		if (!profile.equals("Privado") && !profile.equals("Publico"))
			return false;
		return true;
	}
}
