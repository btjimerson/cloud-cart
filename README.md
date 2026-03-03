# CloudCart

A microservices-based e-commerce demo application built with Spring Boot.

CloudCart demonstrates common microservice architecture patterns including RESTful communication, database-per-service design, and event choreography through pub/sub messaging. It consists of five services: a product catalog, order management, payment processing via Stripe, order history tracking via RabbitMQ fanout exchanges, and a web frontend.

![Cloud Cart Architecture](images/cloud-cart-architecture.png)

## Table of Contents

- [Background](#background)
- [Install](#install)
- [Usage](#usage)
- [API](#api)
- [Contributing](#contributing)
- [License](#license)

## Background

CloudCart was built to demonstrate microservice constructs and loose coupling patterns such as:

- Independent Spring Boot services with separate concerns
- RESTful clients and servers
- Database-per-microservice design (H2 embedded databases)
- Event choreography through RabbitMQ fanout exchanges (pub/sub) -- services are fully decoupled; each publishes its own domain event without knowledge of downstream consumers
- Correlation IDs for linking events across services without direct coupling
- Payment processing with the Stripe API

### Architecture

The application is composed of five microservices managed as a Maven multi-module project with a root parent POM:

| Service | Description |
|---|---|
| [catalog](catalog) | Product catalog with 50 seeded items, backed by H2 |
| [orders](orders) | Order management, backed by H2. Publishes `OrderPlacedEvent` to RabbitMQ |
| [payments](payments) | Payment processing via the Stripe API, publishes `PaymentProcessedEvent` to RabbitMQ |
| [order-history](order-history) | Subscribes to both order and payment events via RabbitMQ, correlates by correlation ID, stores in H2 |
| [frontend](frontend) | Web UI and REST API gateway using Thymeleaf and RestClient |

A [Locust-based load generator](loadgenerator) is also included for testing telemetry and scaling.

### Event Choreography

When an order is placed, the frontend generates a correlation ID (UUID) and passes it to both the payments and orders services. Each service publishes a domain event to its own RabbitMQ **fanout exchange**:

- **orders exchange** - `OrderPlacedEvent` (correlationId, purchaseDate, purchaseAmount, numberOfItems, orderStatus)
- **payments exchange** - `PaymentProcessedEvent` (correlationId, paymentStatus)

The order-history service subscribes to both exchanges and correlates events by their shared correlation ID using an upsert pattern. Because fanout exchanges broadcast to all bound queues, additional consumers can subscribe without modifying the publishers.

## Install

### Prerequisites

- Java 21+
- A [Stripe](https://dashboard.stripe.com/register) developer account (free) for payment processing
- RabbitMQ (required by the `payments`, `orders`, and `order-history` services)
- Docker and Docker Compose (optional, for containerized deployment)
- A Kubernetes cluster (optional, for Kubernetes deployment)

### Clone

```bash
git clone https://github.com/<owner>/cloud-cart.git
cd cloud-cart
```

### Build

Build all services from the root using the included Maven Wrapper:

```bash
./mvnw clean package -DskipTests
```

Build a single service:

```bash
./mvnw clean package -pl catalog -DskipTests
```

### Makefile

A Makefile is provided for common tasks:

```bash
make help       # List all available targets
make build      # Build all services (skip tests)
make test       # Run all tests
make clean      # Clean all build artifacts
```

Per-service targets are also available (`build-<service>`, `test-<service>`):

```bash
make build-catalog
make test-payments
```

## Usage

### Docker Compose

The simplest way to run all services locally. Set your Stripe API key and start the stack:

```bash
STRIPE_API_KEY=<your-key> docker compose up
```

The frontend is accessible at `http://localhost:8080`.

To also start the load generator:

```bash
STRIPE_API_KEY=<your-key> docker compose --profile loadgen up
```

To rebuild containers after code changes:

```bash
STRIPE_API_KEY=<your-key> docker compose up --build -d
```

Or using the Makefile:

```bash
make docker-up      # Start all services
make docker-down    # Stop all services
make docker-logs    # Follow logs
```

### Kubernetes

Create the namespace and Stripe secret, then apply the manifest:

```bash
kubectl create ns cloud-cart
kubectl create secret generic -n cloud-cart stripe-secret --from-literal=stripe-api-key=<your-key>
kubectl apply -n cloud-cart -f ./manifests/cloud-cart.yaml
```

### Local / Standalone

Run each service individually with Maven:

1. Install and start a RabbitMQ instance. Set the `RABBITMQ_HOST` environment variable to its hostname for the `payments`, `orders`, and `order-history` services.
2. Set the `STRIPE_API_KEY` environment variable for the `payments` service.
3. Start each service:

```bash
./mvnw -pl <service> spring-boot:run -Dspring-boot.run.profiles=local
```

### Testing Payments

Use these Stripe test card numbers with any 3-digit CVC and any future expiration date:

| Number | Brand |
|---|---|
| 4242424242424242 | Visa |
| 5555555555554444 | Mastercard |
| 378282246310005 | American Express |
| 6011111111111117 | Discover |

See the [Stripe testing documentation](https://stripe.com/docs/testing) for additional test cards.

## API

The frontend service exposes REST endpoints under `/api`:

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/catalog` | List all catalog items |
| `GET` | `/api/catalog/{id}` | Get a catalog item by ID |
| `POST` | `/api/order` | Place an order |
| `GET` | `/api/order-history` | List all order history records |
| `GET` | `/api/order-history/{id}` | Get an order history record by ID |
| `GET` | `/api/version` | Get the application version |

Full API documentation is available on [Postman](https://documenter.getpostman.com/view/1749839/UVyxQtit).

## TODO

- **Add integration/contract tests** — The current test suite covers unit-level behavior with mocked HTTP calls. Consider adding integration tests (e.g. Testcontainers, Spring Cloud Contract) to verify actual inter-service communication contracts.

## Contributing

PRs are accepted. For questions or bug reports, please open an issue on GitHub.

## License

[Apache-2.0](LICENSE) &copy; Brian Jimerson
