#!/bin/bash

docker build -t simple-helloworld .
docker run --rm --entrypoint cat simple-helloworld /home/application/function.zip > build/function.zip