#!/bin/bash

OPERATION=$1

function start() {
    docker-compose up -d --build
}

function stop() {
    docker-compose down
}

function log() {
    docker logs -f dockercompose_openmrs_1
}

case $OPERATION in
    start)
        start
        ;;
    stop)
        stop
        ;;
    log)
        log
        ;;
    *)
        echo "USAGE:"
        echo "start:  Starts all services"
        echo "stop:   Stops all services"
        echo "log:    Tails the OpenMRS log"
    ;;
esac