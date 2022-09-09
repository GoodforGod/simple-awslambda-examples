#!/bin/bash

docker build -t simple-micronaut-aop .
docker run --rm --entrypoint cat simple-micronaut-aop /home/application/function.zip > build/function.zip