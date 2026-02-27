SERVICES := catalog frontend orders payment-history payments

.DEFAULT_GOAL := help

.PHONY: help build test clean docker-up docker-down docker-logs \
        $(addprefix build-,$(SERVICES)) $(addprefix test-,$(SERVICES))

help: ## Print available targets
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  %-20s %s\n", $$1, $$2}'

build: ## Build all services (skip tests)
	./mvnw clean package -DskipTests

test: ## Run all tests
	./mvnw test

clean: ## Clean all build artifacts
	./mvnw clean

define SERVICE_TARGETS
build-$(1): ## Build $(1) (skip tests)
	./mvnw clean package -pl $(1) -DskipTests

test-$(1): ## Run $(1) tests
	./mvnw test -pl $(1)
endef

$(foreach svc,$(SERVICES),$(eval $(call SERVICE_TARGETS,$(svc))))

docker-up: ## Start all services with Docker Compose
	docker compose up --build -d

docker-down: ## Stop all services
	docker compose down

docker-logs: ## Follow Docker Compose logs
	docker compose logs -f
