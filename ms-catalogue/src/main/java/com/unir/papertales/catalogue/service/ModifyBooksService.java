package com.unir.papertales.catalogue.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.unir.papertales.catalogue.controller.model.GetBookResponseDto;
import com.unir.papertales.catalogue.controller.model.WriteBookRequestDto;
import com.unir.papertales.catalogue.exception.BookNotFoundException;
import com.unir.papertales.catalogue.repository.BookJpaRepository;
import com.unir.papertales.catalogue.repository.model.Book;
import com.unir.papertales.catalogue.utils.BookMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class ModifyBooksService {

    private final BookJpaRepository bookJpaRepository;
    private final BookMapper bookMapper;
    private final ObjectMapper objectMapper;

    @Transactional
    public GetBookResponseDto modifyBook(Integer bookId, WriteBookRequestDto bookDto) {
        Book modifiedBook = bookMapper.asBook(bookId, bookDto);
        modifiedBook.setId(bookId);
        Book updatedBook = bookJpaRepository.save(modifiedBook);
        return bookMapper.asGetBookResponseDto(updatedBook);
    }

    @Transactional
    public GetBookResponseDto modifyBook(Long bookId, String jsonPart) {
        //PATCH se implementa en este caso mediante Merge Patch: https://datatracker.ietf.org/doc/html/rfc7386
        GetBookResponseDto book = bookMapper
                .asGetBookResponseDto(
                        bookJpaRepository
                                .findById(bookId)
                                .orElseThrow(() -> new BookNotFoundException("Book with ID " + bookId + " not found.")));
        try {
            JsonNode patch = objectMapper.readTree(jsonPart);
            JsonNode actualBook = objectMapper.valueToTree(book);
            JsonMergePatch mergePatch = JsonMergePatch.fromJson(patch); // ✅ Desde el patch entrante
            // Apply the patch to the actual book
            JsonNode patchedBookNode = mergePatch.apply(actualBook); // ✅ Aplicando al book actual
            GetBookResponseDto patchedBook = objectMapper.treeToValue(patchedBookNode, GetBookResponseDto.class);
            patchedBook.setId(bookId);
            Book savedBook = bookJpaRepository.save(bookMapper.asBook(patchedBook));
            return bookMapper.asGetBookResponseDto(savedBook);
        } catch (JsonProcessingException | JsonPatchException e) {
            log.error("Error processing JSON patch for book ID {}: {}", bookId, e.getMessage(), e);
            throw new RuntimeException("Error processing JSON patch", e);
        }
    }
}
