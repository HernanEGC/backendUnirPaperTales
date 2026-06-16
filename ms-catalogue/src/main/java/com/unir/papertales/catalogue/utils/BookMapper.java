package com.unir.papertales.catalogue.utils;

import com.unir.papertales.catalogue.controller.model.GetBookResponseDto;
import com.unir.papertales.catalogue.controller.model.BookDto;
import com.unir.papertales.catalogue.controller.model.WriteBookRequestDto;
import com.unir.papertales.catalogue.exception.BookNotFoundException;
import com.unir.papertales.catalogue.repository.BookJpaRepository;
import com.unir.papertales.catalogue.repository.model.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookMapper {

    private final BookJpaRepository bookJpaRepository;

    public List<BookDto> asBookDtoList(List<Book> books) {
        return books.stream()
                .map(book -> BookDto.builder()
                        .id(book.getId())
                        .title(book.getTitle())
                        .author(book.getAuthor())
                        .code(book.getCode())
                        .publicationDate(book.getPublicationDate())
                        .category(book.getCategory())
                        .price(book.getPrice().doubleValue())
                        .rating(book.getRating())
                        .visible(book.getVisible())
                        .stock(book.getStock())
                        .imageUrl(book.getImageUrl())
                        .description(book.getDescription())
                        .build())
                .toList();
    }

    public GetBookResponseDto asGetBookResponseDto(Book book) {
        return GetBookResponseDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .code(book.getCode())
                .publicationDate(book.getPublicationDate())
                .category(book.getCategory())
                .price(book.getPrice().doubleValue())
                .rating(book.getRating())
                .visible(book.getVisible())
                .stock(book.getStock())
                .imageUrl(book.getImageUrl())
                .description(book.getDescription())
                .build();
    }

    public Book asBook(Integer bookId, WriteBookRequestDto bookDto) {
        bookJpaRepository.findById(bookId.longValue()).orElseThrow(
                () -> new BookNotFoundException("Book with ID " + bookId + " not found.")
        );
        return Book.builder()
                .id(bookId)
                .title(bookDto.getTitle())
                .author(bookDto.getAuthor())
                .code(bookDto.getCode())
                .publicationDate(bookDto.getPublicationDate())
                .category(bookDto.getCategory())
                .price(bookDto.getPrice())
                .rating(bookDto.getRating())
                .visible(bookDto.getVisible())
                .stock(bookDto.getStock())
                .imageUrl(bookDto.getImageUrl())
                .description(bookDto.getDescription())
                .build();
    }

    public Book asBook(GetBookResponseDto getBookResponseDto) {
        bookJpaRepository.findById(getBookResponseDto.getId()).orElseThrow(
                () -> new BookNotFoundException("Book with ID " + getBookResponseDto.getId() + " not found.")
        );
        return Book.builder()
                .id(getBookResponseDto.getId())
                .title(getBookResponseDto.getTitle())
                .author(getBookResponseDto.getAuthor())
                .code(getBookResponseDto.getCode())
                .publicationDate(getBookResponseDto.getPublicationDate())
                .description(getBookResponseDto.getDescription())
                .category(getBookResponseDto.getCategory())
                .price(getBookResponseDto.getPrice() != null ? BigDecimal.valueOf(getBookResponseDto.getPrice()) : null)
                .rating(getBookResponseDto.getRating())
                .visible(getBookResponseDto.getVisible())
                .stock(getBookResponseDto.getStock())
                .imageUrl(getBookResponseDto.getImageUrl())
                .build();
    }

}

