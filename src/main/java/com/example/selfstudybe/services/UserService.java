package com.example.selfstudybe.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
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
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final Cloudinary cloudinary;

    public User getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if(user == null)
            throw new CustomNotFoundException("Can't find user with email " + email);
        return user;
    }

    public User getUserByUserId(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new CustomNotFoundException("Can't find user with id " + userId));
    }

    @Transactional
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
        return new ModelMapper().map(user, UserDto.class);
    }

    public List<UserDto> searchUsers(String email, String username, Role role) {
        Specification<User> specification = Specification.where(null);

        if(email != null && !email.isEmpty()){
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("email"), email + "%"));
        }

        if (username != null && !username.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("username")),
                            username.toLowerCase() + "%"
                    )
            );
        }

        if(role != null){
            specification = specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("role"), role)));
        }

        List<User> users = userRepository.findAll(specification);

        return new ModelMapper().map(users, new TypeToken<List<UserDto>>() {}.getType());
    }

    @Transactional
    public UserDto updateUser(UpdateUserDto updateUser) {
        User user = userRepository.findById(updateUser.getId()).orElseThrow(()->new CustomNotFoundException("Can't find user with id " + updateUser.getId()));
        if(updateUser.getEmail()!= null && updateUser.getEmail().equals(user.getEmail()))
            throw new CustomBadRequestException("This email has been used");

        user.setEmail(Optional.ofNullable(updateUser.getEmail()).orElse(user.getEmail()));
        user.setUsername(Optional.ofNullable(updateUser.getUsername()).orElse(user.getUsername()));
        user.setRole(Optional.ofNullable(updateUser.getRole()).orElse(user.getRole()));
        if(updateUser.getPassword() != null)
            user.setPassword(new BCryptPasswordEncoder().encode(updateUser.getPassword()));

        userRepository.save(user);

        return new ModelMapper().map(user, UserDto.class);
    }

    public String uploadAvatar(UUID id, MultipartFile file) throws IOException {
        User user = userRepository.findById(id).orElseThrow(()->new CustomNotFoundException("Can't find user with id " + id));

        Map params = ObjectUtils.asMap(
            "resource_type", "auto",
                "public_id", id.toString(),
                "asset_folder", "Avatar",
                "overwrite", true
        );

        Map result = cloudinary.uploader().upload(file.getBytes(),params);
        String url = result.get("secure_url").toString();

        user.setAvatarLink(url);
        userRepository.save(user);

        return url;
    }
}
