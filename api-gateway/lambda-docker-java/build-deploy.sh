#!/bin/bash

cd app
mvn clean compile

cd ..

cd cdk
cdk deploy
