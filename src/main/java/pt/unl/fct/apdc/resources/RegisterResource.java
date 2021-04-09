package pt.unl.fct.apdc.resources;

import pt.unl.fct.apdc.utils.*;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Transaction;



@Path("/register")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RegisterResource {

	/**
	 * A Logger Object
	 */
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public RegisterResource() {
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doRegister(RegisterData data) {
		LOG.fine("Login attempt by user: " + data.username);
		if ( ! data.validRegistration()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		if ( ! data.validPassword()) {
			return Response.status(Status.BAD_REQUEST).entity("Your passwords differed from each other.").build();
		}

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
		Transaction txn = datastore.newTransaction();
		try {
			Entity user = txn.get(userKey);
			if (user != null) {
				txn.rollback();
				return Response.status(Status.CONFLICT).entity("User already exists.").build();
			} else {
				user = Entity.newBuilder(userKey)
					.set("username", data.username)
					.set("email", data.email)
					.set("password", DigestUtils.sha512Hex(data.password))
					.set("mobilePhone", data.mobilePhone)
					.set("homePhone", data.homePhone)
					.set("addr1", data.address)
					.set("addr2", data.addressComp)
					.set("loc", data.locality)
					.set("profile", data.profile)
					.set("role", "USER")
					.set("state", "ENABLED")
					.build();
				txn.add(user);
				txn.commit();
				return Response.status(Status.ACCEPTED).entity("Account created succesfully.").build();
			}
				
		} catch(Exception e){
			if (txn.isActive())
				txn.rollback();
			return Response.status(Status.CONFLICT).build();
		}	finally {
			if (txn.isActive())
				txn.rollback();
		}
	}

}
