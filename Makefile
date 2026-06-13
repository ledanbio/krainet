.PHONY: build up down restart logs logs-auth logs-db ps clean clean-all shell-auth shell-db db-reset dev-build dev-run test

build:
	docker compose -f docker-compose.yml build

up:
	docker compose -f docker-compose.yml up -d

down:
	docker compose -f docker-compose.yml down

restart: down up

logs:
	docker compose -f docker-compose.yml logs -f

logs-auth:
	docker compose -f docker-compose.yml logs -f auth-service

logs-db:
	docker compose -f docker-compose.yml logs -f postgres

ps:
	docker compose -f docker-compose.yml ps

clean:
	docker compose -f docker-compose.yml down -v

clean-all: clean
	docker system prune -f

shell-auth:
	docker compose -f docker-compose.yml exec auth-service sh

shell-db:
	docker compose -f docker-compose.yml exec postgres psql -U auth_user -d krainet

db-reset:
	docker compose -f docker-compose.yml down -v
	docker compose -f docker-compose.yml up -d postgres

dev-build:
	cd AuthService && mvn clean package -DskipTests

dev-run:
	cd AuthService && mvn spring-boot:run

test:
	cd AuthService && mvn test