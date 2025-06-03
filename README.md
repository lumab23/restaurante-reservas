# Sistema de Gerenciamento de Reservas para Restaurantes

Um sistema completo de gerenciamento de reservas para restaurantes, desenvolvido com Spring Boot e JavaFX, oferecendo uma interface gráfica moderna e intuitiva para gerenciamento de reservas, clientes, mesas e pagamentos.

## 🚀 Funcionalidades

### Área do Cliente
- Cadastro e login de clientes
- Visualização de mesas disponíveis
- Realização de reservas
- Consulta de reservas existentes
- Cancelamento de reservas

### Área do Administrador
- Dashboard administrativo
- Gerenciamento de mesas
- Visualização de todas as reservas
- Gestão de clientes
- Relatórios de ocupação

## 🛠️ Tecnologias Utilizadas

- Java 21
- Spring Boot 3.2
- JavaFX 21
- MySQL 8.0
- JDBC e Spring JDBC
- Maven
- CSS para estilização
- FXML para interface gráfica

## 📋 Pré-requisitos

- Java 21 ou superior
- Maven 3.8+
- MySQL 8.0 ou superior
- IDE compatível com Java (recomendado: IntelliJ IDEA ou VS Code)

## ⚙️ Configuração do Ambiente

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

4. Compile o projeto:
```bash
./mvnw clean install
```

5. Execute o projeto:
```bash
./mvnw spring-boot:run
```

## 📁 Estrutura do Projeto

```
src/main/java/com/example/reservas_restaurantes/
├── model/          # Classes de domínio (Cliente, Mesa, Reserva, etc.)
├── dao/            # Classes de acesso a dados
├── repository/     # Interfaces de acesso a dados
├── service/        # Lógica de negócio
├── ui/             # Interface gráfica
│   └── controller/ # Controladores JavaFX
├── controller/     # Controladores REST da API
├── utils/          # Classes utilitárias
├── seeds/          # Scripts e classes para inicialização de dados
├── enums/          # Enumerações do sistema
├── exception/      # Classes de exceção personalizadas
└── config/         # Classes de configuração

src/main/resources/
├── fxml/           # Arquivos FXML das telas (admin-dashboard.fxml, cliente-reserva.fxml, etc.)
├── css/            # Estilos CSS
├── images/         # Recursos de imagem
└── application.properties  # Configurações da aplicação
```

## 🧪 Testes

Para executar os testes unitários:
```bash
./mvnw test
```

Para executar testes específicos:
```bash
./mvnw test -Dtest=NomeDaClasseDeTeste
```

## 🔒 Segurança

- Nunca compartilhe ou comite o arquivo `.env`
- Mantenha suas credenciais de banco de dados seguras
- Use o arquivo `application.properties.example` como referência para configuração
- Todas as senhas são armazenadas de forma segura usando hash
- Implementação de controle de acesso baseado em roles (cliente/admin)

## 📝 Convenções de Código

- Seguir as convenções de código Java
- Usar nomes descritivos para classes, métodos e variáveis
- Documentar classes e métodos públicos
- Manter o código organizado e limpo
- Seguir o padrão MVC

## 🤝 Contribuindo

1. Faça um Fork do projeto
2. Crie uma Branch para sua Feature (`git checkout -b feature/...`)
3. Commit suas mudanças (`git commit -m 'Add some ...'`)
4. Push para a Branch (`git push origin feature/...`)
5. Abra um Pull Request

