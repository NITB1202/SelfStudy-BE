package com.example.selfstudybe.services;

import com.example.selfstudybe.dtos.User.CreateUserDto;
import com.example.selfstudybe.dtos.User.UpdateUserDto;
import com.example.selfstudybe.dtos.User.UserDto;
import com.example.selfstudybe.enums.Role;
import com.example.selfstudybe.exception.CustomBadRequestException;
import com.example.selfstudybe.exception.CustomNotFoundException;
import com.example.selfstudybe.models.User;
import com.example.selfstudybe.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private UserDto UserToDto(User user) {
        return new UserDto(
                  user.getId(),
                  user.getEmail(),
                  user.getUsername(),
                  user.getAvatarLink(),
                  user.getRole()
        );
    }

    public User getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if(user == null)
            throw new CustomNotFoundException("Can't find user with email " + email);
        return user;
    }

    public User getUserByUserId(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new CustomNotFoundException("Can't find user with id " + userId));
    }

    public UserDto createUser(CreateUserDto createUserDto) {
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
        return UserToDto(user);
    }

    public List<UserDto> searchUsers(String email, String username, Role role) {
        Specification<User> specification = Specification.where(null);

        if(email != null && !email.isEmpty()){
            specification = specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("email"), email)));
        }

        if(username != null && !username.isEmpty()){
            specification = specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("username")), username.toLowerCase())));
        }

        if(role != null){
            specification = specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("role"), role)));
        }

        List<User> users = userRepository.findAll(specification);

        return users.stream().map(this::UserToDto).collect(Collectors.toList());
    }

    @Transactional
    public UserDto updateUser(UpdateUserDto updateUser) {
        User user = userRepository.findById(updateUser.getId()).orElseThrow(()->new CustomNotFoundException("Can't find user with id " + updateUser.getId()));
        if(updateUser.getEmail()!= null && updateUser.getEmail().equals(user.getEmail()))
            throw new CustomBadRequestException("This email has been used");

        user.setEmail(Optional.ofNullable(updateUser.getEmail()).orElse(user.getEmail()));
        user.setUsername(Optional.ofNullable(updateUser.getUsername()).orElse(user.getUsername()));
        user.setAvatarLink(Optional.ofNullable(updateUser.getAvatarLink()).orElse(user.getAvatarLink()));
        user.setRole(Optional.ofNullable(updateUser.getRole()).orElse(user.getRole()));
        if(updateUser.getPassword() != null)
            user.setPassword(new BCryptPasswordEncoder().encode(updateUser.getPassword()));

        userRepository.save(user);

        return UserToDto(user);

    }
}
