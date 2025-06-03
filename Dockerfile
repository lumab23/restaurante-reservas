# Usa uma imagem base Java 21 com OpenJFX para suporte a JavaFX
FROM openjdk:21-jdk-slim

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copia o arquivo pom.xml para que as dependências possam ser baixadas primeiro
COPY pom.xml .

# Copia os diretórios src, db, css, fxml, images e .mvn para o contêiner
COPY src ./src
COPY .mvn ./.mvn
COPY mvnw .
COPY mvnw.cmd .
COPY target/reservas_restaurantes-0.0.1-SNAPSHOT.jar target/

# Define os argumentos de build para as opções do JavaFX
ARG JAVAFX_VERSION=21.0.2

# Define o Spring profile
ARG SPRING_PROFILES_ACTIVE=prod

# Define as variáveis de ambiente do banco de dados (que serão substituídas pelo docker-compose)
ENV DB_URL="jdbc:mysql://mysql-db:3306/restaurante_reservas_db"
ENV DB_USERNAME="user"
ENV DB_PASSWORD="password"
ENV DB_DRIVER="com.mysql.cj.jdbc.Driver"

# Permite que o Spring Boot detecte classes de escaneamento em tempo de execução
ENV SPRING_AOT_ENABLED=true

#  dependências nativas para JavaFX
RUN apt-get update && apt-get install -y \
    libx11-6 \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libfreetype6 \
    libgtk-3-0 \
    libxxf86vm1 \
    libgl1 \
    && rm -rf /var/lib/apt/lists/*

# Compila o projeto Maven. Usa a opção --no-transfer-progress para evitar logs muito verbosos
# e -Dspring-boot.build-info.enabled=false para desabilitar a geração de informações de build (opcional)
RUN --mount=type=cache,target=/root/.m2 ./mvnw clean install -DskipTests -Dspring-boot.run.profiles=${SPRING_PROFILES_ACTIVE} --no-transfer-progress

# Define o comando para executar a aplicação
# Adicione --add-opens java.base/java.lang=ALL-UNNAMED
CMD ["java", "--add-opens=java.base/java.lang=ALL-UNNAMED", "-jar", "target/reservas_restaurantes-0.0.1-SNAPSHOT.jar"]

# Expõe a porta que a aplicação Spring Boot estará ouvindo (8080 por padrão)
EXPOSE 8080