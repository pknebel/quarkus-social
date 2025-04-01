package io.github.pedro.quarkussocial.rest;

import java.util.stream.Collectors;

import io.github.pedro.quarkussocial.domain.model.Post;
import io.github.pedro.quarkussocial.domain.model.User;
import io.github.pedro.quarkussocial.domain.repository.FollowerRepository;
import io.github.pedro.quarkussocial.domain.repository.PostRepository;
import io.github.pedro.quarkussocial.domain.repository.UserRepository;
import io.github.pedro.quarkussocial.rest.dto.CreatePostRequest;
import io.github.pedro.quarkussocial.rest.dto.PostResponse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import io.quarkus.panache.common.Sort.Direction;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
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

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final FollowerRepository followerRepository;

    @Inject
    public PostResource(UserRepository userRepository, PostRepository postRepository, FollowerRepository followerRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.followerRepository = followerRepository;
    }

    @POST
    @Transactional
    public Response savePost(@PathParam("userId") Long userId, CreatePostRequest request) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Post post = new Post();
        post.setBody(request.getBody());
        post.setUser(user);

        postRepository.persist(post);

        return Response.status(Response.Status.CREATED).entity(post).build();
    }

    @GET
    public Response listPosts(@PathParam("userId") Long userId, @HeaderParam("followerId") Long followerId) {
        User user = userRepository.findById(userId);
        
        //caso o usuário não exista
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Usuário não encontrado").build();
        }

        //caso não tenha sido informado o header
        if(followerId == null){
            return Response.status(Response.Status.BAD_REQUEST).entity("Você deve informar o id do seguidor").build();
        }

        User follower = userRepository.findById(followerId);

        //caso o id do seguidor não exista
        if(follower == null){
            return Response.status(Response.Status.NOT_FOUND).entity("Seguidor não encontrado").build();
        }

        boolean follows = followerRepository.isFollowing(user, follower);
        
        //caso o seguidor não siga o usuário
        if(!follows){
            return Response.status(Response.Status.FORBIDDEN).entity("Você não pode ver os posts de quem não segue").build();
        }

        PanacheQuery<Post> query = postRepository.find("user", Sort.by("createdAt", Direction.Descending), user);

        //caso não tenha posts
        if (query.count() == 0) {
            return Response.status(Response.Status.NO_CONTENT).entity("Este usuário ainda não tem post publicado").build();
        }
        
        var list = query.list();

        var postResponseList = list.stream().map(post -> PostResponse.fromEntity(post)).collect(Collectors.toList());

        return Response.ok(postResponseList).build();

    }

}
