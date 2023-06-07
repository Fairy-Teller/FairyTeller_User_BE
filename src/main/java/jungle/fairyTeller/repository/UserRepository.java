package jungle.fairyTeller.repository;

import jungle.fairyTeller.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    UserEntity findByUserId(String userId);
    Boolean existsByUserId(String userId);
    Boolean existsByNickname(String nickname);
    UserEntity findByUserIdAndPassword(String userId, String password);
}
