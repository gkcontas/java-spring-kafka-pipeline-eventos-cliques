# Pipeline de Eventos de Cliques

Simulação de tracking de eventos (cliques/visualizações) em Java com Spring Boot: um producer publica eventos no Kafka, um consumer agrega por página e janela de um minuto e grava/atualiza o resultado no MongoDB, e um endpoint expõe as métricas agregadas.

## Status

✅ MVP implementado.

## Stack

- Java 17 + Spring Boot 3.3
- Apache Kafka (Spring Kafka)
- MongoDB (Spring Data MongoDB)
- Lombok (na entidade `PageMetric`)
- Gradle (Kotlin DSL) + wrapper `gradlew`
- Testcontainers (Kafka + MongoDB) + Awaitility + JUnit 5 + Mockito

## Como rodar

1. Suba Kafka e MongoDB:
   ```bash
   docker compose up -d
   ```
2. Rode a aplicação:
   ```bash
   ./gradlew bootRun
   ```
3. A API sobe em `http://localhost:8080`.

## Como rodar os testes

```bash
./gradlew test
```

Os testes de integração usam Testcontainers e sobem Kafka e MongoDB reais em containers — é necessário ter Docker disponível. Suíte completa: **5 testes, todos passando** — 2 unitários e 3 de integração.

### Nota sobre Testcontainers e Docker Engine recente

Se os testes falharem com `client version 1.32 is too old. Minimum supported API version is 1.40`, a causa é o `docker-java` embutido no Testcontainers negociar a API 1.32, abaixo do mínimo aceito pelo Docker Engine 29+. Correção global, de uma linha:

```bash
echo 'api.version=1.44' > ~/.docker-java.properties
```

## Como a agregação por janela funciona

Cada evento de clique carrega um `timestamp`. Ao ser consumido, esse timestamp é truncado para o minuto (`Instant.truncatedTo(ChronoUnit.MINUTES)`), formando a "janela" do evento. O par `(página, janela)` vira a chave de um documento na coleção `page_metrics` do MongoDB:

```json
{
  "_id": "home_2026-01-01T10:15:00Z",
  "page": "home",
  "minuteWindow": "2026-01-01T10:15:00Z",
  "totalClicks": 42
}
```

Cada novo evento faz um **upsert atômico** (`$inc` no `totalClicks`, `$setOnInsert` para os demais campos) direto no MongoDB via `MongoTemplate`, em vez de ler-modificar-gravar — isso evita perda de incrementos quando várias mensagens do mesmo minuto são consumidas em sequência rápida.

## Endpoints principais

| Método | Rota                          | Descrição                                               |
|--------|--------------------------------|-----------------------------------------------------------|
| POST   | `/events/click`                | Publica um evento de clique no tópico Kafka `clicks`      |
| POST   | `/events/click/generate?count=N` | Gera `N` eventos sintéticos (padrão 20), distribuídos entre páginas de exemplo |
| GET    | `/metrics/{page}`              | Retorna a série de métricas agregadas por minuto para a página |

## Exemplo de uso

```bash
# Publicar um evento de clique real
curl -s -X POST localhost:8080/events/click \
  -H "Content-Type: application/json" \
  -d '{"page": "home", "userId": "user-42"}'

# Gerar 50 eventos sintéticos para demonstração
curl -s -X POST "localhost:8080/events/click/generate?count=50"

# Consultar métricas agregadas da página "home" (aguarde alguns instantes
# até o consumer processar as mensagens)
curl -s localhost:8080/metrics/home
```
