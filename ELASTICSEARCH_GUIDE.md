# Elasticsearch 통합 가이드

## 개요
이 프로젝트는 Elasticsearch 8.12를 사용하여 숙소, 리뷰, 상품 검색 기능을 구현합니다.

## 주요 기능

### 1. 숙소 검색 (AccommodationElasticsearchService)
- **키워드 검색**: 숙소명, 주소 기반 한글 검색 (Nori 분석기)
- **위치 기반 검색**: GeoPoint를 활용한 반경 5km 내 검색
- **필터링**: 숙소 타입, 가격 범위, 평점 필터링
- **정렬**: 가격, 평점, 리뷰 수 기준 정렬
- **자동완성**: Phrase Prefix 쿼리를 활용한 검색어 자동완성

### 2. 리뷰 검색 (ReviewElasticsearchService)
- **숙소별 리뷰 조회**: 최신순 정렬
- **사용자별 리뷰 조회**: 내가 작성한 리뷰
- **키워드 검색**: 리뷰 내용 기반 검색
- **평점 필터링**: 특정 평점 이상 리뷰 조회

### 3. 데이터 동기화 (ElasticsearchSyncService)
- **실시간 동기화**: 데이터 생성/수정/삭제 시 즉시 반영
- **배치 동기화**: 매일 새벽 자동 전체 동기화
- **Bulk 작업**: 대량 데이터 효율적 처리

## 설치 및 실행

### 1. Elasticsearch 실행 (Docker)

```bash
# Docker Compose로 Elasticsearch 및 Kibana 실행
docker-compose -f docker-compose-elasticsearch.yml up -d

# Elasticsearch 상태 확인
curl -u elastic:meomulm2026! http://localhost:9200/_cluster/health

# Kibana 접속
# http://localhost:5601
# Username: elastic
# Password: meomulm2026!
```

### 2. Nori 한글 분석기 설치

Elasticsearch 컨테이너에서 자동으로 설치되지만, 수동 설치가 필요한 경우:

```bash
# Elasticsearch 컨테이너 접속
docker exec -it meomulm-elasticsearch bash

# Nori 플러그인 설치
elasticsearch-plugin install analysis-nori

# Elasticsearch 재시작
docker restart meomulm-elasticsearch
```

### 3. 애플리케이션 설정

`application.properties`에 다음 설정 추가:

```properties
# Elasticsearch Configuration
spring.elasticsearch.uris=http://localhost:9200
spring.elasticsearch.username=elastic
spring.elasticsearch.password=meomulm2026!
spring.elasticsearch.connection-timeout=10s
spring.elasticsearch.socket-timeout=30s

# Index Names
elasticsearch.index.accommodation=accommodations
elasticsearch.index.product=products
elasticsearch.index.review=reviews
```

### 4. 초기 데이터 동기화

애플리케이션 시작 후 전체 데이터 동기화:

```bash
# Spring Boot 애플리케이션 실행 시 자동으로 인덱스 생성
# 수동으로 배치 동기화를 실행하려면 스케줄러 직접 호출 또는
# 아래 API 호출

curl -X POST http://localhost:8080/api/admin/sync/all
```

## API 엔드포인트

### 숙소 검색 API

#### 1. 통합 검색
```http
GET /api/accommodation/search?keyword=강남&minPrice=50000&maxPrice=200000&minRating=4.0
```

**파라미터:**
- `keyword`: 검색 키워드 (숙소명, 주소)
- `latitude`, `longitude`: 현재 위치 (위치 기반 검색)
- `accommodationType`: 숙소 타입
- `minPrice`, `maxPrice`: 가격 범위
- `minRating`: 최소 평점
- `sortBy`: 정렬 기준 (price_asc, price_desc, rating, review_count)
- `page`: 페이지 번호

#### 2. 위치 기반 검색
```http
POST /api/accommodation/map
Content-Type: application/json

{
  "latitude": 37.5665,
  "longitude": 126.9780
}
```

#### 3. 자동완성
```http
GET /api/accommodation/autocomplete?prefix=강남
```

#### 4. 인기 숙소
```http
GET /api/accommodation/popular?accommodationAddress=서울특별시 강남구
```

### 리뷰 검색 API

#### 1. 숙소별 리뷰 조회
```http
GET /api/review/accommodationId/123
```

#### 2. 내 리뷰 조회
```http
GET /api/review
Authorization: Bearer {token}
```

#### 3. 리뷰 검색
```http
GET /api/review/search?keyword=깨끗
```

