# restaurante-reservas

Sistema de gerenciamento de reservas para restaurantes, desenvolvido com Spring Boot.

## Requisitos

- Java 21 ou superior
- Maven
- MySQL 8.0 ou superior

## Configuração do Ambiente

1. Clone o repositório:
```bash
git clone https://github.com/lumab23/restaurante-reservas.git
cd restaurante-reservas
```

2. Configure as variáveis de ambiente:
   - Crie um arquivo `.env` na raiz do projeto
   - Adicione as seguintes variáveis com seus valores:
```bash
DB_URL=jdbc:mysql://localhost:3306/reservas_restaurantes
DB_USERNAME=seu_usuario
DB_PASSWORD=sua_senha
DB_DRIVER=com.mysql.cj.jdbc.Driver
```

3. Crie o banco de dados:
```sql
CREATE DATABASE reservas_restaurantes;
```

4. Execute o projeto:
```bash
./mvnw spring-boot:run
```

## Estrutura do Projeto

- `src/main/java/com/example/reservas_restaurantes/`
  - `model/` - Classes de domínio
  - `dao/` - Classes de acesso a dados
  - `repository/` - Interfaces de acesso a dados
  - `service/` - Lógica de negócio
  - `controller/` - Controladores da aplicação
  - `enums/` - Enumerações do sistema
  - `exception/` - Classes de exceção personalizadas

## Testes

Para executar os testes:
```bash
./mvnw test
```

## Segurança

- Nunca compartilhe ou comite o arquivo `.env`
- Mantenha suas credenciais de banco de dados seguras
- Use o arquivo `application.properties.example` como referência para configuração 