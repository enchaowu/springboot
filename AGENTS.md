# AGENTS.md

## Cursor Cloud specific instructions

### Project overview

Spring Boot 2.1.5 monolith ("基金定投" fund investment demo) with Vue.js + Element UI frontend. Java 8 source level.

### Required services (all on localhost)

| Service   | Port | Credentials          | Start command                                                                                              |
|-----------|------|----------------------|------------------------------------------------------------------------------------------------------------|
| MySQL 8   | 3306 | root / root, db `test` | `sudo mysqld --user=mysql --port=3306 --socket=/var/run/mysqld/mysqld.sock --pid-file=/var/run/mysqld/mysqld.pid &` |
| Redis     | 6379 | password `root`      | `sudo redis-server --daemonize yes --requirepass root --port 6379`                                         |
| RabbitMQ  | 5672 | guest / guest        | `sudo rabbitmq-server -detached`                                                                           |

After MySQL starts, ensure the `test` database and tables exist:
```bash
mysql -u root -proot -h 127.0.0.1 -e "CREATE DATABASE IF NOT EXISTS test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -u root -proot -h 127.0.0.1 test < src/main/resources/db/dml_create_fund_02.sql
mysql -u root -proot -h 127.0.0.1 test < src/main/resources/db/dml_create_msg_log_03.sql
mysql -u root -proot -h 127.0.0.1 test < src/main/resources/db/dml_create_user_04.sql
```

### Build, test, run

All commands require `JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64` (the system default is JDK 21, but this project targets Java 8).

```bash
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
./mvnw clean compile -DskipTests   # build
./mvnw test                         # run tests (4 tests, requires all 3 services)
./mvnw spring-boot:run              # start dev server on port 8088
```

Web UI is at `http://localhost:8088/home`.

### Gotchas

- The Maven wrapper (`mvnw`) must be executable (`chmod +x mvnw`).
- MySQL's `dpkg --configure` fails inside the container because systemd is not running; start `mysqld` manually instead.
- The `ApiIdempotentInterceptor` and `AccessLimitInterceptor` are registered globally. Requests to `@ApiIdempotent` annotated endpoints require a Redis-stored token in the `token` header (get one via `GET /token`).
- RabbitMQ is technically optional for core fund CRUD but `RabbitConfig` is an eagerly loaded `@Configuration` bean, so the app may fail to start without it.
- SQL schema scripts use `DROP TABLE IF EXISTS`, so re-running them will wipe existing data.
