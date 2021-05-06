FROM ubuntu:latest

RUN apt-get update && apt-get install -y openssh-server
RUN apt-get update
RUN apt -y install vim
RUN apt -y install openjdk-8-jdk
RUN apt -y install maven

RUN mkdir /var/run/sshd
RUN echo 'root:password' | chpasswd
RUN sed -i 's/#*PermitRootLogin prohibit-password/PermitRootLogin yes/g' /etc/ssh/sshd_config

RUN sed -i 's@session\s*required\s*pam_loginuid.so@session optional pam_loginuid.so@g' /etc/pam.d/sshd

ENV NOTVISIBLE="in users profile"
RUN echo "export VISIBLE=now" >> /etc/profile

RUN mkdir project
RUN cd project

RUN mkdir ~/.ssh
COPY ~/.ssh/id_rsa.pub ~/.ssh/

RUN git init
RUN git config --global user.name "sanghoon12"
RUN git config --global user.email "tkdgns971@naver.com"
RUN git clone git@github.com:InhuJo/Software-engineering-wanted.git
