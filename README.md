# Pipeline de Eventos de Cliques

Pipeline de tracking de eventos: um producer publica cliques no Kafka, um consumer agrega as contagens por página em janelas de um minuto e grava o resultado no MongoDB, e um endpoint expõe as métricas já agregadas.

É o desenho clássico de ingestão de eventos em alto volume — escrever rápido no broker, processar fora do caminho da requisição e consultar um resultado pré-agregado.

## Tecnologias e bibliotecas

| | |
|---|---|
| Linguagem | Java 17 |
| Framework | Spring Boot 3.3 |
| Mensageria | Apache Kafka via Spring Kafka |
| Persistência | MongoDB 7 via Spring Data MongoDB |
| Validação | Bean Validation |
| Build | Gradle Kotlin DSL (wrapper `gradlew`) |
| Testes | JUnit 5, Mockito, Awaitility, Testcontainers (Kafka e MongoDB) |
| Apoio | Lombok |

## Pré-requisitos

- JDK 17 ou superior
- Docker

## Como rodar

```bash
docker compose up -d
```

```bash
./gradlew bootRun
```

A API fica em `http://localhost:8080`.

## Como a agregação funciona

Cada evento carrega um timestamp, truncado para o minuto na hora do consumo. O par `(página, janela)` identifica um documento na coleção `page_metrics`:

```json
{
  "_id": "home_2026-01-01T10:15:00Z",
  "page": "home",
  "minuteWindow": "2026-01-01T10:15:00Z",
  "totalClicks": 42
}
```

O incremento é feito com um upsert atômico (`$inc`), então várias mensagens do mesmo minuto consumidas em sequência não se sobrescrevem.

## Endpoints

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/events/click` | Publica um evento de clique no tópico `clicks` |
| `POST` | `/events/click/generate?count=N` | Gera `N` eventos sintéticos (padrão 20) |
| `GET` | `/metrics/{page}` | Série de métricas agregadas por minuto da página |

## Exemplos de uso

```bash
curl -s -X POST localhost:8080/events/click \
  -H "Content-Type: application/json" \
  -d '{"page": "home", "userId": "user-42"}'
```

```bash
curl -s -X POST "localhost:8080/events/click/generate?count=50"
```

```bash
curl -s localhost:8080/metrics/home
```

## Testes

```bash
./gradlew test
```

5 testes: 2 unitários e 3 de integração, que sobem Kafka e MongoDB reais em containers pelo Testcontainers.
