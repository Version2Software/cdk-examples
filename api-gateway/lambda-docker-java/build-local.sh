#!/bin/bash

cd app
mvn clean compile

docker rm myjava
docker build -t myjava .
cd ..

docker run -p 9000:8080 --name myjava myjava
curl -XPOST "http://localhost:9000/2015-03-31/functions/function/invocations" -d '{"payload":"hello world!"}'
