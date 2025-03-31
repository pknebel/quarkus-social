package io.github.pedro.quarkussocial.rest;

import java.util.List;
import java.util.Set;

import io.github.pedro.quarkussocial.domain.model.User;
import io.github.pedro.quarkussocial.domain.repository.UserRepository;
import io.github.pedro.quarkussocial.rest.dto.CreateUserRequest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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
            StringBuilder errorMessage = new StringBuilder("Erro de validação: ");
            for (ConstraintViolation<CreateUserRequest> violation : violations) {
                errorMessage.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("; ");
            }
            return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage.toString()).build();
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
