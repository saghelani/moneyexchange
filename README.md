# Money Exchange REST API

This is a REST API that allows you to transfer money between two accounts. Currently it only supports transferring money in one currency.

This application can run in a standalone mode, without running on a dedicated container or server. It currently uses a in memory database using a H2 database.

On startup the database is empty and so it has to be populated with the required data using the approriate REST calls (see below).

The application has been developed using a TDD approach and using the DropWizard framework.

The application is able to support concurrent requests, as all changes to the database are made using their own local Hibernate sessions by using the @UnitOfWork annotation.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

Requires the following software installed in your development environment:

```
Java 8 JDK
Maven
```

### Installing

After cloning the git repo, you can build the project and run the tests using the following maven command in the root directory of the repository:

```
mvn clean install
```

### Running

After building the project, you can run the application using the fat jar generated in the target directory. You need to specify the config file to use on the command line (an example config file to use can be found in the resources directory).

The application listens on port 8080 by default.

```
java -jar moneyexchange-1.0-SNAPSHOT.jar server resources/config.yml
```

### Example use cases

Error codes used:

* 404 - Account/Transaction Not Found
* 400/422 - Bad Request/Unprocessable Entity (i.e. the request failed validation)
* 500 - Internal Server Error

#### Create an account

Example request:

    POST /accounts
    {
        "balance" : {
            "currency": "GBP",
            "value" : 5
        },
        "holder" : "John",
        "type" : "current"
    }

Example response:

    HTTP 201 Created
    {
        "id": 1,
        "holder" : "John",
        "balance" : {
            "currency": "GBP",
            "value" : 5
        },
        "type" : "current"
    }    

#### Update an account

Example request:

    POST /accounts
    {
        "id" : 1,
        "balance" : {
            "currency": "GBP",
            "value" : 10
        },
        "holder" : "John",
        "type" : "current"
    }

Example response:

    HTTP 201 Created
    {
        "id": 1,
        "holder" : "John",
        "balance" : {
            "currency": "GBP",
            "value" : 10
        },
        "type" : "current"
    }   
    
#### List all accounts

Example request:

    GET /accounts

Example response:

    HTTP 200 OK
    [{
        "id" : 1,
        "balance" : {
            "currency": "GBP",
            "value" : 10
        },
        "holder" : "John",
        "type" : "current"
    }]

#### Get account by Id

Example request:

    GET /accounts/1

Example response:

    HTTP 200 OK
    {
        "id" : 1,
        "balance" : {
            "currency": "GBP",
            "value" : 10
        },
        "holder" : "John",
        "type" : "current"
    }

#### Execute a transaction

Example request:

    POST /transactions
    {
        "amount" : {
            "currency" : "GBP",
            "value" : 4
        },
        "remitterAccountId" : 1,
        "beneficiaryAccountId" : 2
    }

Example response:

    HTTP 200 OK
    {
        "id": 3,
        "remitterAccountId": 1,
        "beneficiaryAccountId": 2,
        "amount": {
            "currency": "GBP",
            "value": 4
        },
        "status": "EXECUTED"
    }

#### Get all transactions

Example request:

    GET /transactions

Example response:

    HTTP 200 OK    
    [{
        "id": 3,
        "remitterAccountId": 1,
        "beneficiaryAccountId": 2,
        "amount": {
            "currency": "GBP",
            "value": 4
        },
        "status": "EXECUTED"
    }]
    
#### Get transaction by ID

Example request:

    GET /transactions/3

Example response:

    HTTP 200 OK    
    {
        "id": 3,
        "remitterAccountId": 1,
        "beneficiaryAccountId": 2,
        "amount": {
            "currency": "GBP",
            "value": 4
        },
        "status": "EXECUTED"
    }


## Deployment

For deploying to live systems, you just need to deploy the fat jar and generate a bash script to run the above command.

## Built With

* [Dropwizard](http://www.dropwizard.io/1.0.2/docs/) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management

## Future improvements

* Add automated integration tests
* Support multiple currencies
* Add Guice integration
