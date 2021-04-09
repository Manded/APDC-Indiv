package pt.unl.fct.apdc.resources;

import pt.unl.fct.apdc.utils.AuthData;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
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
import com.google.cloud.datastore.Transaction;

@Path("/logout")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LogoutResource {

	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public LogoutResource() {
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doLogout(AuthData data) {
		LOG.fine("Logout attempt by user: " + data.username);
		Key tokenKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", data.username))
			.setKind("Token").newKey(data.tokenID);
	
		Transaction txn = datastore.newTransaction();
		try {
			Entity token = txn.get(tokenKey);
			if (token != null) {
			if (Long.parseLong(token.getString("expirationData")) < System.currentTimeMillis()) {
				txn.delete(tokenKey);
				txn.commit();
				return Response.ok().build();
			}else {
				return Response.status(Status.UNAUTHORIZED).entity("Session timed out, please logout.").build();
			}
			}else {
				return Response.status(Status.UNAUTHORIZED).entity("Please login.").build();
			}
		}catch (Exception e) {
			return Response.status(Status.BAD_GATEWAY).build();
		}finally {
			if (txn.isActive())
				txn.rollback();
		}		
	}
}
