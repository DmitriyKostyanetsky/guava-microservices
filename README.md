#### Запуск всего приложения в докере:
В корне проекта /IdeaProjects/guava-microservices$ выполнить 
* docker compose up -d
* eureka server доступен по http://localhost:8181/eureka/web
* К сожалению, swagger не настроен :( http://localhost:8181/swagger-ui.html. 
Для тестирования приложения в корне лежит коллекция вызовов для postman(Guava.postman_collection.json). 
Ее можно импортировать.

#### Запуск приложения локально и поднятие в докере postgres и кластер кафки:
Так же в корне проекта ~/IdeaProjects/guava-microservices$ 
* docker compose -f docker-compose-db-and-kafka.yml up -d
* eureka server доступен по http://localhost:8181/eureka/web
* swagger OrderService http://localhost:8082/swagger-ui/index.html
* swagger DeliveryService http://localhost:8083/swagger-ui/index.html 
(для сервиса сделан super user с правами админа. Логин: admin Пароль: admin)
