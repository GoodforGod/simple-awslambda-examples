#!/bin/bash

docker build -t simple-s3 .
docker run --rm --entrypoint cat simple-s3 /home/application/function.zip > build/function.zip