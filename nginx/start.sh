#!/bin/bash
#support dynamic flask server based on predefine Environments :
echo "FLASK env is: ${JENKINS_SERVER_IP}:${FLASK_SERVER_PORT}"
cat /tmp/default.conf  | sed s/FLASK_SERVER_ADDR/${JENKINS_SERVER_IP}/ | sed s/FLASK_SERVER_PORT/${FLASK_SERVER_PORT}/ > /etc/nginx/conf.d/default.conf 
nginx -g 'daemon off;'
