package com.unir.papertales.catalogue.service;

import com.unir.papertales.catalogue.controller.model.WriteBookRequestDto;
import com.unir.papertales.catalogue.controller.model.GetBookResponseDto;
import com.unir.papertales.catalogue.repository.BookJpaRepository;
import com.unir.papertales.catalogue.repository.model.Book;
import com.unir.papertales.catalogue.utils.BookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CreateBooksService {

    private final BookJpaRepository bookJpaRepository;
    private final BookMapper bookMapper;

    @Transactional
    public GetBookResponseDto createBook(WriteBookRequestDto request) {
        // Crear la entidad Book principal
        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .code(request.getCode())
                .publicationDate(request.getPublicationDate())
                .category(request.getCategory())
                .price(request.getPrice())
                .rating(request.getRating())
                .visible(request.getVisible())
                .stock(request.getStock())
                .imageUrl(request.getImageUrl())
                .description(request.getDescription())
                .build();


        // Guardar la entidad (cascade guardará automáticamente las especificaciones e imágenes)
        Book savedSupply = bookJpaRepository.save(book);
        return bookMapper.asGetBookResponseDto(savedSupply);
    }
}
