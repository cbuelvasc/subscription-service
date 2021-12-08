# Subscription Service

## REST API

* Business Domain

| Endpoint	                  | Method   | Req. body    | Status | Resp. body       | Description    		   	                      |
|:---------------------------:|:--------:|:------------:|:------:|:----------------:|:-----------------------------------------------:|
| `/api/subscriptions`        | `GET`    |              | 200    | Subscription[]   | Get all the subscription.                       |
| `/api/subscriptions`        | `POST`   | Subscription | 201    | Subscription     | Add a new subscription.                         |
|                             |          |              | 422    |                  | A subscription with the same id already exists. |
| `/api/subscriptions/{id}`   | `GET`    |              | 200    | Subscription     | Get the subscription with the given id.         |
|                             |          |              | 404    |                  | No subscription with the given id exists.       |
| `/api/subscriptions/{id}`   | `PUT`    | Subscription | 200    | Subscription     | Update the subscription with the given id.      |
|                             |          |              | 201    | Subscription     | Update a subscription with the given id.        |
|                             |          |              | 404    |                  | No subscription with the given id exists.       |
| `/api/subscriptions/{id}`   | `DELETE` |              | 204    |                  | Delete the subscription with the given id.      |
|                             |          |              | 404    |                  | No subscription with the given id exists.       |
| `/api/campaigns`            | `GET`    |              | 200    | Campaigns[]      | Get all the campaigns.                          |
| `/api/campaigns`            | `POST`   | Campaigns    | 201    | Campaigns        | Add a new campaign.                             |
|                             |          |              | 422    |                  | A campaigns with the same id already exists.    |
| `/api/campaigns/{id}`       | `GET`    |              | 200    | Campaigns        | Get the campaigns with the given id.            |
|                             |          |              | 404    |                  | No campaigns with the given id exists.          |
| `/api/campaigns/{id}`       | `PUT`    | Campaigns    | 200    | Campaigns        | Update the campaigns with the given id.         |
|                             |          |              | 201    | Campaigns        | Update a campaigns with the given id.           |
|                             |          |              | 404    |                  | No campaigns with the given id exists.          |
| `/api/campaigns/{id}`       | `DELETE` |              | 204    |                  | Delete the campaigns with the given id.         |
|                             |          |              | 404    |                  | No campaigns with the given id exists.          |

* Monitoring        

| Endpoint	                  | Method   | Status | Description    		   	                        |
|:---------------------------:|:--------:|:------:|:-----------------------------------------------:|
| `/actuator/info`            | `GET`    | 200    | Get info of service.                            |
| `/actuator/health`          | `GET`    | 200    | Get health of service.                          |
| `/actuator/health/readiness`| `GET`    | 200    | Get readiness state of service.                 |
| `/actuator/health/liveness` | `GET`    | 200    | Get liveness state of service.                  |
| `/actuator/flyway`          | `GET`    | 200    | Get config of flyway.                           |
| `/actuator/prometheus`      | `GET`    | 200    | Get Prometheus metrics of service.              |
| `/actuator/metrics`         | `GET`    | 200    | Get all metrics of service.                     |


## Useful Commands

| Gradle Command	                 | Description                                                  |
|:-----------------------------------|:-------------------------------------------------------------|
| `./gradlew bootRun`                | Run the application.                                         |
| `./gradlew build`                  | Build the application.                                       |
| `./gradlew build -x test`          | Build the application and omit test.                         |
| `./gradlew test`                   | Run tests.                                                   |
| `./gradlew bootJar`                | Package the application as a JAR.                            |
| `./gradlew bootBuildImage`         | Package the application as a container image.                |
| `./gradlew bootBuildImage -x test` | Package the application as a container image and omit test.  |

After building the application, you can also run it from the Java CLI:

```bash
java -jar build/libs/subscription-service-0.0.1-SNAPSHOT.jar
```
