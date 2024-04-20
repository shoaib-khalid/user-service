# User Service

This project was generated with Spring Framework

## Development server

Run `mvn spring-boot:run` for a dev server. Navigate to `http://localhost:1201/`. The application will automatically reload if you change any of the source files.

## Environment Serve
```bash
# Development
$ mvn spring-boot:run

# Production
$ mvn clean install
```

## Build

From your local pc run
```bash
# make sure you are in correct working directory
[user@localpc]$ pwd
/home/user/path-to-your-project/user-service
...
..
[user@localpc]$ docker run -it --rm --platform linux/amd64 -v ./:/home/docker/Software -w /home/docker/Software openjdk:8-alpine sh
```

in docker shell
```bash
[user@docker]$ ./mvnw clean install -DskipTests
```

After the process done you should see folder `target` will have the following files & folder.

```bash
.
└── target/
    ├── classes
    ├── generated-sources
    ├── libs/
    ├── maven-archiver
    ├── maven-status
    └── user-service-<version>.jar
```

Exit the docker shell 

```bash
[user@docker]$ exit
...
..
...
[user@localpc]$
```

## Build Docker Image
To build the docker image, simply run `docker compose --profile prod build --push`, to build and push the docker image to docker registry.

```bash
[user@localpc]$ docker compose --profile prod build --push
```

> Note: If you having problem pushing docker image to docker registry. See [add docker registry](http://localhost)

## Container Architecture
This docker image is based on [openjdk:8-alpine](https://hub.docker.com/_/openjdk) image (see Dockerfile). It only require a web server (httpd:latest) to run the application. For more configuration, see https://hub.docker.com/_/openjdk

## Deployment
This project encourange developer to use docker to run the application for production.

Simply run :

```bash
docker compose up -d
```


## Further help
To get more help on the Java CLI use `java --help` or go check out the Java Official page.
