#!/bin/bash

docker build -t simple-awslambda-rest-api .
docker run --rm --entrypoint cat simple-awslambda-rest-api /home/application/function.zip > build/function.zip