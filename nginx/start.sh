#!/bin/bash
#support dynamic flask server:
#envsubst '$FLASK_SERVER_ADDR' < /tmp/default.conf > /etc/nginx/conf.d/default.conf && nginx -g 'daemon off;'
echo "JENKINS_SERVER_IP env is: $JENKINS_SERVER_IP"
cat /tmp/default.conf  | sed s/FLASK_SERVER_ADDR/${JENKINS_SERVER_IP}/ > /etc/nginx/conf.d/default.conf 
nginx -g 'daemon off;'
