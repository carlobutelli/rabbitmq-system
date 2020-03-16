Rabbit Queuing System
---------------------
Simple queuing system using RabbitMQ and Spring Boot.

---

Setup
-----

### Run RabbitMQ

First run the RabbitMQ container
```
docker-compose up -d
```
Access the UI at [RabbitMQ](http://localhost:15672) to check messages are successfully sent to Rabbit  
Username: `guest`
Password: `guest`

### Run the application

(mainclass: `com.queue`).

Either run this locally in maven using `mvn spring_boot:run` or using the run features from your IDE of choice.

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
export RABBIT_USER=guest
export RABBIT_PASSWORD=guest
export RABBIT_HOST=127.0.0.1
export RABBIT_PORT=5672
export RABBIT_VHOST=tech
```