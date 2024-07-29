
# identity-management-service-stubs

Stub for the Identity Management Service used by The Integration Hub project.

For more information on the project please visit this space in Confluence:
https://confluence.tools.tax.service.gov.uk/display/AH/The+API+Hub+Home

## Requirements

This service is written in [Scala](http://www.scala-lang.org/) and [Play](http://playframework.com/), so needs at least a [JRE] to run.

## Dependencies
Beyond the typical HMRC Digital platform dependencies this service relies on:
- MongoDb

The full set of dependencies can be started using Service Manager and the group API_HUB_ALL.

You can view service dependencies using the Tax Catalogue's Service Relationships
section here:
https://catalogue.tax.service.gov.uk/service/identity-management-service-stubs

### MongoDb
This service uses MongoDb to store details of clients, secrets and scopes.

The MongoDb version should be 4.2 or 4.4 and is constrained by the wider platform not this service.

- Database: identity-management-service-stubs
- Collection: identities

## Using the service

### Running the application

To run the application use `sbt run` to start the service. All local dependencies should be running first.

Once everything is up and running you can access the application at

```
http://localhost:9000/identity-management-service-stubs
```

### Authentication
An Authorization header must be specified to call the /identity endpoints in
this service. This should specify a clientId and secret in this format:
- Authorization: Basic base64Encoded(clientId:secret)

The clientId and secret values at time of writing are:
- clientId: idms-stub-client-id
- secret: idms-stub-secret 

The Authorization header to specify based on these values is:
- Authorization: Basic aWRtcy1zdHViLWNsaWVudC1pZDppZG1zLXN0dWItc2VjcmV0

## Building the service
This service can be built on the command line using sbt.
```
sbt compile
```

### Unit tests
This microservice has many unit tests that can be run from the command line:
```
sbt test
```

### Integration tests
This microservice has some integration tests that can be run from the command line:
```
sbt it:test
```

## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
