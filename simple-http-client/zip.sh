#!/bin/bash

docker build -t simple-http-client .
docker run --rm --entrypoint cat simple-http-client /home/application/function.zip > build/function.zip