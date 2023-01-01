#!/bin/sh

nginxIP=`docker network inspect --format '{{ range .Containers }}{{ .Name }} {{ .IPv4Address }} {{ "\n" }} {{ end }}' jenkins_jenkins_isolated  | grep nginx | cut -d" " -f3 | cut -d"/" -f1`

echo "NGINX IP Address is: $nginxIP"
[[ X"$nginxIP" == "X" ]] && echo "999" && exit 999

curl $nginxIP | grep -q '404 Not Found' && echo '404' || echo '1'