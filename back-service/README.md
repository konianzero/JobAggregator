Job Aggregator Back-End
-----------------------

Aggregate java vacancies in Saint-Petersburg from websites:
- [HeadHunter](https://hh.ru/search/vacancy) - [HHStrategy](src/main/java/org/aggregator/job/model/strategy/HHStrategy.java)
- [Хабр Карьера](https://career.habr.com/vacancies) - [CareerHabrStrategy](src/main/java/org/aggregator/job/model/strategy/CareerHabrStrategy.java)
- [Работа.ру](https://www.rabota.ru/vacancy) - [RabotaRuStrategy](src/main/java/org/aggregator/job/model/strategy/RabotaRuStrategy.java)
- [SuperJob](https://www.superjob.ru/vacancy) - [SuperJobStrategy](src/main/java/org/aggregator/job/model/strategy/SuperJobStrategy.java)

Information about vacancies processing is logged by a [LogInterceptor](src/main/java/org/aggregator/job/util/interceptor/LogInterceptor.java) and [@Log](src/main/java/org/aggregator/job/util/interceptor/Log.java) annotation.

---

### Requirements

- JDK 17

---

### Launch
```
./gradlew libertyDev
```

You can check it with [endpoints.http](endpoints.http).

---

### API documentation

[Swagger Api Documentation](http://localhost:9080/openapi/ui/)

URL: [http://localhost:9080/api](http://localhost:9080/api)

| API          | Method | Description                  | URL                                                               |
|--------------|--------|------------------------------|-------------------------------------------------------------------|
| Back Service | GET    | Search vacancies in the city | {URL}/vacancies?position={programmerPosition}&location={cityName} |   

---