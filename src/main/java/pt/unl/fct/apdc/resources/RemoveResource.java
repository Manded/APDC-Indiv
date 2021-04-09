package pt.unl.fct.apdc.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import pt.unl.fct.apdc.utils.*;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.datastore.Transaction;

@Path("/remove")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RemoveResource {

	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	/**
	 * A Logger Object
	 */
	public RemoveResource() {
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(RemoveAccountData data) {
		LOG.fine("Delete attempt by token: " + data.tokenID);
		// It is probably more sensible to insert the target username in the
		// URL instead of a JSON but this way i can be more consistent in using only
		// JSON files
		Key tUserKey = datastore.newKeyFactory().setKind("User").newKey(data.targetUsername);
		Key tokenKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", data.username))
			.setKind("Token").newKey(data.tokenID);
		Query<Key> query = Query.newKeyQueryBuilder()
				.setKind("AuthToken")
				.setFilter(PropertyFilter.hasAncestor(tUserKey))
				.build();
		Transaction txn = datastore.newTransaction();
		try {
			Entity targetUser = txn.get(tUserKey);
			Entity opUser = txn.get(tokenKey);
			if (targetUser != null) {
				if (opUser != null) {
				if (Long.parseLong(opUser.getString("expirationData")) < System.currentTimeMillis()) {
				String opUserRole = opUser.getString("role");
				if (opUserRole.equals("GBO") || opUserRole.equals("GA") ||
					opUser.getString("username").equals(data.targetUsername)) {
					txn.delete(tUserKey);
					QueryResults<Key> tUserTokens = txn.run(query);
					while(tUserTokens.hasNext()) {
						txn.delete(tUserTokens.next());
					}
					txn.commit();
					return Response.ok().build();
				}else {
					return Response.status(Status.UNAUTHORIZED).entity("Session timed out, please logout.").build();
				}
				} else {
					return Response.status(Status.BAD_REQUEST).entity("Cant delete another user.").build();
				}
					
				}else {
					return Response.status(Status.UNAUTHORIZED).entity("Please login").build();
				}
			} else {
				return Response.status(Status.BAD_REQUEST).entity("Target User does not exist.").build();
			}
		} finally {
			if (txn.isActive())
				txn.rollback();
		}
	}

}
