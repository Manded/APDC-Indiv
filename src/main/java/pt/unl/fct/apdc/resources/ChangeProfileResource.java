package pt.unl.fct.apdc.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
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
import com.google.cloud.datastore.Transaction;

import pt.unl.fct.apdc.utils.ChangeAttrData;
import pt.unl.fct.apdc.utils.ChangeStateData;
import pt.unl.fct.apdc.utils.RoleChangeData;

@Path("/changeProfile")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ChangeProfileResource {

	/**
	 * A Logger Object
	 */
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public ChangeProfileResource() {
	}

	@PUT
	@Path("/attr")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response changeProfileAttribute(ChangeAttrData data) {
		LOG.fine("Attribute change attempt by user: " + data.username);
		if (!data.validRegistration()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
		Key tokenKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", data.username)).setKind("Token")
				.newKey(data.tokenID);
		Transaction txn = datastore.newTransaction();
		try {
			Entity token = txn.get(tokenKey);
			if (token == null) {
				txn.rollback();
				return Response.status(Status.CONFLICT).entity("Please login.").build();
			} else {
				Entity user = txn.get(userKey);
				if (user == null || (Long.parseLong(token.getString("expirationData")) < System.currentTimeMillis()) ) {
					txn.rollback();
					return Response.status(Status.UNAUTHORIZED).entity("User doesnt exist.").build();
				} else {
					user = Entity.newBuilder(userKey).set("mobilePhone", data.mobilePhone)
							.set("homePhone", data.homePhone).set("addr1", data.address).set("addr2", data.addressComp)
							.set("loc", data.locality).set("profile", data.profile).set("role", "USER")
							.set("state", "ENABLED").build();
					txn.update(user);
					txn.commit();
					return Response.status(Status.ACCEPTED).entity("Account updated succesfully.").build();
				}
			}
		} catch (Exception e) {
			if (txn.isActive())
				txn.rollback();
			return Response.status(Status.CONFLICT).build();
		} finally {
			if (txn.isActive())
				txn.rollback();
		}
	}

	@PUT
	@Path("/role")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response changeProfileRole(RoleChangeData data) {
		LOG.fine("Role change attempt by user: " + data.username);
		Key targetUserKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
		Key tokenKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", data.username)).setKind("Token")
				.newKey(data.tokenID);
		short aux = 0;
		if (data.newRole.equals("GA"))
			aux = 2;
		if (data.newRole.equals("GBO"))
			aux = 1;
		if (aux == 0)
			return Response.status(Status.BAD_REQUEST).entity("Inserted an invalid role.").build();
		Transaction txn = datastore.newTransaction();
		try {
			Entity token = txn.get(tokenKey);
			if (token == null) {
				txn.rollback();
				return Response.status(Status.CONFLICT).entity("Please login.").build();
			} else {
				Entity tUser = txn.get(targetUserKey);
				if (tUser == null || (Long.parseLong(token.getString("expirationData")) < System.currentTimeMillis())) {
					txn.rollback();
					return Response.status(Status.UNAUTHORIZED).entity("User doesnt exist.").build();
				} else {
					String userRole = token.getString("role");
					if (tUser.getString("role").equals("USER")
							&& (userRole.equals("SU") || (userRole.equals("GA") && aux < 2))) {
						tUser = Entity.newBuilder(tUser).set("role", data.newRole).build();
						txn.update(tUser);
						txn.commit();
						return Response.status(Status.ACCEPTED).entity("Account updated succesfully.").build();
					}
					else {
						return Response.status(Status.FORBIDDEN).entity("Insufficient permissions.").build();
					}
				}
			}
		} catch (Exception e) {
			if (txn.isActive())
				txn.rollback();
			return Response.status(Status.CONFLICT).build();
		} finally {
			if (txn.isActive())
				txn.rollback();
		}
	}

	@PUT
	@Path("/state")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response changeProfileState(ChangeStateData data) {
		LOG.fine("State change attempt by user: " + data.username);
		Key targetUserKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
		Key tokenKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", data.username)).setKind("Token")
				.newKey(data.tokenID);
		if (!data.newState.equals("ENABLED") && !data.newState.equals("DISABLE"))
			return Response.status(Status.BAD_REQUEST).entity("Invalid state.").build();
		Transaction txn = datastore.newTransaction();
		try {
			Entity token = txn.get(tokenKey);
			if (token == null) {
				txn.rollback();
				return Response.status(Status.CONFLICT).entity("Please login.").build();
			} else {
				Entity tUser = txn.get(targetUserKey);
				if (tUser == null || (Long.parseLong(token.getString("expirationData")) < System.currentTimeMillis())) {
					txn.rollback();
					return Response.status(Status.UNAUTHORIZED).entity("User doesnt exist.").build();
				} else {
					short opValue = assignValue(token.getString("role"));
					short targetValue = assignValue(tUser.getString("role"));
					if (opValue>targetValue) {
						tUser = Entity.newBuilder(tUser).set("state", data.newState).build();
						txn.update(tUser);
						txn.commit();
						return Response.status(Status.ACCEPTED).entity("User state updated succesfully.").build();
					}else {
						return Response.status(Status.FORBIDDEN).entity("Target user does not have less permissions than you.").build();
					}
				}
			}
		} catch (Exception e) {
			if (txn.isActive())
				txn.rollback();
			return Response.status(Status.CONFLICT).build();
		} finally {
			if (txn.isActive())
				txn.rollback();
		}
	}
	
	private short assignValue(String role) {
		if (role.equals("USER"))
			return 0;
		if(role.equals("GBO"))
			return 1;
		if(role.equals("GA"))
			return 2;
		if(role.equals("SU"))
			return 3;
		return -1;
	}
}
