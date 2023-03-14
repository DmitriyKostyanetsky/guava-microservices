## Архитектура:


####Запуск приложения:
Для начала локально билдим все проекты и создаем images локально:
1. docker build -t discovery-server .
2. docker build -t guava-apigateway:latest .
3. docker build -t order-service:latest .
4. docker build -t delivery-service:latest .

Далее docker compose up -d
http://localhost:8181/eureka/web
