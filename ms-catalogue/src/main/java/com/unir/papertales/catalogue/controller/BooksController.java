package com.unir.papertales.catalogue.controller;

import com.unir.papertales.catalogue.controller.model.WriteBookRequestDto;
import com.unir.papertales.catalogue.controller.model.GetBooksResponseDto;
import com.unir.papertales.catalogue.controller.model.GetBookResponseDto;
import com.unir.papertales.catalogue.service.CreateBooksService;
import com.unir.papertales.catalogue.service.DeleteBooksService;
import com.unir.papertales.catalogue.service.GetBooksService;
import com.unir.papertales.catalogue.service.ModifyBooksService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class BooksController {

    private final GetBooksService getBooksService;
    private final ModifyBooksService modifyBooksService;
    private final DeleteBooksService deleteBooksService;
    private final CreateBooksService createBooksService;

    @GetMapping("books")
    @PreAuthorize("hasAnyAuthority('ROLE_LECTOR', 'ROLE_ADMIN')")
    public ResponseEntity<GetBooksResponseDto> getBooks() {
        return ResponseEntity.ok(getBooksService.getBooks());
    }

    @GetMapping("books/{bookId}")
    @PreAuthorize("hasAnyAuthority('ROLE_LECTOR', 'ROLE_ADMIN')")
    public ResponseEntity<GetBookResponseDto> getSuppl(@PathVariable Long bookId) {
        return ResponseEntity.ok(getBooksService.getBook(bookId));
    }

    @PostMapping("books")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<GetBookResponseDto> createBook(@RequestBody WriteBookRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createBooksService.createBook(request));
    }

    @PutMapping("books/{bookId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<GetBookResponseDto> updateBook(
            @PathVariable Long bookId,
            @RequestBody WriteBookRequestDto request) {
        return ResponseEntity.ok(modifyBooksService.modifyBook(bookId.intValue(), request));
    }

    @PatchMapping("books/{bookId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<GetBookResponseDto> updateBook(
            @PathVariable Long bookId,
            @RequestBody String jsonPart) {
        return ResponseEntity.ok(modifyBooksService.modifyBook(bookId, jsonPart));
    }

    @DeleteMapping("books/{bookId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId) {
        deleteBooksService.deleteBook(bookId);
        return ResponseEntity.noContent().build();
    }

}
