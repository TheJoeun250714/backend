# 빌드 스테이지 - Java 21 사용
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew build -x test

# 실행 스테이지 - Java 21 사용
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 환경변수 기본값 설정
ENV DB_URL=jdbc:postgresql://localhost:5432/meomulm
ENV DB_USERNAME=postgres
ENV DB_PASSWORD=postgres
ENV ELASTICSEARCH_URIS=http://localhost:9200
ENV ELASTICSEARCH_USERNAME=elastic
ENV ELASTICSEARCH_PASSWORD=meomulm2026!

# 포트 노출
EXPOSE 8080

# 애플리케이션 실행 (환경변수 사용)
ENTRYPOINT ["sh", "-c", \
    "java \
    -Dspring.datasource.url=${DB_URL} \
    -Dspring.datasource.username=${DB_USERNAME} \
    -Dspring.datasource.password=${DB_PASSWORD} \
    -Dspring.elasticsearch.uris=${ELASTICSEARCH_URIS} \
    -Dspring.elasticsearch.username=${ELASTICSEARCH_USERNAME} \
    -Dspring.elasticsearch.password=${ELASTICSEARCH_PASSWORD} \
    -jar app.jar"]