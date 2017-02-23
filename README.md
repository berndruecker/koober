Koober
----------------

An uber data pipeline sample app.  Play Framework, Akka Streams, Kafka, Flink, Spark Streaming, and Cassandra.


Start Kafka:

    ./sbt kafkaServer/run

Web App:

1. Obtain an API key from [mapbox.com](https://www.mapbox.com/)
1. Start the Play web app: `MAPBOX_ACCESS_TOKEN=YOUR-MAPBOX-API-KEY ./sbt webapp/run`

Try it out:

1. Open the driver UI: [http://localhost:9000/driver](http://localhost:9000/driver)
1. Open the rider UI: [http://localhost:9000/rider](http://localhost:9000/rider)
1. In the Rider UI, click on the map to position the rider
1. In the Driver UI, click on the rider to initiate a pickup

Start Flink:

1. `./sbt flinkClient/run`
1. Initiate a few picks and see the average pickup wait time change

Start Cassandra:

    ./sbt cassandraServer/run

Start the Spark Streaming process:

1. `./sbt kafkaToCassandra/run`
1. Watch all of the ride data be micro-batched from Kafka to Cassandra


Start the Camunda workflow engine:

1. `./sbt camunda/run`
1. On the console you see that new rider requests start a process instance and driver pickups trigger the billing.

You can also start the Camunda Webapplication to use Cockpit to inspect what's going on. Therefor you need Maven installed:
1. `./mvn -f camunda-webapp/ spring-boot:run`
1. Open your browser at [http://localhost:9001/](http://localhost:9001/)
1. Login with `demo`/`demo`
1. Check [Cockpit](http://localhost:9001/app/cockpit/) for the `Koober Rider` process when a rider requested a ride and the driver has not yet picked up. You will see a running process instance.
1. Every third ride a payment failure is simulated and a Human Task is created. You can see them via the [Tasklist](http://localhost:9001/app/tasklist/).
