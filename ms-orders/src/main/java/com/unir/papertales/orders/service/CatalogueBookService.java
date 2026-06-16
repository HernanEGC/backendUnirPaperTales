package com.unir.papertales.orders.service;

import com.unir.papertales.orders.client.CatalogueClient;
import com.unir.papertales.orders.controller.model.WriteOrderItemDto;
import com.unir.papertales.orders.repository.model.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CatalogueBookService {

    private final CatalogueClient catalogueClient;

    public Map<Long, CatalogueClient.BookSummary> validateItems(List<WriteOrderItemDto> items) {
        Map<Long, CatalogueClient.BookSummary> summaries = new HashMap<>();
        Map<Long, Integer> totals = new HashMap<>();
        for (WriteOrderItemDto item : items) {
            Long bookId = item.getBookId();
            if (bookId == null) {
                throw new IllegalArgumentException("Book id is required");
            }
            CatalogueClient.BookSummary summary = summaries.computeIfAbsent(bookId, catalogueClient::getBookSummary);
            if (summary == null) {
                throw new IllegalArgumentException("Book not found with id: " + bookId);
            }
            int quantity = item.getQuantity() != null ? item.getQuantity() : 1;
            if (quantity < 1) {
                throw new IllegalArgumentException("Quantity must be at least 1 for book " + bookId);
            }
            totals.merge(bookId, quantity, Integer::sum);
        }
        for (Map.Entry<Long, Integer> entry : totals.entrySet()) {
            Long bookId = entry.getKey();
            CatalogueClient.BookSummary summary = summaries.get(bookId);
            if (summary.getVisible() == null || !summary.getVisible()) {
                throw new IllegalArgumentException("Book " + bookId + " is not visible");
            }
            Integer stock = summary.getStock();
            if (stock == null || stock < entry.getValue()) {
                throw new IllegalArgumentException("Insufficient stock for book " + bookId);
            }
        }
        return summaries;
    }

    public void decreaseStock(List<WriteOrderItemDto> items, Map<Long, CatalogueClient.BookSummary> summaries) {
        Map<Long, Integer> totals = new HashMap<>();
        for (WriteOrderItemDto item : items) {
            Long bookId = item.getBookId();
            if (bookId == null) {
                throw new IllegalArgumentException("Book id is required");
            }
            int quantity = item.getQuantity() != null ? item.getQuantity() : 1;
            totals.merge(bookId, quantity, Integer::sum);
        }
        Map<Long, Integer> originalStocks = new HashMap<>();
        try {
            for (Map.Entry<Long, Integer> entry : totals.entrySet()) {
                Long bookId = entry.getKey();
                CatalogueClient.BookSummary summary = summaries.getOrDefault(bookId, catalogueClient.getBookSummary(bookId));
                Integer stock = summary.getStock();
                if (stock == null) {
                    throw new IllegalArgumentException("Stock unavailable for book " + bookId);
                }
                int newStock = stock - entry.getValue();
                if (newStock < 0) {
                    throw new IllegalArgumentException("Insufficient stock for book " + bookId);
                }
                catalogueClient.updateBookStock(bookId, newStock);
                originalStocks.put(bookId, stock);
            }
        } catch (RuntimeException ex) {
            for (Map.Entry<Long, Integer> rollback : originalStocks.entrySet()) {
                try {
                    catalogueClient.updateBookStock(rollback.getKey(), rollback.getValue());
                } catch (RuntimeException rollbackEx) {
                    ex.addSuppressed(rollbackEx);
                }
            }
            throw ex;
        }
    }

    public Map<Long, String> getBookTitles(List<OrderItem> items) {
        Map<Long, String> titles = new HashMap<>();
        for (OrderItem item : items) {
            Long bookId = item.getBookId();
            if (bookId == null || titles.containsKey(bookId)) {
                continue;
            }
            CatalogueClient.BookSummary summary = catalogueClient.getBookSummary(bookId);
            titles.put(bookId, summary != null ? summary.getTitle() : null);
        }
        return titles;
    }

    public Map<Long, String> getBookTitles(Map<Long, CatalogueClient.BookSummary> summaries) {
        Map<Long, String> titles = new HashMap<>();
        for (Map.Entry<Long, CatalogueClient.BookSummary> entry : summaries.entrySet()) {
            CatalogueClient.BookSummary summary = entry.getValue();
            titles.put(entry.getKey(), summary != null ? summary.getTitle() : null);
        }
        return titles;
    }
}
