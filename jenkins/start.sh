#!/bin/bash

sed -i s/\$SERVER_IP/`hostname -I | cut -d' ' -f1`/ docker-compose.yml
hostname -I | cut -d' ' -f1 > /tmp/JENKINS_SERVER_IP
docker-compose up

