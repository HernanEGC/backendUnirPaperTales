package com.unir.papertales.catalogue.service;

import com.unir.papertales.catalogue.controller.model.GetBooksResponseDto;
import com.unir.papertales.catalogue.controller.model.GetBookResponseDto;
import com.unir.papertales.catalogue.exception.BookNotFoundException;
import com.unir.papertales.catalogue.repository.BookJpaRepository;
import com.unir.papertales.catalogue.repository.model.Book;
import com.unir.papertales.catalogue.utils.BookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetBooksService {

    private final BookJpaRepository repository;
    private final BookMapper mapper;

    @Transactional(readOnly = true)
    public GetBooksResponseDto getBooks() {
        List<Book> books = repository.findAvailableBooks();
        return GetBooksResponseDto.builder()
                .books(mapper.asBookDtoList(books))
                .build();
    }

    @Transactional(readOnly = true)
    public GetBookResponseDto getBook(Long bookId) {
        Optional<Book> book = repository.findById(bookId);
        return book.map(
                s -> GetBookResponseDto.builder()
                        .id(s.getId())
                        .author(s.getAuthor())
                        .code(s.getCode())
                        .publicationDate(s.getPublicationDate())
                        .description(s.getDescription())
                        .category(s.getCategory())
                        .price(s.getPrice().doubleValue())
                        .stock(s.getStock())
                        .visible(s.getVisible())
                        .rating(s.getRating())
                        .imageUrl(s.getImageUrl())
                        .build()
        ).orElseThrow(
                () -> new BookNotFoundException("Book not found with id: " + bookId));
    }
}
