# SepsisSolutions

Practical case of the [Datahack Bootcamp](https://www.datahack.es/formacion/bootcamp-full-stacks-developers/) 2018-2019.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. 

### Prerequisites

You need to have installed:

```
docker
docker-compose
```

### Launching the solution 

To test the solution just launch:

```
docker-compose up --build
```

## Testing the solution

Go to http://localhost:5000 to check the solution web page. You should see something like this once you have some patients in the DB:

![](./sepsissolutions.gif?raw=true)

### Create a new pruebas file:

From the root directory execute:

```
touch volumes/data/new_file
```

### Check the DB:

To check the DB status launch:

```
./tools/launch_cassandra_web.sh
```

and then go to http://localhost:3000


## Solution description

