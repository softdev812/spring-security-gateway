package com.ptc.gateway.feign;

import com.ptc.common.auth.UserDetail;
import com.ptc.common.utils.R;
import com.ptc.gateway.security.entity.UserEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "ptc-user")
public interface UserFeignClient {

    @GetMapping("/system/user/findByUsername")
    UserEntity findByUsername(@RequestParam("username") String username);

    @GetMapping("/system/user/authorities")
    R authorities(@RequestParam Long userId);

    @GetMapping("/system/user/getByToken")
    UserDetail getByToken(@RequestParam("token") String token);

    @GetMapping("/system/captcha/verify")
    Boolean verify(@RequestBody Map<String, String> body);

    @PostMapping("/system/user/refreshToken")
    void refreshToken(@RequestParam("token") String token);
}
