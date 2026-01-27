package com.dgliger.marketplace.auth.service;
import com.dgliger.marketplace.auth.dto.UserDto;
import com.dgliger.marketplace.auth.entity.User;
import com.dgliger.marketplace.auth.mapper.UserMapper;
import com.dgliger.marketplace.auth.repository.UserRepository;
import com.dgliger.marketplace.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return userMapper.toDto(user);
    }

    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return userMapper.toDto(user);
    }
}