FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN javac AIChatbotAPI.java ChatServer.java

EXPOSE 8081

CMD ["java", "ChatServer"]