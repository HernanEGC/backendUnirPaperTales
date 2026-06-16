package com.unir.papertales.catalogue.repository;

import com.unir.papertales.catalogue.repository.model.Book;
import com.unir.papertales.catalogue.repository.predicate.SearchFields;
import com.unir.papertales.catalogue.repository.predicate.SearchOperation;
import com.unir.papertales.catalogue.repository.predicate.SearchStatement;
import com.unir.papertales.catalogue.repository.predicate.SearchCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BookRepository {

    private final BookJpaRepository bookJpaRepository;

    public List<Book> getBooks(Long id, String title, String author, String code, LocalDate publicationDate, String category, Double price, Integer rating, Boolean visible, Integer stock, String image, String description ) {

        SearchCriteria<Book> spec = new SearchCriteria<>();

        if (id != null) {
            spec.add(new SearchStatement(SearchFields.ID, id, SearchOperation.EQUAL));
        }

        if (StringUtils.hasText(title)) {
            spec.add(new SearchStatement(SearchFields.TITLE, title, SearchOperation.MATCH));
        }

        if (StringUtils.hasText(author)) {
            spec.add(new SearchStatement(SearchFields.AUTHOR, author, SearchOperation.MATCH));
        }

        if (StringUtils.hasText(code)) {
            spec.add(new SearchStatement(SearchFields.CODE, code, SearchOperation.MATCH));
        }

        if (publicationDate != null) {
            spec.add(new SearchStatement(SearchFields.PUBLICATION_DATE, publicationDate, SearchOperation.EQUAL));
        }

        if (StringUtils.hasText(category)) {
            spec.add(new SearchStatement(SearchFields.CATEGORY, category, SearchOperation.MATCH));
        }

        if (rating != null && rating > 0) {
            spec.add(new SearchStatement(SearchFields.RATING, rating, SearchOperation.LESS_THAN_EQUAL));
        }

        if (price != null && price > 0) {
            spec.add(new SearchStatement(SearchFields.PRICE, price, SearchOperation.LESS_THAN_EQUAL));
        }

        if (visible != null) {
            spec.add(new SearchStatement(SearchFields.VISIBLE, visible, SearchOperation.EQUAL));
        }


        if (stock != null && stock > 0) {
            spec.add(new SearchStatement(SearchFields.STOCK, stock, SearchOperation.GREATER_THAN_EQUAL));
        }

        if (StringUtils.hasText(image)) {
            spec.add(new SearchStatement(SearchFields.IMAGE_URL, image, SearchOperation.MATCH));
        }

        if (StringUtils.hasText(description)) {
            spec.add(new SearchStatement(SearchFields.DESCRIPTION, description, SearchOperation.MATCH));
        }

        return bookJpaRepository.findAll(spec);
    }

    public List<Book> getBooks() {
        return bookJpaRepository.findAvailableBooks();
    }

    public List<Book> getBooks(Integer size, Integer page) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Page number must be non-negative and size must be positive.");
        }
        return bookJpaRepository.findAll(Pageable.ofSize(size).withPage(page)).getContent();
    }

    public List<Book> getBooks(
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
            Integer page) {

        SearchCriteria<Book> spec = new SearchCriteria<>();

        if (id != null) {
            spec.add(new SearchStatement(SearchFields.ID, id, SearchOperation.EQUAL));
        }

        if (StringUtils.hasText(title)) {
            spec.add(new SearchStatement(SearchFields.TITLE, title, SearchOperation.MATCH));
        }

        if (StringUtils.hasText(author)) {
            spec.add(new SearchStatement(SearchFields.AUTHOR, author, SearchOperation.MATCH));
        }

        if (StringUtils.hasText(code)) {
            spec.add(new SearchStatement(SearchFields.CODE, code, SearchOperation.MATCH));
        }

        if (publicationDate != null) {
            spec.add(new SearchStatement(SearchFields.PUBLICATION_DATE, publicationDate, SearchOperation.EQUAL));
        }

        if (StringUtils.hasText(category)) {
            spec.add(new SearchStatement(SearchFields.CATEGORY, category, SearchOperation.MATCH));
        }

        if (rating != null && rating > 0) {
            spec.add(new SearchStatement(SearchFields.RATING, rating, SearchOperation.LESS_THAN_EQUAL));
        }

        if (price != null && price > 0) {
            spec.add(new SearchStatement(SearchFields.PRICE, price, SearchOperation.LESS_THAN_EQUAL));
        }

        if (visible != null) {
            spec.add(new SearchStatement(SearchFields.VISIBLE, visible, SearchOperation.EQUAL));
        }


        if (stock != null && stock > 0) {
            spec.add(new SearchStatement(SearchFields.STOCK, stock, SearchOperation.GREATER_THAN_EQUAL));
        }

        if (StringUtils.hasText(image)) {
            spec.add(new SearchStatement(SearchFields.IMAGE_URL, image, SearchOperation.MATCH));
        }

        if (StringUtils.hasText(description)) {
            spec.add(new SearchStatement(SearchFields.DESCRIPTION, description, SearchOperation.MATCH));
        }

        return bookJpaRepository.findAll(spec, Pageable.ofSize(pageSize).withPage(page)).getContent();
    }
}
