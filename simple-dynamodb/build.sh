#!/bin/bash

docker build -t simple-dynamodb .
docker run --rm --entrypoint cat simple-dynamodb /home/application/function.zip > build/function.zip