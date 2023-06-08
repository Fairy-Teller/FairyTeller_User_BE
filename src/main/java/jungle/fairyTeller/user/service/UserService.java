package jungle.fairyTeller.user.service;

import jungle.fairyTeller.user.entity.UserEntity;
import jungle.fairyTeller.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserEntity create(final UserEntity userEntity) {
        if (userEntity == null || userEntity.getUserId() == null) {
            throw new RuntimeException("Invalid arguments");
        }
        final String userId = userEntity.getUserId();
        if (userRepository.existsByUserId(userId)) {
            log.warn("Userid already exists {}", userId);
            throw new RuntimeException("Userid already exists");
        }
        final String nickname = userEntity.getNickname();
        if (userRepository.existsByNickname(nickname)) {
            log.warn("Nickname already exists {}", nickname);
            throw new RuntimeException("Nickname already exists");
        }
        return userRepository.save(userEntity);
    }

    public UserEntity getByCredentials(final String userId, final String password, final PasswordEncoder encoder) {

        final UserEntity originalUser = userRepository.findByUserId(userId);
        if (originalUser != null && encoder.matches(password, originalUser.getPassword())) {
            return originalUser;
        }
        return null;
    }

    public boolean isUserIdAvailable(String userId) {
        return userRepository.findByUserId(userId) == null;
    }
}
