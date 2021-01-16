package es.codeurjc.books.services.impl;

import es.codeurjc.books.exceptions.UserNotFoundException;
import es.codeurjc.books.models.Role;
import es.codeurjc.books.models.User;
import es.codeurjc.books.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailsImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String nick) throws UsernameNotFoundException {
        User user = this.userRepository.findByNick(nick).orElseThrow(UserNotFoundException::new);
        return this.toUserDetails(user);
    }

    private UserDetails toUserDetails(User user) {
        Set<Role> roles = user.getRoles();
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));

        return new org.springframework.security.core.userdetails.User(user.getNick(), user.getPassword(), authorities);
    }
}
