#!/bin/bash

gradlew shadowJar
docker build -t simple-awslambda-helloworld .
docker run --rm --entrypoint cat simple-awslambda-helloworld /home/application/function.zip > build/function.zip