#### 4. 평점별 필터링
```http
GET /api/review/filter/123?minRating=4.0
```

## 인덱스 구조

### 1. accommodations 인덱스

```json
{
  "mappings": {
    "properties": {
      "accommodationId": { "type": "integer" },
      "accommodationName": { 
        "type": "text",
        "analyzer": "nori"
      },
      "accommodationAddress": { 
        "type": "text",
        "analyzer": "nori"
      },
      "accommodationType": { "type": "keyword" },
      "location": { "type": "geo_point" },
      "minPrice": { "type": "integer" },
      "averageRating": { "type": "double" },
      "reviewCount": { "type": "integer" }
    }
  }
}
```

### 2. reviews 인덱스

```json
{
  "mappings": {
    "properties": {
      "reviewId": { "type": "integer" },
      "accommodationId": { "type": "integer" },
      "userId": { "type": "integer" },
      "reviewContent": { 
        "type": "text",
        "analyzer": "nori"
      },
      "rating": { "type": "double" },
      "createdAt": { "type": "date" }
    }
  }
}
```

## 검색 쿼리 예제

### 1. 복합 검색 (키워드 + 위치 + 필터)

```java
SearchRequest searchRequest = SearchRequest.of(s -> s
    .index("accommodations")
    .query(q -> q
        .bool(b -> b
            // 키워드 검색
            .should(sh -> sh
                .match(m -> m
                    .field("accommodationName")
                    .query("강남")
                    .boost(2.0f)
                )
            )
            // 위치 검색
            .filter(f -> f
                .geoDistance(g -> g
                    .field("location")
                    .distance("5km")
                    .location(l -> l.latlon(ll -> ll
                        .lat(37.5665)
                        .lon(126.9780)
                    ))
                )
            )
            // 가격 필터
            .filter(f -> f
                .range(r -> r
                    .field("minPrice")
                    .gte(JsonData.of(50000))
                    .lte(JsonData.of(200000))
                )
            )
        )
    )
    .sort(sort -> sort
        .field(f -> f.field("averageRating").order(SortOrder.Desc))
    )
);
```

### 2. 한글 검색 (Nori 분석기)

```java
// "깨끗한 호텔" 검색
SearchRequest searchRequest = SearchRequest.of(s -> s
    .index("reviews")
    .query(q -> q
        .match(m -> m
            .field("reviewContent")
            .query("깨끗한 호텔")
        )
    )
);
```

## 성능 최적화

### 1. 인덱스 설정
- Refresh Interval: 실시간성이 중요하지 않은 경우 증가
- Replica 수: 읽기 성능 향상을 위해 조정

### 2. 쿼리 최적화
- Filter Context 사용: 스코어링이 필요없는 조건은 filter 사용
- Routing: 특정 샤드로 쿼리 라우팅
- Source Filtering: 필요한 필드만 반환

### 3. 배치 작업
- Bulk API 사용: 대량 데이터 처리 시 성능 향상
- 비동기 처리: CompletableFuture 활용

## 모니터링

### Kibana를 통한 모니터링
1. 인덱스 상태: `GET /_cat/indices?v`
2. 클러스터 상태: `GET /_cluster/health`
3. 검색 성능: Kibana Dev Tools에서 쿼리 실행 및 분석

### 로그 확인
```bash
# Elasticsearch 로그
docker logs meomulm-elasticsearch

# 애플리케이션 로그
tail -f application.log
```

## 트러블슈팅

### 1. 연결 오류
```
Could not connect to Elasticsearch
```
**해결방법**: Docker 컨테이너 상태 확인 및 재시작

### 2. 한글 검색 안됨
```
Korean text not properly analyzed
```
**해결방법**: Nori 플러그인 설치 확인 및 인덱스 재생성

### 3. 메모리 부족
```
OutOfMemoryError
```
**해결방법**: Docker Compose의 ES_JAVA_OPTS 조정

## 참고 자료

- [Elasticsearch 공식 문서](https://www.elastic.co/guide/en/elasticsearch/reference/8.12/index.html)
- [Nori 분석기 가이드](https://www.elastic.co/guide/en/elasticsearch/plugins/8.12/analysis-nori.html)
- [Spring Data Elasticsearch](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/)

## 버전 정보

- Elasticsearch: 8.12.0
- Spring Data Elasticsearch: 5.2.x
- Elasticsearch Java Client: 8.12.0
- Nori Plugin: 8.12.0
