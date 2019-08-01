#!/usr/bin/env bash

docker image rm osmp4j-host
gradle build
docker build -t osmp4j-host .