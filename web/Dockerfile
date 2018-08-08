FROM nginx:stable

MAINTAINER Jack Hay "https://github.com/jackHay22"

ENV SERVER_PORT=80
EXPOSE $SERVER_PORT

RUN rm -v /etc/nginx/nginx.conf
ADD nginx.conf /etc/nginx/

ADD web_content /usr/share/nginx/html/
