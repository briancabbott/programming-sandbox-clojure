# Pet Store Example

This is a simple version of pet store app, Swagger UI on the front-end
and Vase app on the back-end. This app is for Vase newcommers to
understand how Vase app is configured. If you want to learn more to
build a real app, [a full version of pet store app](../petstore-full),
is a good example.


## Datomic Setup

This pet-store simple version uses Datomic memory database. The
dependency to Datomic is defined in Vase. If you want to use Datomic
Pro's transactor, see
[project.clj-with-datomic-pro](./project.clj-with-datomic-pro) for
project setup.  Also, change database uri defined in
`resources/petstore-simple.edn` accordingly, for example,

```clojure
:vase.descriptor/datomic-uri "datomic:dev://localhost:4334/pet-store"
```

When you create a docker image with Datomic Pro,
you may need to fix a dependency.

## Getting Started

0. Start a Datomic Free transactor.
1. Start the application: `lein run`
2. Go to [localhost:8080/index.html](http://localhost:8080/index.html) to see Swagger UI.
3. Click `default`
    - GET /pets will get all pets

      click "Try it out!" button.

    - POST /pets will transact new pet(s)

      click "Examble Value" and edit id, name and tag values, then click "Try it out!" button.

    - GET /pet/{id} will get a single pet by id

      input id, then click "Try it out!" button

4. Read your app's spec at resources/pet-store.edn and source code at src/pet_store/service.clj.


## Port number

A port number is defined in both Swagger UI and Vase app.
If you want to use other port number:

- Change `"host": "localhost:8080",` in `resources/public/v1/petstore-simple.json` for Swagger UI.
- Change `::http/port 8080` in `src/pet_store/services.clj` for Vase app.


## [Docker](https://www.docker.com/) container support

1. Build an uberjar of your service: `lein uberjar`
2. Build a Docker image: `sudo docker build -t pet-store .`
3. Run your Docker image: `docker run -p 8080:8080 pet-store`


## Configuration (Pedestal)

To configure logging see config/logback.xml. By default, the app logs to stdout and logs/.
To learn more about configuring Logback, read its [documentation](http://logback.qos.ch/documentation.html).
