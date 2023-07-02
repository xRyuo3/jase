#!/usr/bin/bash

emqx_id=$(docker ps --filter "name=emqx" -aq)

if [ "$1" == "start" ]; then
    if [ -n "$emqx_id" ]; then
        echo "The Server is already running."
    else
        echo "Starting the MQTT Server ..."
        docker run -d --name emqx -p 1883:1883 -p 8083:8083 -p 8084:8084 -p 8883:8883 -p 18083:18083 emqx/emqx
    fi
elif [ "$1" == "stop" ]; then
    if [ -n "$emqx_id" ]; then
        echo "Stopping the Server ..."
        docker kill $(docker ps --filter "name=emqx" -aq)
        docker rm $(docker ps --filter "name=emqx" -aq)
    else
        echo "The Server is already stopped."
    fi
elif [ "$1" == "status" ]; then
    if [ -n "$emqx_id" ]; then
        echo "The Server is running."
    else
        echo "The Server is down."
    fi
else
    echo "Invalid argument. Please provide 'start', 'stop' or 'status'."
fi
