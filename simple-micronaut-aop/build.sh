#!/bin/bash

docker build -t simple-awslambda-micronaut-aop .
docker run --rm --entrypoint cat simple-awslambda-micronaut-aop /home/application/function.zip > build/function.zip