FROM jenkinsci/blueocean:1.25.5

USER root

RUN apk add --update --no-cache docker-compose

COPY start.sh ./start.sh
COPY nginxVerification.sh /tmp/nginxVerification.sh 

RUN chmod 755 ./start.sh /tmp/nginxVerification.sh 

RUN ./start.sh

USER jenkins
