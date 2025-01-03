package ru.melowetty.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.melowetty.entity.User;
import ru.melowetty.model.UserRole;
import ru.melowetty.repository.RoleRepository;
import ru.melowetty.repository.UserRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getUserByUsername(username);
    }

    public User createUser(String username, String password) {
        var role = roleRepository.findById(UserRole.USER.getId()).get();
        var encodedPassword = encoder.encode(password);

        var user = new User();

        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.authorities = Set.of(role);

        return userRepository.save(user);
    }

    public User changePassword(String username, String password) {
        var user = getUserByUsername(username);

        var encodedPassword = encoder.encode(password);

        user.setPassword(encodedPassword);

        return userRepository.save(user);
    }

    public boolean usernameIsExist(String username) {
        return userRepository.existsByUsername(username);
    }

    public User getUserByUsername(String username) {
        return userRepository.findUserByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Пользователь с таким логином не найден")
        );
    }
}
