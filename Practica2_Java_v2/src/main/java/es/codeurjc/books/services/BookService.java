package es.codeurjc.books.services;

import java.util.Collection;

import es.codeurjc.books.dtos.requests.BookRequestDto;
import es.codeurjc.books.dtos.responses.BookDetailsResponseDto;
import es.codeurjc.books.dtos.responses.BookResponseDto;
import es.codeurjc.books.models.Book;

public interface BookService {

    Collection<BookResponseDto> findAll();

    BookDetailsResponseDto save(BookRequestDto bookRequestDto);

    BookDetailsResponseDto findById(long bookId);

    BookDetailsResponseDto remove(long bookId);

    BookDetailsResponseDto modify(long bookId, Book book);
}
