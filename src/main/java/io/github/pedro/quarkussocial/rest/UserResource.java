package io.github.pedro.quarkussocial.rest;

import io.github.pedro.quarkussocial.domain.model.User;
import io.github.pedro.quarkussocial.domain.repository.UserRepository;
import io.github.pedro.quarkussocial.rest.dto.CreateUserRequest;
import io.github.pedro.quarkussocial.rest.dto.ResponseError;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Set;

import java.util.List;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserRepository repository;
    private final Validator validator;

    @Inject
    public UserResource(UserRepository repository, Validator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    @POST
    @Transactional
    public Response createUser(@Valid CreateUserRequest userRequest) {

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(userRequest);
        if (!violations.isEmpty()) {

            return ResponseError.createFromValidation(violations).withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
        }


        User user = new User();
        user.setName(userRequest.getName());
        user.setAge(userRequest.getAge());

        repository.persist(user);

        return  Response.status(Response.Status.CREATED.getStatusCode()).entity(user).build();

    }

    @DELETE
    @Transactional
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        User user = repository.findById(id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Usuário não localizado").build();
        }
        repository.delete(user);
        return Response.ok("Usuário deletado com sucesso").build();
    }

    @PUT
    @Transactional
    public  Response updateUser(User user) {
        if(user.getId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("O id deve ser informado para atualização").build();
        }

        User updatedUser = repository.findById(user.getId());
        if(updatedUser == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Usuário não localizado").build();
        }

        updatedUser.setName(user.getName());
        updatedUser.setAge(user.getAge());
        updatedUser.persist();
        return Response.ok(updatedUser).build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        User user = User.findById(id);
        if(user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Usuário não encontrado").build();
        }
        return Response.ok(user).build();
    }

    @GET
    public Response listAllUsers() {
        List<User> users = User.listAll();
        return Response.ok(users).build();
    }

}
