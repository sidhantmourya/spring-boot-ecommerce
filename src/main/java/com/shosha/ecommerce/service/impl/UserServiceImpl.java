package com.shosha.ecommerce.service.impl;

import com.shosha.ecommerce.dao.UserRepository;
import com.shosha.ecommerce.dto.UserDTO;
import com.shosha.ecommerce.entity.User;
import com.shosha.ecommerce.entity.enums.Role;
import com.shosha.ecommerce.service.UserService;
import com.shosha.ecommerce.service.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDTO save(UserDTO userDTO) {
        log.debug("Request to save User : {}", userDTO);
        User user = userMapper.toEntity(userDTO);
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public Optional<UserDTO> findOne(String email) {
        log.debug("Request to get User by email : {}", email);
        return userRepository.findByEmail(email).map(userMapper::toDto);
    }

    @Override
    public boolean existsByEmail(String email) {
        log.debug("existsByEmail: {}", email);
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    user.getAuthorities()
            );
        };
    }

    @Override
    public Optional<UserDTO> getByRole(Role role) {

        return userRepository.getUserByRole(role).map(userMapper::toDto);
    }

    @Override
    public Optional<UserDTO> getByEmail(String email) {
        return userRepository.getUserByEmail(email).map(userMapper::toDto);
    }

    @Override
    public List<UserDTO> getAllCustomers() {
        return userRepository.findAllByRole(Role.CUSTOMER).stream().map(userMapper::toDto).toList();
    }
}
