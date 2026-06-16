# Тестовое крайнет
***

## Для запуска приложения необходимо
1. Создать **.env** файл. Вставить свою почту(в поле YOUR_MAIL) и пароль приложения для этой почты(в поле YOUR_APP-PASSWORD). Почту на которую будут приходить сообщения вставить в поле ADMIN_EMAIL

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
ADMIN_EMAIL=**ADMIN_EMAIL**
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

## Обоснование выбора способа обмена между микросервисами Kafka+Feign
1. Kafka используется для доставки event с auth-service на notification-service. Благодаря этому auth-service возвращает ответ сразу не дожидаясь отправки письма на почту админов(Асинхронность)
2. Feign В то же время для получения почт админов, на которые будут отправлять письма, должен приходить сразу, а не ожидать в event. Также создание новых event для пересылки больших данных замедляет работу сервера(Быстродействие)
3. Kafka обеспечит получение email гарантированно, даже если сервис упадет(при следующем поднятии контейнера). В то же время использование только Feign клиента приведет к падению и auth-service тоже(Отказоустойчивость)
4. Данная комбинация позволяет расширять функционал и обеспечивает возможность масштабирования проекта(Масштабирование)

## Просмотр логов
```bash
make logs
make logs-auth
make logs-db
```

