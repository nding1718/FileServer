# FileServer
	Simple file server(SpringBoot) used to store some small files 

Use docker instance of MongoDB as backend database and Maven as the PM tool

Configurations
	docker stop mongodb
	docker rm   mongodb
	docker run --name mongodb -d -p 27017:27017 mongo:latest

Restart/Start
	mvn clean install
	java -jar target/FileServer-0.0.1-SNAPSHOT.jar

