Job Aggregator
--------------

MicroProfile based job aggregator application (Initially, this is a task from JavaRush - [Java aggregator](https://javarush.ru/quests/lectures/questcollections.level08.lecture15)
(_CodeGym_ - [Java aggregator](https://codegym.cc/quests/lectures/questcollections.level08.lecture15))).

There are 2 projects, front-service actually call an endpoint of back-service.

* In the _[front-service](front-service)_ directory, you can find an application with the major parts of the application. This can be seen as the 'client'.
* In the _[back-service](back-service)_ directory, you can find endpoint which will be called by code within the client application. This can be seen as the 'backend'.

Have a look in the README file in each directory ([back readme](back-service/README.md), [front readme](front-service/README.md)) which describes how each project can be built and run.

Once both projects are built and started, open your browser at the following URL to launch the page:
```
GET http://localhost:9081/view/vacancies?position=java+developer&location=Санкт-Петербург
```
