package com.xiaoyu.babycare.controller;

import com.xiaoyu.babycare.auth.RequireLogin;
import com.xiaoyu.babycare.auth.UserContext;
import com.xiaoyu.babycare.common.ApiResponse;
import com.xiaoyu.babycare.dto.user.LoginRequest;
import com.xiaoyu.babycare.dto.user.LoginResponse;
import com.xiaoyu.babycare.dto.user.RegisterRequest;
import com.xiaoyu.babycare.dto.user.UpdateProfileRequest;
import com.xiaoyu.babycare.entity.User;
import com.xiaoyu.babycare.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    // 通过构造器注入 Service，便于测试和保证依赖不可变。
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ApiResponse<Void> register(@Valid @RequestBody RegisterRequest request) {
        // 1) 参数校验通过后，调用注册业务。
        userService.register(request);
        // 2) 返回统一响应结构。
        return ApiResponse.success("注册成功", null);
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        // 登录成功后返回 JWT，前端需保存并放到 Authorization 请求头。
        return ApiResponse.success("登录成功", userService.login(request));
    }

    @RequireLogin
    @GetMapping("/me")
    public ApiResponse<User> me() {
        // UserContext.userId() 由鉴权拦截器在请求进入时写入。
        return ApiResponse.success(userService.me(UserContext.userId()));
    }

    @RequireLogin
    @PutMapping("/me")
    public ApiResponse<Void> updateMe(@Valid @RequestBody UpdateProfileRequest request) {
        // 仅允许当前登录用户更新自己的资料。
        userService.updateProfile(UserContext.userId(), request);
        return ApiResponse.success("更新成功", null);
    }

    @RequireLogin(adminOnly = true)
    @GetMapping
    public ApiResponse<List<User>> listAllUsers() {
        // 仅老板可查看所有已注册用户。
        return ApiResponse.success(userService.listAllUsers());
    }
}
