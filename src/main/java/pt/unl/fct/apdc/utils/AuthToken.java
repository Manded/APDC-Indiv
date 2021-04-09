package pt.unl.fct.apdc.utils;

public class AuthToken {
	public static final long EXPIRATION_TIME = 10006060 * 2; // 2h
	public String username;
	public String tokenID;
	public long creationData;
	public long expirationData;
	public String role;

	public AuthToken() {}
	
	public AuthToken(String username, String role, String tokenID) {
		this.username = username;
		this.tokenID = tokenID;
		this.creationData = System.currentTimeMillis();
		this.expirationData = this.creationData + AuthToken.EXPIRATION_TIME;
		this.role = role;
	}
}
