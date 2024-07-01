# Use a imagem base do OpenJDK 17
FROM openjdk:17-jdk-slim

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copie o arquivo JAR da aplicação para o diretório de trabalho
COPY target/transfer-0.0.1-SNAPSHOT.jar /app/myapp.jar

# Exponha a porta que sua aplicação Spring Boot usa (ajuste conforme necessário)
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "myapp.jar"]