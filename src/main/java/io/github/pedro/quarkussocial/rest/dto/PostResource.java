package io.github.pedro.quarkussocial.rest.dto;

import io.github.pedro.quarkussocial.domain.model.User;
import io.github.pedro.quarkussocial.domain.repository.UserRepository;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    private UserRepository userRepository;

    public PostResource(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @POST
    public Response savePost(@PathParam("userId") Long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    public Response getPosts(@PathParam("userId") Long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok().build();
    }

}
