package es.codeurjc.books.services;

import java.util.Collection;

import es.codeurjc.books.dtos.requests.UpdateUserEmailRequestDto;
import es.codeurjc.books.dtos.requests.UserRequestDto;
import es.codeurjc.books.dtos.responses.UserResponseDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    Collection<UserResponseDto> findAll();

    UserResponseDto save(UserRequestDto userRequestDto);

    UserResponseDto findById(long userId);

    UserResponseDto updateEmail(long userId, UpdateUserEmailRequestDto updateUserEmailRequestDto);

    UserResponseDto delete(long userId);

}
