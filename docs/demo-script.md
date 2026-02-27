# CloudCart Demo Script

A guided walkthrough of CloudCart's microservice architecture, e-commerce features, and operational capabilities.

## Prerequisites

- Docker and Docker Compose installed
- A [Stripe test API key](https://dashboard.stripe.com/test/apikeys)
- A terminal and a web browser
- (Optional) `curl` or Postman for API demos
- (Optional) A Kubernetes cluster with [Gloo Gateway](https://docs.solo.io/gloo-gateway/) for the canary deployment demo

## 1. Start the Stack

Start all services with Docker Compose:

```bash
STRIPE_API_KEY=<your-test-key> docker compose up -d
```

Wait for all containers to report healthy:

```bash
docker ps
```

Every Java service includes a Docker `HEALTHCHECK` that polls its Spring Boot Actuator health endpoint. The `STATUS` column will show `healthy` once each service is ready.

| Service | URL |
|---|---|
| Frontend | http://localhost:8080 |
| Catalog | http://localhost:8081 |
| Orders | http://localhost:8082 |
| Payments | http://localhost:8083 |
| Payment History | http://localhost:8084 |
| RabbitMQ | localhost:5672 |

## 2. Architecture Overview

Open http://localhost:8080 in a browser. The home page renders a live architecture diagram using Mermaid.js, showing all five microservices and their dependencies.

**Talking points:**

- Five independently deployable Spring Boot services
- Database-per-service pattern -- catalog, orders, and payment-history each own an H2 database
- Synchronous communication via REST (frontend to backend services)
- Asynchronous communication via RabbitMQ (payments to payment-history)
- External integration with the Stripe API for payment processing

## 3. Browse the Product Catalog

1. Click **Go To Catalog** on the home page
2. Browse the 50-product catalog displayed in a responsive card grid
3. Click any product to view its detail page with image, description, and price

**Talking points:**

- The frontend service calls the catalog service over REST to fetch product data
- The catalog service seeds its H2 database on startup from a JSON file (`seed-data/catalog.json`)
- Each service runs on its own port with its own embedded database -- no shared state

**API equivalent:**

```bash
# List all catalog items
curl http://localhost:8080/api/catalog | jq

# Get a single item
curl http://localhost:8080/api/catalog/100 | jq
```

## 4. Shopping Cart and Checkout

1. From a product detail page, click **Add to Cart** -- notice the cart icon and badge appear in the navbar
2. Add a few more items
3. Click the cart icon to go to checkout
4. Fill in the billing address form
5. Enter a Stripe test card:

| Field | Value |
|---|---|
| Card Number | `4242424242424242` |
| Expiration Month | `12` |
| Expiration Year | `26` |
| CVC | `123` |

6. Click **Submit Order**
7. Observe the "Your order was successfully placed" confirmation

**Talking points:**

- The shopping cart is stored server-side in the HTTP session
- Client-side form validation prevents incomplete submissions
- On submit, the frontend orchestrates calls to multiple backend services

## 5. Payment Processing Flow

This is the most architecturally interesting flow in the application. Walk through what happened when the order was placed:

1. **Frontend** sent the payment details to the **payments** service via REST
2. **Payments** service created a Stripe Token and Charge using the Stripe API
3. **Payments** service published the payment record to a RabbitMQ queue (`payments`)
4. **Payment-history** service consumed the message from RabbitMQ and persisted it to its own H2 database
5. **Frontend** received the charge status, created an order in the **orders** service, and cleared the cart

**Demonstrate the result:**

1. Click **View Payment History** from the home page (or navigate to http://localhost:8080/payments)
2. Click on a payment to see its details
3. The payment-history service received this data asynchronously -- it has no direct dependency on the payments service

**Talking points:**

- Synchronous REST for request-response flows (payment processing)
- Asynchronous messaging for event-driven flows (payment history recording)
- The payments and payment-history services are loosely coupled through RabbitMQ
- If payment-history is temporarily down, messages queue in RabbitMQ and are delivered when it recovers

## 6. REST API

CloudCart exposes a JSON API through the frontend service at `/api`. Demonstrate with `curl`:

```bash
# List all catalog items
curl -s http://localhost:8080/api/catalog | jq '.[0:3]'

# Get a specific product
curl -s http://localhost:8080/api/catalog/100 | jq

# View all payments
curl -s http://localhost:8080/api/payments | jq

# Get a specific payment
curl -s http://localhost:8080/api/payments/1 | jq

# Check the application version
curl -s http://localhost:8080/api/version | jq
```

**Place an order via the API:**

```bash
curl -s -X POST http://localhost:8080/api/order \
  -H "Content-Type: application/json" \
  -d '{
    "catalogItems": [{"id": 100, "name": "Apple AirPods Pro", "amount": 189.99}],
    "billingAddress": {
      "name": "Jane Doe",
      "address": "123 Main St",
      "city": "Portland",
      "state": "OR",
      "zip": "97201"
    },
    "payment": {
      "cardNumber": "4242424242424242",
      "expirationMonth": 12,
      "expirationYear": 26,
      "cvc": "123"
    }
  }' | jq
```

## 7. Spring Boot Actuator

Every service exposes the full set of Spring Boot Actuator endpoints. These provide production-ready operational features out of the box.

```bash
# Health check (also used by Docker HEALTHCHECK)
curl -s http://localhost:8080/actuator/health | jq

# List all available actuator endpoints
curl -s http://localhost:8080/actuator | jq

# View all request mappings
curl -s http://localhost:8080/actuator/mappings | jq '.contexts[].mappings.dispatcherServlets' | head -50

# View application environment and configuration
curl -s http://localhost:8080/actuator/env | jq '.propertySources[0]'

# View JVM and HTTP metrics
curl -s http://localhost:8080/actuator/metrics | jq '.names[]' | head -20

# Get a specific metric (e.g., JVM memory)
curl -s http://localhost:8080/actuator/metrics/jvm.memory.used | jq

# View HTTP request metrics
curl -s http://localhost:8080/actuator/metrics/http.server.requests | jq

# Change a logger level at runtime (no restart required)
curl -s -X POST http://localhost:8080/actuator/loggers/dev.snbv2 \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel": "DEBUG"}'

# Verify the logger change
curl -s http://localhost:8080/actuator/loggers/dev.snbv2 | jq
```

**Talking points:**

- Health endpoints are used by Docker and Kubernetes for liveness and readiness probes
- Metrics can be scraped by Prometheus or other monitoring systems
- Logger levels can be changed at runtime without restarting the service
- These endpoints work on every service (try ports 8081-8084 too)

## 8. Load Testing with Locust

Start the load generator to simulate realistic user traffic:

```bash
STRIPE_API_KEY=<your-test-key> docker compose --profile loadgen up -d
```

The load generator uses Locust to simulate users performing weighted actions:

| Action | Weight | Description |
|---|---|---|
| Browse catalog | 10 | View a random product (most common) |
| Add to cart | 3 | Add a random product to the cart |
| View cart | 3 | Visit the checkout page |
| Checkout | 2 | Complete a purchase with a test card |
| View payment history | 2 | Browse past payments |
| Visit home page | 1 | View the landing page |

**Talking points:**

- Traffic distribution mirrors realistic user behavior (more browsing than buying)
- The generator uses Stripe test cards so real charges are never created
- Watch the payment-history table grow as checkouts complete
- Use actuator metrics (`/actuator/metrics/http.server.requests`) to observe request rates across services

## 9. Canary Deployments with Kubernetes (Optional)

This section requires a Kubernetes cluster with Gloo Gateway installed.

### Deploy the Application

```bash
kubectl create ns cloud-cart
kubectl create secret generic -n cloud-cart stripe-secret \
  --from-literal=stripe-api-key=<your-test-key>
kubectl apply -n cloud-cart -f ./manifests/cloud-cart.yaml
```

The manifest deploys **two versions of the frontend** simultaneously:

- `frontend-v1` with `APP_VERSION=v1`
- `frontend-v2` with `APP_VERSION=v2`

The version is displayed in the navbar of the application, so you can visually identify which version you are hitting.

### Apply the Gateway API HTTPRoute

```bash
kubectl apply -n cloud-cart -f ./manifests/http-route.yaml
```

This creates an HTTPRoute for `www.cloudcart.com` that routes to `frontend-v1` by default.

### Demonstrate Traffic Splitting

Modify the HTTPRoute to split traffic between v1 and v2:

```yaml
rules:
  - backendRefs:
      - name: frontend-v1
        port: 8080
        weight: 80
      - name: frontend-v2
        port: 8080
        weight: 20
```

Apply the change and refresh the browser multiple times. The version in the navbar will alternate between v1 and v2 based on the weight distribution.

**Talking points:**

- Canary deployments allow gradual rollout of new versions
- Gateway API is the standard Kubernetes API for traffic management
- The version endpoint (`/api/version`) can be used to programmatically verify which version is responding
- Combined with the load generator, you can observe traffic distribution across versions

## 10. Tear Down

```bash
# Stop Docker Compose
docker compose --profile loadgen down

# (Optional) Remove Kubernetes resources
kubectl delete ns cloud-cart
```
