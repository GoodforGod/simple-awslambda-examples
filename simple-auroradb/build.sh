#!/bin/bash

docker build -t simple-awslambda-auroradb .
docker run --rm --entrypoint cat simple-awslambda-auroradb /home/application/function.zip > build/function.zip
