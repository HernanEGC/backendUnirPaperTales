package com.unir.papertales.catalogue.repository;

import com.unir.papertales.catalogue.repository.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookJpaRepository extends
        JpaRepository<Book, Long>,
        JpaSpecificationExecutor<Book>,
        PagingAndSortingRepository<Book, Long> {

    // Consulta con JPQL
    @Query("SELECT s FROM Book s ")
    List<Book> findAllBooks();

    // Consulta nativa SQL equivalente a la anterior
    @Query(
            value = "SELECT * FROM books",
            nativeQuery = true)
    List<Book> findAllBooksNative();

    // Consulta con JPQL
    @Query("SELECT s FROM Book s WHERE s.stock > 0")
    List<Book> findAvailableBooks();

    // Consulta nativa SQL equivalente a la anterior
    @Query(
            value = "SELECT * FROM books WHERE stock > 0",
            nativeQuery = true)
    List<Book> findAvailableBooksNative();


    // Consultas por derivacion de nombre de metodo
    List<Book> findByCategoryIgnoreCase(String category);

    List<Book> findByTitleContainingIgnoreCase(String title);
}
