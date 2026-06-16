package com.unir.papertales.catalogue.service;

import com.unir.papertales.catalogue.controller.model.GetBooksResponseDto;
import com.unir.papertales.catalogue.repository.BookRepository;
import com.unir.papertales.catalogue.repository.model.Book;
import com.unir.papertales.catalogue.utils.BookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetBooksWithPredicateService {

    private final BookRepository repository;
    private final BookMapper mapper;

    @Transactional(readOnly = true)
    public GetBooksResponseDto getBooks(
            String title,
            String author,
            String code,
            LocalDate publicationDate,
            String category,
            Double price,
            Integer rating,
            Boolean visible,
            Integer stock,
            String image,
            String description
    ) {

        List<Book> books;
        boolean hasFilters = StringUtils.hasText(title)
                || StringUtils.hasText(author)
                || StringUtils.hasText(code)
                || publicationDate != null
                || StringUtils.hasText(category)
                || price != null
                || rating != null
                || visible != null
                || stock != null
                || StringUtils.hasText(image)
                || StringUtils.hasText(description);

        if (hasFilters) {
            books = repository.getBooks(null, title, author, code, publicationDate, category, price, rating, visible, stock, image, description);
        } else {
            books = repository.getBooks();
        }
        return GetBooksResponseDto.builder()
                .books(mapper.asBookDtoList(books))
                .build();
    }
}
