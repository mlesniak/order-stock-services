# Overview

This is a simple order service that provides a REST API to create and retrieve product orders via a basic REST API. Many design remark have been added directly to the code as comments, starting with `REMARK`. Please have a look at the code to see the reasoning behind the design decisions.

Note that we have intentionally not configured a production profile, i.e. the application is not ready to be deployed to production, but shall just serve as a example.

## Build and Run

This is a standard gradle project, i.e. can be run via

```bash
    gradle compile
    gradle spring-boot:run
```

Note that this starts a MongoDB as well, therefore you need to have a running docker daemon.

## OpenAPI documentation

OpenAPI documentation in JSON format can be found at http://localhost:8081/v3/api-docs. The Swagger UI is available at http://localhost:8081/swagger-ui/index.html.

## General remarks

The integration tests use [Testcontainers](https://testcontainers.com/), i.e. you need a running docker daemon to run all tests. In a future version this can also be hidden behind a profile or flag.