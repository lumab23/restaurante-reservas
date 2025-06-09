# Sistema de Reservas de Restaurantes

Este é um sistema de gerenciamento de reservas para restaurantes desenvolvido em Java com Spring Boot e JavaFX.

## Requisitos do Sistema

### 1. Ferramentas Necessárias

1. **Java Development Kit (JDK) 21**
   - Download: [Oracle JDK 21](https://www.oracle.com/java/technologies/downloads/#java21) ou [OpenJDK 21](https://jdk.java.net/21/)
   - Instruções de instalação:
     - Windows: Execute o instalador e siga as instruções
     - macOS: Use o instalador .dmg ou Homebrew (`brew install openjdk@21`)
     - Linux: Use o gerenciador de pacotes da sua distribuição

2. **MySQL Server 8.0+**
   - Download: [MySQL Community Server](https://dev.mysql.com/downloads/mysql/)
   - Instruções de instalação:
     - Windows: Execute o instalador MySQL
     - macOS: Use Homebrew (`brew install mysql`)
     - Linux: Use o gerenciador de pacotes da sua distribuição

3. **IDE (Ambiente de Desenvolvimento Integrado)**
   - Recomendado: IntelliJ IDEA Ultimate ou Community Edition
   - Download: [IntelliJ IDEA](https://www.jetbrains.com/idea/download/)
   - Extensões necessárias:
     - Lombok Plugin
     - Spring Boot Plugin
     - JavaFX Plugin

4. **Maven**
   - Geralmente já vem com o IntelliJ IDEA
   - Se necessário, download: [Maven](https://maven.apache.org/download.cgi)

### 2. Configuração do Banco de Dados

1. Instale o MySQL Server seguindo as instruções do site oficial
2. Abra o MySQL Command Line Client ou MySQL Workbench
3. Execute os seguintes comandos:

```sql
CREATE DATABASE restaurante_reservas_db;
USE restaurante_reservas_db;
```

4. Configure as credenciais do banco de dados no arquivo `src/main/resources/application.properties`:
   - Por padrão, o sistema usa:
     - URL: jdbc:mysql://localhost:3306/restaurante_reservas_db
     - Usuário: root
     - Senha: 
   - Você pode alterar estas configurações conforme necessário

### 3. Importando o Projeto

1. Abra o IntelliJ IDEA
2. Clique em "File" > "Open"
3. Navegue até a pasta do projeto e selecione o arquivo `pom.xml`
4. Clique em "Open as Project"
5. Aguarde o Maven baixar todas as dependências

### 4. Configurando o Ambiente de Desenvolvimento

1. **Instalando Extensões no IntelliJ IDEA**:
   - Vá em "File" > "Settings" > "Plugins"
   - Procure e instale:
     - "Lombok"
     - "Spring Boot"
     - "JavaFX"

2. **Configurando o JDK**:
   - Vá em "File" > "Project Structure" > "Project"
   - Em "Project SDK", selecione o JDK 21
   - Em "Project language level", selecione "21 - Preview features"

3. **Configurando o Maven**:
   - Vá em "File" > "Settings" > "Build, Execution, Deployment" > "Build Tools" > "Maven"
   - Verifique se o Maven está configurado corretamente

### 5. Executando o Projeto

1. **Iniciando o Banco de Dados**:
   - Certifique-se de que o MySQL Server está rodando
   - Verifique se o banco de dados `restaurante_reservas_db` foi criado

2. **Executando a Aplicação**:
   - Localize a classe `MainApplication` em `src/main/java/com/example/reservas_restaurantes/ui/MainApplication.java`
   - Clique com o botão direito e selecione "Run 'MainApplication'"
   - Ou use o terminal na pasta do projeto:
     ```bash
     mvn spring-boot:run
     ```

### 6. Estrutura do Projeto

- `src/main/java/com/example/reservas_restaurantes/`
  - `controller/`: Controladores REST
  - `service/`: Lógica de negócios
  - `repository/`: Interfaces de acesso ao banco de dados
  - `model/`: Classes de entidade
  - `ui/`: Interface gráfica JavaFX
  - `config/`: Configurações do Spring Boot
  - `exception/`: Classes de exceção personalizadas
  - `utils/`: Classes utilitárias

### 7. Solução de Problemas Comuns

1. **Erro de Conexão com o Banco de Dados**:
   - Verifique se o MySQL está rodando
   - Confirme as credenciais no `application.properties`
   - Verifique se o banco de dados foi criado

2. **Erro de Dependências**:
   - Execute `mvn clean install` no terminal
   - Atualize o projeto no IntelliJ IDEA (Ctrl+Shift+O)

3. **Erro de JavaFX**:
   - Verifique se o JDK 21 está instalado corretamente
   - Confirme se as dependências do JavaFX estão no `pom.xml`

### 8. Suporte

Para qualquer problema ou dúvida, entre em contato com o desenvolvedor do projeto.

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

