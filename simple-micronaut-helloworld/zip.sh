#!/bin/bash

docker build -t simple-micronaut-helloworld .
docker run --rm --entrypoint cat simple-micronaut-helloworld /home/application/function.zip > build/function.zip