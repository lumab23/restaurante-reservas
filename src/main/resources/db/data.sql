-- Inserir administrador padrão
INSERT IGNORE INTO Admin (nome, email, senha, cargo, ativo) VALUES 
('Administrador', 'admin@restaurante.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'GERENTE', true);

-- Inserir mesas iniciais
INSERT INTO Mesa (capacidade, localizacao, statusMesa) VALUES 
-- Terraço
(2, 'Terraço - Mesa 1', 'DISPONIVEL'),
(2, 'Terraço - Mesa 2', 'DISPONIVEL'),
-- Salão Principal
(4, 'Salão Principal - Mesa 1', 'DISPONIVEL'),
(4, 'Salão Principal - Mesa 2', 'DISPONIVEL'),
(4, 'Salão Principal - Mesa 3', 'DISPONIVEL'),
-- Área VIP
(6, 'Área VIP - Mesa 1', 'DISPONIVEL'),
(6, 'Área VIP - Mesa 2', 'DISPONIVEL'),
(8, 'Área VIP - Mesa 3', 'DISPONIVEL'),
-- Salão de Eventos
(10, 'Salão de Eventos - Mesa 1', 'DISPONIVEL'); 