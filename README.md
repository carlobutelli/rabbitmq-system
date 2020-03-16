Rabbit Queuing System
---------------------
Simple queuing system using RabbitMQ and Spring Boot.

---

Setup
-----

### Run RabbitMQ

First run the RabbitMQ management container
```
docker-compose up -d
```
Access [RabbitMQ UI](http://localhost:15672) to check messages are successfully sent to Rabbit  
Username: `guest`
Password: `guest`

### Run the application
Application contains two modules:
- `publisher` in charge of publishing messages to a specified exchange
- `worker` listening on a given queue ready to consume messages

Either run this locally in maven using `mvn -T 2 spring-boot:run` or run each module from its root dir
```
cd publisher && mvn spring-boot:run
```
```
cd worker && mvn spring-boot:run
```

### Swagger

To consume the API visit [SwaggerUI](http://localhost:8080/swagger)

---

Standard RabbitMQ Flow
----------------------
- The producer publishes a message to the exchange
- The exchange receives the message routes the message to one or more queues
    - Binding must be set up between the queue and the exchange
- The messages stay in the queue until they are handled by a consumer
- The consumer handles the message

---

Environment variables
---------------------

The app requires to set some env variables in order to perform all its operations correctly

```bash
export RABBITMQ_USER=guest
export RABBITMQ_PASSWORD=guest
export RABBITMQ_HOST=127.0.0.1
export RABBITMQ_PORT=5672
export RABBITMQ_VHOST=tech
export RABBITMQ_QUEUE=hello
export RABBITMQ_EXCHANGE=greetings
export RABBITMQ_BINDING_LIST="got_success, got_failure"
export RABBITMQ_CONCURRENCY=2
export RABBITMQ_MAX_SERVER_WAIT_TIME=1
```