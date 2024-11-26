package com.example.selfstudybe.services;

import com.example.selfstudybe.dtos.User.CreateUserDto;
import com.example.selfstudybe.dtos.User.UserDto;
import com.example.selfstudybe.enums.Role;
import com.example.selfstudybe.exception.CustomBadRequestException;
import com.example.selfstudybe.exception.CustomNotFoundException;
import com.example.selfstudybe.models.User;
import com.example.selfstudybe.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private UserDto UserToDto(User user) {
        return new UserDto(
                  user.getEmail(),
                  user.getUsername(),
                  user.getAvatarLink(),
                  user.getRole()
        );
    }

    public User getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if(user == null)
            throw new CustomNotFoundException("Cannot find user with email " + email);
        return user;
    }

    public User getUserByUserId(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new CustomNotFoundException("Cannot find user with id " + userId));
    }

    public User createUser(CreateUserDto createUserDto) {
        // Check duplicate
        User checkUser = userRepository.findByEmail(createUserDto.getEmail());
        if(checkUser != null)
            throw new CustomBadRequestException("User with email " + createUserDto.getEmail() + " already exists");

        // Hash password
        String hashPassword = new BCryptPasswordEncoder().encode(createUserDto.getPassword());

        // Create new user
        User user = new User();

        user.setEmail(createUserDto.getEmail());
        user.setUsername(createUserDto.getUsername());
        user.setPassword(hashPassword);
        user.setRole(Role.ADMIN);

        userRepository.save(user);
        return user;
    }
}
