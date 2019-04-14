# SubscriptionManagementDashboard
A Dashboard for Managing  Subscription Service to Magazines.  
Include the ability to manage Subscribers
Include the ability to manage Magazines
Include the ability to generate Subscription Massive Campaign
Include the ability to generate Postal Code for each Subscription
Include the ability to match payments with Remote Poste Italiane Account 


Installation:

Prerequisite: postgresql 11
database must be created: by postgres user run:

-->createdb smd

To run test and build:

mvn install
you can run the system stand alone with

java -jar target/smd-0.0.1-SNAPSHOT.jar

To run inline with sample data use:

mvn spring-boot:run -Dspring-boot.run.arguments=--load.sample.data=true

Check application.properties for setting up the database using create and validate.
