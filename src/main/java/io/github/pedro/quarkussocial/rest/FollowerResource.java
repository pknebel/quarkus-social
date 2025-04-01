package io.github.pedro.quarkussocial.rest;

import java.util.stream.Collectors;

import io.github.pedro.quarkussocial.domain.model.Follower;
import io.github.pedro.quarkussocial.domain.repository.FollowerRepository;
import io.github.pedro.quarkussocial.domain.repository.UserRepository;
import io.github.pedro.quarkussocial.rest.dto.FollowerRequest;
import io.github.pedro.quarkussocial.rest.dto.FollowerResponse;
import io.github.pedro.quarkussocial.rest.dto.FollowersPerUserResponse;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {

    private final FollowerRepository followerRepository;
    private final UserRepository userRepository;

    @Inject
    public FollowerResource(FollowerRepository followerRepository, UserRepository userRepository) {
        this.followerRepository = followerRepository;
        this.userRepository = userRepository;

    }

    @PUT
    @Transactional
    public Response followUser(
        @PathParam("userId") Long userId, FollowerRequest followerRequest) {

            if(userId.equals(followerRequest.getFollowerId())){
                return Response.status(Response.Status.CONFLICT).entity("Você não pode seguir a si mesmo").build();
            }

        var user = userRepository.findByIdOptional(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var follower = userRepository.findById(followerRequest.getFollowerId());

        boolean follows = followerRepository.isFollowing(user.get(), follower);

        if(!follows){
            var newFollower = new Follower();
            newFollower.setUser(user.get());
            newFollower.setFollower(follower);
    
            followerRepository.persist(newFollower);
            
        }

        return Response.status(Response.Status.NO_CONTENT).build();

    }

    @GET
    public Response listFollowers(@PathParam("userId") Long userId){

        var user = userRepository.findByIdOptional(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var list = followerRepository.findByUserId(userId);
        FollowersPerUserResponse response = new FollowersPerUserResponse();
        response.setFollowersCount(list.size());

        var followersList = list.stream().map(FollowerResponse::new).collect(Collectors.toList());

        response.setContent(followersList);
        return Response.ok(response).build();

    }

    @DELETE
    @Transactional
    public Response unfollowUser(@PathParam("userId") Long userId, @QueryParam("followerId") Long followerId){
        var user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        followerRepository.deleteByFollowerAndUser(userId, followerId);

        return Response.status(Response.Status.NO_CONTENT).build();
    }

}
