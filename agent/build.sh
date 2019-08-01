#!/usr/bin/env bash

docker image rm osmp4j-agent
gradle build
docker build -t osmp4j-agent .