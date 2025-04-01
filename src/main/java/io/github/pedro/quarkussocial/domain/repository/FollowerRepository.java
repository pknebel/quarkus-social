package io.github.pedro.quarkussocial.domain.repository;

import java.util.List;
import java.util.Optional;

import io.github.pedro.quarkussocial.domain.model.Follower;
import io.github.pedro.quarkussocial.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FollowerRepository implements PanacheRepository<Follower>{

    public boolean isFollowing(User user, User follower) {
        
        var params = Parameters.with("follower", follower).and("user", user).map();

        PanacheQuery<Follower> query = find("follower = :follower and user = :user", params);

        Optional<Follower> result = query.firstResultOptional();
        
        return result.isPresent();
    }

    public List<Follower> findByUserId(Long userId) {

        PanacheQuery<Follower> query = find("user.id", userId);
        return query.list();
    }

    public void deleteByFollowerAndUser(Long userId, Long followerId) {
        var params = Parameters.with("userId", userId).and("followerId", followerId).map();

        delete("user.id = :userId and follower.id = :followerId", params);

    }

}
