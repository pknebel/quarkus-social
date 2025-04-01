package io.github.pedro.quarkussocial.rest.dto;

import io.github.pedro.quarkussocial.domain.model.Follower;
import lombok.Data;

@Data
public class FollowerResponse {
    private Long id;
    private String name;
    private Long followerId;

    public FollowerResponse(){
    }

    public FollowerResponse(Follower follower){
        this(follower.getId(), follower.getFollower().getName(), follower.getFollower().getId());
    }

    public FollowerResponse(Long id, String name, Long followerId) {
        this.id = id;
        this.name = name;
        this.followerId = followerId;
    }

}
