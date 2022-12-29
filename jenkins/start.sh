#!/bin/bash

sed -i s/\$SERVER_IP/`hostname -I | cut -d' ' -f1`/ docker-compose.yml
docker-compose up

