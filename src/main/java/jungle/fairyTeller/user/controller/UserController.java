package jungle.fairyTeller.user.controller;

import jungle.fairyTeller.user.dto.ResponseDTO;
import jungle.fairyTeller.user.dto.UserDTO;
import jungle.fairyTeller.user.entity.UserEntity;
import jungle.fairyTeller.security.TokenProvider;
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
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenProvider tokenProvider;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/auth/signup")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        try {
            if(userDTO == null || userDTO.getPassword() == null) {
                throw new RuntimeException("Invalid Password Value.");
            }
            // 1. 저장할 user 만들기
            UserEntity user = UserEntity.builder()
                    .userId(userDTO.getUserid())
                    .nickname(userDTO.getNickname())
                    .password(passwordEncoder.encode(userDTO.getPassword()))
                    .build();
            // 2. 만든 user를 저장
            UserEntity registeredUser = userService.create(user);

            UserDTO responseUserDTO = UserDTO.builder()
                    .id(registeredUser.getId())
                    .userid(registeredUser.getUserId())
                    .nickname(registeredUser.getNickname())
                    .build();
            return ResponseEntity.ok().body(responseUserDTO);

        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @GetMapping("/auth/signup/check-userid")
    public boolean checkUserIdAvailability(@RequestParam(value = "userid") String userid) {
        return userService.isUserIdAvailable(userid);
    }

    @GetMapping("/auth/signup/check-nickname")
    public boolean checkNicknameAvailability(@RequestParam(value = "nickname") String nickname) {
        return userService.isNicknameAvailable(nickname);
    }

    @GetMapping("/mypage/me")
    public boolean isSocialLogin(@AuthenticationPrincipal String userId) {
        Optional<UserEntity> user = userService.getUserById(Integer.parseInt(userId));
        return user.isPresent() && user.get().getAuthorize() != null;
    }


    @PostMapping("/auth/signin")
    public ResponseEntity<?> authenticate(@RequestBody UserDTO userDTO) {
        UserEntity user = userService.getByCredentials(
                userDTO.getUserid(), userDTO.getPassword(), passwordEncoder
        );

        if(user != null) {
            final String token = tokenProvider.create(user);

            final UserDTO responseUserDTO = UserDTO.builder()
                    .userid(user.getUserId())
                    .id(user.getId())
                    .nickname(user.getNickname())
                    .token(token)
                    .build();
            return ResponseEntity.ok().body(responseUserDTO);
        } else {
            ResponseDTO responseDTO = ResponseDTO.builder().error("Login Failed").build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }


}
