package com.unir.papertales.orders.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CatalogueClient {

    private final RestTemplate restTemplate;

    public BookSummary getBookSummary(Long bookId) {
        try {
            HttpEntity<Void> entity = new HttpEntity<>(createAuthHeaders());
            return restTemplate.exchange(
                    "http://catalogue/api/v1/books/{bookId}",
                    HttpMethod.GET,
                    entity,
                    BookSummary.class,
                    bookId
            ).getBody();
        } catch (HttpClientErrorException.NotFound ex) {
            throw new IllegalArgumentException("Book not found with id: " + bookId);
        } catch (HttpClientErrorException ex) {
            throw new IllegalArgumentException("Catalogue rejected request for book id: " + bookId);
        } catch (ResourceAccessException ex) {
            throw new IllegalStateException("Catalogue service is unavailable", ex);
        }
    }

    public void updateBookStock(Long bookId, Integer stock) {
        try {
            HttpHeaders headers = createAuthHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(Map.of("stock", stock), headers);
            restTemplate.exchange(
                    "http://catalogue/api/v1/books/{bookId}",
                    HttpMethod.PATCH,
                    entity,
                    Void.class,
                    bookId
            );
        } catch (HttpClientErrorException.NotFound ex) {
            throw new IllegalArgumentException("Book not found with id: " + bookId);
        } catch (HttpClientErrorException ex) {
            throw new IllegalArgumentException("Catalogue rejected stock update for book id: " + bookId);
        } catch (ResourceAccessException ex) {
            throw new IllegalStateException("Catalogue service is unavailable", ex);
        }
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            headers.setBearerAuth(jwtAuthenticationToken.getToken().getTokenValue());
        }
        return headers;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BookSummary {
        @JsonProperty("id")
        private Long id;
        @JsonProperty("title")
        private String title;
        @JsonProperty("stock")
        private Integer stock;
        @JsonProperty("visible")
        private Boolean visible;
    }
}
