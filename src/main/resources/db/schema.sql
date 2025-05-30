-- Criação da tabela Cliente
CREATE TABLE IF NOT EXISTS Cliente (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    telefone VARCHAR(20),
    email VARCHAR(100),
    dataNascimento DATE
);

-- Criação da tabela Admin
CREATE TABLE IF NOT EXISTS Admin (
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
CREATE TABLE IF NOT EXISTS Mesa (
    id_mesa INT AUTO_INCREMENT PRIMARY KEY,
    capacidade INT NOT NULL,
    localizacao VARCHAR(100),
    statusMesa ENUM('DISPONIVEL', 'RESERVADA', 'OCUPADA') NOT NULL DEFAULT 'DISPONIVEL'
);

-- Criação da tabela Reserva
CREATE TABLE IF NOT EXISTS Reserva (
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

-- Criação da tabela Cardapio
CREATE TABLE IF NOT EXISTS Cardapio (
    id_cardapio INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    preco DECIMAL(10,2) NOT NULL
);

-- Criação da tabela Pedido
CREATE TABLE IF NOT EXISTS Pedido (
    id_pedido INT PRIMARY KEY AUTO_INCREMENT,
    id_reserva INT NOT NULL,
    dataHora DATETIME NOT NULL,
    FOREIGN KEY (id_reserva) REFERENCES Reserva(id_reserva) ON DELETE CASCADE
);

-- Criação da tabela ItemPedido
CREATE TABLE IF NOT EXISTS ItemPedido (
    id_item_pedido INT PRIMARY KEY AUTO_INCREMENT,
    id_pedido INT NOT NULL,
    id_cardapio INT NOT NULL,
    quantidade INT NOT NULL,
    FOREIGN KEY (id_pedido) REFERENCES Pedido(id_pedido) ON DELETE CASCADE,
    FOREIGN KEY (id_cardapio) REFERENCES Cardapio(id_cardapio)
);

-- Criação da tabela Pagamento
CREATE TABLE IF NOT EXISTS Pagamento (
    id_pagamento INT PRIMARY KEY AUTO_INCREMENT,
    id_reserva INT NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    metodoPagamento ENUM('PIX', 'CARTAO') NOT NULL,
    FOREIGN KEY (id_reserva) REFERENCES Reserva(id_reserva) ON DELETE CASCADE
);

-- Criação da tabela PagamentoCartao
CREATE TABLE IF NOT EXISTS PagamentoCartao (
    id_pagamento INT PRIMARY KEY,
    numeroCartao VARCHAR(20) NOT NULL,
    titular VARCHAR(100) NOT NULL,
    validade DATE NOT NULL,
    cvv VARCHAR(5) NOT NULL,
    FOREIGN KEY (id_pagamento) REFERENCES Pagamento(id_pagamento) ON DELETE CASCADE
);

-- Criação da tabela PagamentoPix
CREATE TABLE IF NOT EXISTS PagamentoPix (
    id_pagamento INT PRIMARY KEY,
    chavePix VARCHAR(100) NOT NULL,
    FOREIGN KEY (id_pagamento) REFERENCES Pagamento(id_pagamento) ON DELETE CASCADE
); 