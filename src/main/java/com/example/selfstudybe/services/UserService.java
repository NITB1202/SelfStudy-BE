package com.example.selfstudybe.services;

import com.example.selfstudybe.dtos.User.UserDto;
import com.example.selfstudybe.models.User;
import com.example.selfstudybe.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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

    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if(user == null) return null;
        return UserToDto(user);
    }
}
