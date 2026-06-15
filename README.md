# Тестовое крайнет
***

## Для запуска приложения необходимо
1. Создать **.env** файл. Вставить свою почту и пароль приложения для этой почты

```bash
DB_NAME=krainet
DB_USERNAME=krainet
DB_PASSWORD=asdq-123a-das2-gfe&

JWT_SECRET=62bf83f7d199de0009192b2065084c43dc8aabff34e5ef0ba9a422cdacf84455
JWT_ACCESS_EXPIRATION=900000
JWT_REFRESH_EXPIRATION=604800000

SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=**YOUR_MAIL**
SMTP_PASSWORD=**YOUR_APP-PASSWORD**

ADMIN_USERNAME=admin
ADMIN_EMAIL=**YOUR_MAIL**
ADMIN_PASSWORD=**YOUR_PASSWORD**

SERVICE_API_KEY=49a1eb1f-10c2-4649-bae1-87b610eb
```
2. Запустить билд приложения через Make

```bash
make build
```

3. Запустить приложение через Make

```bash
make up
```

4. Перейти в сваггер <http://localhost:8081/swagger-ui/index.html>

## Просмотр логов
```bash
make logs
make logs-auth
make logs-db
```