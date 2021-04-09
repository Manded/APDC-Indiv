package pt.unl.fct.apdc.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

@Path("/info")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class InfoResource {


	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Gson g = new Gson();

	public InfoResource() {}
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getInfo() {
		LOG.fine("User attempted info operation");
		return Response.ok(g.toJson("Available Ops:"
				+ "/register"
				+ "/login"
				+ "/logout"
				+ "/changeProfile/attr"
				+ "/changeProfile/role"
				+ "/changeProfile/state"
				+ "/remove")).build();
	}
}
