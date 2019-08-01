#!/usr/bin/env bash

docker-compose stop
docker-compose down

cd agent/
./build.sh
cd ..
cd host/
./build.sh
cd ..
docker-compose up -d