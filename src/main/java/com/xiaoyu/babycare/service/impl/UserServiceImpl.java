package com.xiaoyu.babycare.service.impl;

import com.xiaoyu.babycare.auth.UserRole;
import com.xiaoyu.babycare.common.BizException;
import com.xiaoyu.babycare.dto.user.LoginRequest;
import com.xiaoyu.babycare.dto.user.LoginResponse;
import com.xiaoyu.babycare.dto.user.RegisterRequest;
import com.xiaoyu.babycare.dto.user.UpdateProfileRequest;
import com.xiaoyu.babycare.entity.User;
import com.xiaoyu.babycare.mapper.UserMapper;
import com.xiaoyu.babycare.service.UserService;
import com.xiaoyu.babycare.util.JwtUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;
    // BCrypt 是推荐密码哈希算法，避免明文/可逆加密存储。
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserServiceImpl(UserMapper userMapper, JwtUtils jwtUtils) {
        this.userMapper = userMapper;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public void register(RegisterRequest request) {
        // 1) 注册前先做唯一性校验。
        if (userMapper.findByUsername(request.getUsername()) != null) {
            throw new BizException("注册失败: 用户名已存在");
        }
        if (userMapper.findByEmail(request.getEmail()) != null) {
            throw new BizException("注册失败: 邮箱已被使用");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        // 2) 密码哈希后入库，数据库不保存原始密码。
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber("");
        // 3) 插入数据库。
        userMapper.insert(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // 1) 按用户名查询用户。
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            throw new BizException("登录失败: 用户不存在");
        }
        // 2) 使用 BCrypt 校验明文密码与哈希是否匹配。
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BizException("登录失败: 密码错误");
        }
        // 3) 生成 JWT 返回给前端，后续请求通过它进行鉴权。
        String role = "admin".equalsIgnoreCase(user.getUsername()) ? UserRole.ADMIN : UserRole.USER;
        return new LoginResponse(jwtUtils.createToken(user.getId(), user.getUsername(), role));
    }

    @Override
    public User me(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        // 返回给前端前移除敏感字段。
        user.setPassword(null);
        return user;
    }

    @Override
    public void updateProfile(Long userId, UpdateProfileRequest request) {
        // 只更新当前登录用户资料，不允许跨用户修改。
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            // 邮箱更新时校验唯一性。
            User existByEmail = userMapper.findByEmail(request.getEmail());
            if (existByEmail != null && !existByEmail.getId().equals(userId)) {
                throw new BizException("更新失败: 邮箱已被其他用户占用");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        userMapper.updateProfile(user);
    }

    @Override
    public List<User> listAllUsers() {
        List<User> users = userMapper.listAll();
        for (User user : users) {
            user.setPassword(null);
        }
        return users;
    }
}
