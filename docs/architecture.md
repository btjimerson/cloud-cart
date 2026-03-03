```mermaid
graph TD
    frontend["frontend"]

    catalog["catalog"]
    orders["orders"]
    order-history["order-history"]
    payments["payments"]

    catalog_db[("H2")]
    orders_db[("H2")]
    order_history_db[("H2")]

    stripe(["Stripe API"])
    rabbitmq(["RabbitMQ"])

    orders_exchange{{"orders exchange\n(fanout)"}}
    payments_exchange{{"payments exchange\n(fanout)"}}

    frontend -->|REST| catalog
    frontend -->|REST| orders
    frontend -->|REST| order-history
    frontend -->|REST| payments

    catalog --- catalog_db
    orders --- orders_db
    order-history --- order_history_db

    payments -->|REST| stripe
    orders -.->|OrderPlacedEvent| orders_exchange
    payments -.->|PaymentProcessedEvent| payments_exchange
    orders_exchange -.-> order-history
    payments_exchange -.-> order-history
```

## Patterns

- **Database-per-service** -- Each service owns its own H2 database. No shared state between services.
- **Event choreography** -- Orders and payments each publish domain events to their own RabbitMQ fanout exchange. The order-history service subscribes to both and correlates events by a shared correlation ID. No service orchestrates or directs the others, keeping them loosely coupled.
- **Loose coupling** -- Services communicate only through REST APIs (synchronous) and pub/sub events (asynchronous). Adding a new event consumer requires no changes to the publishers.
- **Correlation IDs** -- The frontend generates a UUID and passes it to both the orders and payments services. This ID links events from different services without requiring direct coupling between them.
