package es.codeurjc.books.services.impl;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import es.codeurjc.books.models.Role;
import org.dozer.Mapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import es.codeurjc.books.dtos.requests.UpdateUserEmailRequestDto;
import es.codeurjc.books.dtos.requests.UserRequestDto;
import es.codeurjc.books.dtos.responses.UserResponseDto;
import es.codeurjc.books.exceptions.UserCanNotBeDeletedException;
import es.codeurjc.books.exceptions.UserNotFoundException;
import es.codeurjc.books.exceptions.UserWithSameNickException;
import es.codeurjc.books.models.User;
import es.codeurjc.books.repositories.UserRepository;
import es.codeurjc.books.services.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final Mapper mapper;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public UserServiceImpl(Mapper mapper, UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public Collection<UserResponseDto> findAll() {
        return this.userRepository.findAll().stream()
                .map(user -> this.mapper.map(user, UserResponseDto.class))
                .collect(Collectors.toList());
    }

    public UserResponseDto save(UserRequestDto userRequestDto) {
        if (this.userRepository.existsByNick(userRequestDto.getNick())) {
            throw new UserWithSameNickException();
        }
        User user = this.mapper.map(userRequestDto, User.class);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user = this.userRepository.save(user);
        return this.mapper.map(user, UserResponseDto.class);
    }

    public UserResponseDto findById(long userId) {
        User user = this.userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        return this.mapper.map(user, UserResponseDto.class);
    }

    public UserResponseDto updateEmail(long userId, UpdateUserEmailRequestDto updateUserEmailRequestDto) {
        User user = this.userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        if (!user.getEmail().equalsIgnoreCase(updateUserEmailRequestDto.getEmail())) {
            user.setEmail(updateUserEmailRequestDto.getEmail());
            user = this.userRepository.save(user);
        }
        return this.mapper.map(user, UserResponseDto.class);
    }

    public UserResponseDto delete(long userId) {
        User user = this.userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        if (!isEmpty(user.getComments())) {
            throw new UserCanNotBeDeletedException();
        }
        this.userRepository.delete(user);
        return this.mapper.map(user, UserResponseDto.class);
    }

    private UserDetails toUserDetails(User user) {
        Set<Role> roles = user.getRoles();
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));

        return new org.springframework.security.core.userdetails.User(user.getNick(), user.getPassword(), authorities);
    }

    @Override
    public UserDetails loadUserByUsername(String nick) throws UsernameNotFoundException {
        User user = this.userRepository.findByNick(nick).orElseThrow(UserNotFoundException::new);
        return this.toUserDetails(user);
    }
}
