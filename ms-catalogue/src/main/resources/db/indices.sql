-- =====================================================
-- ÍNDICES para optimización de consultas
-- =====================================================

USE papertales_catalogue;

-- Índices en tabla books

-- Permite reinicios sin fallar si los índices ya existen.
SET sql_notes = 0;

CREATE INDEX idx_books_category ON books(category);
CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_books_stock ON books(stock);
CREATE INDEX idx_books_price ON books(price);
CREATE INDEX idx_books_author ON books(author);

SET sql_notes = 1;

