package pt.unl.fct.apdc.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.Transaction;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.gson.Gson;

import pt.unl.fct.apdc.utils.AuthData;
import pt.unl.fct.apdc.utils.ChangeAttrData;
import pt.unl.fct.apdc.utils.ChangeStateData;
import pt.unl.fct.apdc.utils.RemoveAccountData;
import pt.unl.fct.apdc.utils.RoleChangeData;

@Path("/opt")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class OptionalResource {

	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Gson g = new Gson();
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	
	public OptionalResource() {
		
	}
	
	@POST
	@Path("/2c")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response GBO2C(RemoveAccountData data) {
		LOG.fine("2c change attempt by user: " + data.username);

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.targetUsername);
		Key tokenKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", data.username)).setKind("Token")
				.newKey(data.tokenID);
			Entity token = datastore.get(tokenKey);
			if (token == null) {
				return Response.status(Status.CONFLICT).entity("Please login.").build();
			} else {
				Entity user = datastore.get(userKey);
				if (user == null || (Long.parseLong(token.getString("expirationData")) < System.currentTimeMillis()) ) {
					return Response.status(Status.UNAUTHORIZED).entity("User doesnt exist.").build();
				} else {
					String opUserRole = user.getString("role");
					if (!opUserRole.equals("GA") && !opUserRole.equals("GBO")) {
						return Response.status(Status.UNAUTHORIZED).entity("Insufficient permissions.").build();
					}else {
						return Response.ok(g.toJson(user)).build();
					}
				}
			}
		
	}
	@POST
	@Path("/1a")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response USER1A(AuthData data) {
		LOG.fine("1a change attempt by user: " + data.username);
		//everything but hashed password is sent to the user
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
		Key tokenKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", data.username)).setKind("Token")
				.newKey(data.tokenID);
			Entity token = datastore.get(tokenKey);
			if (token == null) {
				return Response.status(Status.CONFLICT).entity("Please login.").build();
			} else {
				Entity user = datastore.get(userKey);
				if (user == null || (Long.parseLong(token.getString("expirationData")) < System.currentTimeMillis()) ) {
					return Response.status(Status.UNAUTHORIZED).entity("User doesnt exist.").build();
				} else {
					String opUserRole = user.getString("username");
					if (!opUserRole.equals(token.getString("username"))) {
						return Response.status(Status.UNAUTHORIZED).entity("Insufficient permissions.").build();
					}else {
						return Response.ok(g.toJson(user.getString("username") + user.getString("email")
						+ user.getString("email") + user.getString("mobilePhone") + user.getString("homePhone")
						+ user.getString("address")+ user.getString("addressComp") + user.getString("locality") +
						user.getString("profile"))).build();
					}
				}
			}
		
	}
}
