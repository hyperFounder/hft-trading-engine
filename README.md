# HFT Trading Execution Engine

## Project Summary
A low-latency, multithreaded trading execution engine built with **Spring Boot 3**. This system simulates a High-Frequency Trading (HFT) environment, utilizing pessimistic locking and asynchronous processing to ensure strict ACID compliance and thread safety under intense concurrency.


## Core Technical Features
* **Concurrency Control:** Implements `PESSIMISTIC_WRITE` database locking to serialize concurrent ledger updates and prevent race conditions.
* **Asynchronous Processing:** Decouples order ingestion from execution using `CompletableFuture` and non-blocking I/O.
* **Numerical Precision:** Uses `BigDecimal` exclusively for financial arithmetic to avoid floating-point rounding errors.

**Build & Run**

1. Build: ```mvn clean install```

2. Start the application: ```mvn spring-boot:run```. The server will start on: ```http://localhost:8080```

## Interactive API Documentation (Swagger UI)

- Once the application is running, open your browser and go to: ```http://localhost:8080/swagger-ui/index.html``` to explore all endpoints

## API Endpoints

### Execute Trade

```POST /api/v1/trading/execute```

#### Request Body (JSON)
```json
{
  "userId":     1,          // integer - ID of the user placing the order 
  "symbol":     "BTC",      // string  - Trading pair symbol (e.g. BTC, ETH, AAPL) 
  "payWith":    "USD",      // string  - Quote currency (e.g. USD, USDT, EUR)
  "quantity":   0.05        // number  - Quantity to buy in base asset
}
```

### View Portfolio
GET ```/api/v1/trading/portfolio/{userId}```
