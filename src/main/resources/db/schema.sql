-- Criação do banco de dados
CREATE DATABASE IF NOT EXISTS restaurante_reservas_db;
USE restaurante_reservas_db;

-- Criação da tabela Cliente
CREATE TABLE Cliente (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    telefone VARCHAR(20),
    email VARCHAR(100),
    dataNascimento DATE
);

-- Criação da tabela Admin
CREATE TABLE Admin (
    id_admin INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    cargo VARCHAR(50) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Criação da tabela Mesa
CREATE TABLE Mesa (
    id_mesa INT AUTO_INCREMENT PRIMARY KEY,
    capacidade INT NOT NULL,
    localizacao VARCHAR(100),
    statusMesa ENUM('DISPONIVEL', 'RESERVADA', 'OCUPADA') NOT NULL DEFAULT 'DISPONIVEL'
);

-- Criação da tabela Reserva
CREATE TABLE Reserva (
    id_reserva INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_mesa INT NOT NULL,
    dataHora DATETIME NOT NULL,
    numPessoas INT NOT NULL,
    ocasiao ENUM('ANIVERSARIO', 'CURTICAO', 'AMIGOS_E_FAMILIA', 'JANTAR_A_DOIS', 'NEGOCIOS', 'RELAXAR') NOT NULL,
    statusReserva ENUM('PENDENTE', 'CONFIRMADA', 'CANCELADA') NOT NULL DEFAULT 'PENDENTE',
    observacao TEXT,
    FOREIGN KEY (id_cliente) REFERENCES Cliente(id_cliente) ON DELETE CASCADE,
    FOREIGN KEY (id_mesa) REFERENCES Mesa(id_mesa) ON DELETE CASCADE
);

-- Índices para otimizar joins e buscas
CREATE INDEX idx_reserva_cliente ON Reserva(id_cliente);
CREATE INDEX idx_reserva_mesa ON Reserva(id_mesa);

-- Dados de teste
-- Clientes
INSERT INTO Cliente (nome, telefone, email, dataNascimento) VALUES 
('João Silva', '11999999999', 'joao@email.com', '1990-05-10'),
('Maria Souza', '21988888888', 'maria@email.com', '1985-08-22');

-- Administradores
INSERT INTO Admin (nome, email, senha, cargo) VALUES 
('Administrador Principal', 'admin@restaurante.com', '$2a$10$...', 'Gerente'),
('Supervisor', 'supervisor@restaurante.com', '$2a$10$...', 'Supervisor'),
('Atendente', 'atendente@restaurante.com', '$2a$10$...', 'Atendente');

-- Reservas
INSERT INTO Reserva (id_cliente, id_mesa, dataHora, numPessoas, ocasiao, observacao) VALUES 
(1, 1, '2025-05-20 20:00:00', 2, 'JANTAR_A_DOIS', 'Mesa romântica com vela.'),
(2, 2, '2025-05-21 13:00:00', 1, 'RELAXAR', 'Almoço rápido.'); 