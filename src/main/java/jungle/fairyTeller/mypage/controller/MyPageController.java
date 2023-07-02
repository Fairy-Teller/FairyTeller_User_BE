package jungle.fairyTeller.mypage.controller;

import jungle.fairyTeller.security.TokenProvider;
import jungle.fairyTeller.user.dto.ResponseDTO;
import jungle.fairyTeller.user.dto.UserDTO;
import jungle.fairyTeller.user.entity.UserEntity;
import jungle.fairyTeller.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/mypage")
public class MyPageController {

    @Autowired
    private UserService userService;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PutMapping("/update-user")
    public ResponseEntity<?> updateUser(@AuthenticationPrincipal String UserId, @RequestBody UserDTO userDTO) {
        try {
            if (userDTO == null || userDTO.getPassword() == null) {
                throw new RuntimeException("Invalid Password Value.");
            }

            // 1. 업데이트할 user 정보 가져오기
            Optional<UserEntity> userOptional = userService.getUserById(Integer.parseInt(UserId));
            if (!userOptional.isPresent()) {
                throw new RuntimeException("User not found.");
            }

            // 2. 변경할 필드 업데이트
            UserEntity user = userOptional.get();
            user.setNickname(userDTO.getNickname());
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

            // 3. 업데이트된 user 저장
            UserEntity updatedUser = userService.update(user);

            UserDTO responseUserDTO = UserDTO.builder()
                    .id(updatedUser.getId())
                    .userid(updatedUser.getUserId())
                    .nickname(updatedUser.getNickname())
                    .build();
            return ResponseEntity.ok().body(responseUserDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @GetMapping("/mypage/me")
    public boolean isSocialLogin(@AuthenticationPrincipal String userId) {
        Optional<UserEntity> user = userService.getUserById(Integer.parseInt(userId));
        return user.isPresent() && user.get().getAuthorize() != null;
    }

}
