package pt.unl.fct.apdc.utils;

public class ChangeRoleData {
	
	public String username;
	public String tokenID;
	public String newRole;
	
	public ChangeRoleData() {
		
	}
	
	public ChangeRoleData(String username, String tokenID, String newRole) {
		this.username = username;
		this.tokenID = tokenID;
		this.newRole = newRole;
	}
}
