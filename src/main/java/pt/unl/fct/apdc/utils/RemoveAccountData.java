package pt.unl.fct.apdc.utils;

public class RemoveAccountData {

	public String targetUsername;
	public String tokenID;
	public String username;
	
	public RemoveAccountData() {}
	public RemoveAccountData(String targetUsername, String tokenID, String username) {
		this.targetUsername = targetUsername;
		this.tokenID = tokenID;
		this.username = username;
	}
}
