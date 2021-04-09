package pt.unl.fct.apdc.utils;

public class ChangeStateData {
	
	public String username;
	public String tokenID;
	public String targetUsername;
	public String newState;
	
	public ChangeStateData() {}
	
	public ChangeStateData(String username, String tokenID, String targetUsername, String newState) {
		this.username = username;
		this.tokenID = tokenID;
		this.targetUsername = targetUsername;
		this.newState = newState;
	}
}
