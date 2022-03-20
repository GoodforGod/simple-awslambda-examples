#!/bin/bash

docker build -t simple-awslambda-micronaut-helloworld .
docker run --rm --entrypoint cat simple-awslambda-micronaut-helloworld /home/application/function.zip > build/function.zip