package jungle.fairyTeller.repository;

import jungle.fairyTeller.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    UserEntity findByUserid(String userid);
    Boolean existsByUserid(String userid);
    Boolean existsByNickname(String nickname);

    UserEntity findByUseridAndPassword(String userid, String password);
}
