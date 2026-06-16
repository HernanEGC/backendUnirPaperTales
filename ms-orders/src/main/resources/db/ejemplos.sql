-- =====================================================
-- Datos de ejemplo para la base de datos de órdenes
-- =====================================================

-- Insertar usuario de ejemplo
INSERT INTO users (id, name, email, password, avatar) VALUES
(1, 'Lucía Martín', 'lucia@relatosdepapel.com', '123456', 'https://i.pravatar.cc/150?img=47');

-- Insertar órdenes
INSERT INTO orders (id, user_id, order_date, status, total) VALUES
('PED-1051', 1, '2026-04-18', 'Entregado', 38.94),
('PED-1044', 1, '2026-03-27', 'Entregado', 21.50),
('PED-1038', 1, '2026-02-14', 'Entregado', 35.70),
('PED-1029', 1, '2026-01-22', 'Entregado', 22.10),
('PED-1015', 1, '2025-12-09', 'Entregado', 20.00);

-- Insertar items de órdenes (PED-1051: Código de tinta y Cartas desde el invierno)
INSERT INTO order_items (order_id, book_id, price, quantity) VALUES
('PED-1051', 4, 19.99, 1),
('PED-1051', 3, 16.75, 1);

-- Insertar items de órdenes (PED-1044: El mapa de las ciudades invisibles)
INSERT INTO order_items (order_id, book_id, price, quantity) VALUES
('PED-1044', 2, 21.50, 1);

-- Insertar items de órdenes (PED-1038: La biblioteca de los susurros y Manual para domar dragones)
INSERT INTO order_items (order_id, book_id, price, quantity) VALUES
('PED-1038', 1, 18.95, 1),
('PED-1038', 7, 14.95, 1);

-- Insertar items de órdenes (PED-1029: Sombras en la editorial)
INSERT INTO order_items (order_id, book_id, price, quantity) VALUES
('PED-1029', 6, 22.10, 1);

-- Insertar items de órdenes (PED-1015: La hora azul)
INSERT INTO order_items (order_id, book_id, price, quantity) VALUES
('PED-1015', 8, 20.00, 1);
