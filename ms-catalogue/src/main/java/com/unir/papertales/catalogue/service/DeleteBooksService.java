package com.unir.papertales.catalogue.service;

import com.unir.papertales.catalogue.exception.BookNotFoundException;
import com.unir.papertales.catalogue.repository.BookJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DeleteBooksService {

    private final BookJpaRepository bookJpaRepository;

    @Transactional
    public void deleteBook(Long bookId) {
        if (!bookJpaRepository.existsById(bookId)) {
            throw new BookNotFoundException("Book with ID " + bookId + " does not exist.");
        }
        bookJpaRepository.deleteById(bookId);
    }
}
