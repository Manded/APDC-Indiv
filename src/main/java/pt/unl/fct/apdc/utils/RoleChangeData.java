package pt.unl.fct.apdc.utils;

public class RoleChangeData {
	public String username;
	public String tokenID;
	public String targetUsername;
	public String newRole;
	
	public RoleChangeData() {
	}
	
	public RoleChangeData(String username, String tokenID, String targetUsername, String newRole) {
		this.username = username;
		this.tokenID = tokenID;
		this.targetUsername = targetUsername;
		this.newRole = newRole;
	}
}
