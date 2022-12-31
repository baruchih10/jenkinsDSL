#!/bin/bash
#support dynamic flask server based on predefine Environments :
echo "FLASK env is: ${FLASK_SERVER_ADDR}:${FLASK_SERVER_PORT}"
cat /tmp/default.conf  | sed s/FLASK_SERVER_ADDR/${FLASK_SERVER_ADDR}/ | sed s/FLASK_SERVER_PORT/${FLASK_SERVER_PORT}/ > /etc/nginx/conf.d/default.conf 
nginx -g 'daemon off;'
