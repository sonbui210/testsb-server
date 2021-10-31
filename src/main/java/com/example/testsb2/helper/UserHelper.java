package com.example.testsb2.helper;


import com.example.testsb2.api.auth.payload.RegisterRequest;
import com.example.testsb2.constant.Message;
import com.example.testsb2.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class UserHelper {

    private final UserService userService;

    public String checkRegisterUserRequest(RegisterRequest request) {
        if (userService.findByUsername(request.getUsername()) != null) {
            return Message.EXISTED_USERNAME;
        }

        if (!StringUtils.isEmpty(request.getEmail()) && userService.findByEmail(request.getEmail()) != null) {
            return Message.EXISTED_EMAIL;
        }

        if (!StringUtils.isEmpty(request.getPhone()) && userService.findByPhone(request.getPhone()) != null) {
            return Message.EXISTED_PHONE;
        }

        return "";
    }

}
