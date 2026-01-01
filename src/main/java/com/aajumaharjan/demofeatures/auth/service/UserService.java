package com.aajumaharjan.demofeatures.auth.service;

import com.aajumaharjan.demofeatures.auth.dto.RegisterDto;
import com.aajumaharjan.demofeatures.auth.model.Role;
import com.aajumaharjan.demofeatures.auth.model.UserEntity;
import com.aajumaharjan.demofeatures.auth.repository.RoleRepository;
import com.aajumaharjan.demofeatures.auth.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service("userService")
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return new org.springframework.security.core.userdetails.User(username, user.getPassword(), getAuthority(user));
    }

    private Set<SimpleGrantedAuthority> getAuthority(UserEntity userEntity) {
        return userEntity.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().replace(" ", "_").toUpperCase()))
                .collect(Collectors.toSet());
    }

    @Transactional
    public UserEntity register(RegisterDto registerDto) {
        var password = passwordEncoder.encode(registerDto.getPassword());

        UserEntity user = new UserEntity();
        user.setName(registerDto.getName());
        user.setEmail(registerDto.getEmail());
        user.setPassword(password);

        Set<Role> roles = new HashSet<>(roleRepository.findByNameIn(registerDto.getRoles()));
        user.setRoles(roles);

        var saved = userRepository.save(user);
        UserEntity refreshed = new UserEntity();
        refreshed.setId(saved.getId());
        refreshed.setEmail(saved.getEmail());
        refreshed.setName(saved.getName());
        refreshed.setRoles(saved.getRoles());
        return refreshed;
    }
}
