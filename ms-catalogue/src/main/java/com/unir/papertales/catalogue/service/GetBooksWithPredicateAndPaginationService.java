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
public class GetBooksWithPredicateAndPaginationService {

    private final BookRepository repository;
    private final BookMapper mapper;

    @Transactional(readOnly = true)
    public GetBooksResponseDto getBooks(
            Long id,
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
            String description,
            Integer pageSize,
            Integer page
    ) {

        boolean hasFilters = id != null
                || StringUtils.hasText(title)
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

        List<Book> books;
        if (hasFilters) {
            books = repository.getBooks(id, title, author, code, publicationDate, category, price, rating, visible, stock, image, description, pageSize, page);
        } else {
            books = repository.getBooks(pageSize, page);
        }
        return GetBooksResponseDto.builder()
                .books(mapper.asBookDtoList(books))
                .build();
    }
}
