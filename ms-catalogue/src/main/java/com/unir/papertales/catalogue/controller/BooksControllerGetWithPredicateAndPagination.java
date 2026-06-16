package com.unir.papertales.catalogue.controller;

import com.unir.papertales.catalogue.controller.model.GetBooksResponseDto;
import com.unir.papertales.catalogue.service.GetBooksWithPredicateAndPaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v3/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class BooksControllerGetWithPredicateAndPagination {

    private final GetBooksWithPredicateAndPaginationService getBooksService;

    @GetMapping("books")
    @PreAuthorize("hasAnyAuthority('ROLE_LECTOR', 'ROLE_ADMIN')")
    public ResponseEntity<GetBooksResponseDto> getBooks(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double price,
            @RequestParam(required = false) Integer rating,
            @RequestParam(name = "publication_date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate publicationDate,
            @RequestParam(required = false) Boolean visible,
            @RequestParam(required = false)  Integer stock,
            @RequestParam(required = false) String image,
            @RequestParam(required = false, defaultValue = "5") Integer pageSize,
            @RequestParam(required = false, defaultValue = "0")
            Integer page    ) {
        return ResponseEntity.ok(getBooksService.getBooks(
                id,
                title,
                author,
                code,
                publicationDate,
                category,
                price,
                rating,
                visible,
                stock,
                image,
                description,
                pageSize,
                page));
    }
}
