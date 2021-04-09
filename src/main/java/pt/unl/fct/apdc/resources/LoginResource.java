package pt.unl.fct.apdc.resources;

import java.util.UUID;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import pt.unl.fct.apdc.utils.*;

import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.Transaction;
import com.google.gson.Gson;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LoginResource {

	/**
	 * A Logger Object
	 */
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Gson g = new Gson();
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public LoginResource() {
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doLogin(LoginData data) {
		LOG.fine("Login attempt by user: " + data.username);

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
		String tokenID = UUID.randomUUID().toString();
		Key tokenKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", data.username))
				.setKind("Token").newKey(tokenID);
		Transaction txn = datastore.newTransaction();
		try {
			Entity user = txn.get(userKey);
			if (user != null) {
				String hashedPWD = user.getString("password");
				if (hashedPWD.equals(DigestUtils.sha512Hex(data.password))) {
					AuthToken at = new AuthToken(data.username, user.getString("role"), tokenID);
					Entity token = Entity.newBuilder(tokenKey).set("tokenID", tokenID)
							.set("role", user.getString("role")).set("creationData", System.currentTimeMillis())
							.set("expirationData", System.currentTimeMillis() + 1000 * 60 * 30)
							.set("username", data.username).build();
					txn.add(token);
					txn.commit();
					LOG.info("User " + data.username + " logged in succesfully.");
					return Response.ok(g.toJson(at.tokenID)).build();
				} else {
					LOG.warning("Wrong password for username: " + data.username);
					return Response.status(Status.FORBIDDEN).entity("Incorrect username or password.").build();
				}
			} else {
				return Response.status(Status.NOT_FOUND).build();
			}
		} finally {
			if (txn.isActive())
				txn.rollback();
		}
	}

}
