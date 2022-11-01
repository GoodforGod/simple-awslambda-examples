#!/bin/bash

docker build -t simple-auroradb .
docker run --rm --entrypoint cat simple-auroradb /home/application/function.zip > build/function.zip
