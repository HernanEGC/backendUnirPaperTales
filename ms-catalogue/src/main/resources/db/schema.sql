-- =====================================================
-- DDL para crear el esquema completo de UNIR Papertales Catalogue
-- =====================================================

-- Creacion de schema (reinicio completo en cada arranque)
DROP SCHEMA IF EXISTS papertales_catalogue;
CREATE SCHEMA papertales_catalogue;
USE papertales_catalogue;

-- Tabla principal de catalogo
CREATE TABLE IF NOT EXISTS books (
            id BIGINT AUTO_INCREMENT PRIMARY KEY,
            title VARCHAR(255) NOT NULL,
            author VARCHAR(255) NOT NULL,
            code VARCHAR(20) UNIQUE,
            publication_date DATE,
            category VARCHAR(100),
            price DECIMAL(10,2),
            rating INTEGER CHECK (rating >= 1 AND rating <= 5) DEFAULT 5,
            visible BOOLEAN DEFAULT TRUE,
            stock INT DEFAULT 0,
            image_url VARCHAR(500) NOT NULL,
            description VARCHAR(500)
);
