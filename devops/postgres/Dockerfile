FROM postgres

WORKDIR /opt/finalysis/postgres

# 使用 aliyun 镜像源
RUN cp /etc/apt/sources.list /etc/apt/sources.list_backup

RUN sed -i 's/http:\/\/deb.debian.org/http:\/\/mirrors.aliyun.com/g' /etc/apt/sources.list
RUN sed -i 's/http:\/\/security.debian.org/http:\/\/mirrors.aliyun.com/g' /etc/apt/sources.list

ADD sql sql
ADD script script

RUN apt update
RUN apt -y install vim