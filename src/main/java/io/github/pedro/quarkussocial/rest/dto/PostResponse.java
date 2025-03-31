package io.github.pedro.quarkussocial.rest.dto;

import java.time.LocalDateTime;

import io.github.pedro.quarkussocial.domain.model.Post;
import lombok.Data;

@Data
public class PostResponse {

    private String body;
    private LocalDateTime createdAt;

    public static PostResponse fromEntity(Post post) {
        PostResponse response = new PostResponse();
        response.setBody(post.getBody());
        response.setCreatedAt(post.getCreatedAt());
        return response;
    }

}
