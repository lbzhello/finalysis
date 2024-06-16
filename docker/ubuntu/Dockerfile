FROM ubuntu

WORKDIR /opt/finalysis/ubuntu

# 使用 aliyun 镜像源
RUN cp /etc/apt/sources.list /etc/apt/sources.list_backup

RUN sed -i 's/http:\/\/archive.ubuntu.com/http:\/\/mirrors.aliyun.com/g' /etc/apt/sources.list
RUN sed -i 's/http:\/\/security.ubuntu.com/http:\/\/mirrors.aliyun.com/g' /etc/apt/sources.list

# 安装必要工具
RUN apt update
RUN apt -y install curl && apt -y install vim && apt -y install net-tools \
    && apt install -y wget \
    && apt install -y openjdk-21-jdk

#RUN wget https://download.java.net/java/GA/jdk20.0.2/6e380f22cbe7469fa75fb448bd903d8e/9/GPL/openjdk-20.0.2_linux-x64_bin.tar.gz

#ADD libs libs
#RUN mkdir /opt/openjdk-20.0.2 \
#    && tar -zxvf libs/openjdk-20.0.2_linux-x64_bin.tar.gz --strip-components 1 -C /opt/openjdk-20.0.2 \
#    && export PATH=$PATH:/opt/openjdk-20.0.2/bin
