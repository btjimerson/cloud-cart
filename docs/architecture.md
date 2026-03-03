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

    frontend --> catalog
    frontend --> orders
    frontend --> order-history
    frontend --> payments

    catalog --- catalog_db
    orders --- orders_db
    order-history --- order_history_db

    payments -.-> stripe
    payments -.-> rabbitmq
    orders -.-> rabbitmq
    order-history -.-> rabbitmq
```
