FROM ubuntu:20.04

ARG SSH_PRIVATE_KEY

RUN apt update && apt install -y openssh-server
RUN apt -y install git vim openjdk-11-jdk maven curl

RUN mkdir /var/run/sshd
RUN mkdir /root/project
RUN echo 'root:123456789' | chpasswd
RUN sed -i 's/#*PermitRootLogin prohibit-password/PermitRootLogin yes/g' /etc/ssh/sshd_config
RUN sed -i 's@session\s*required\s*pam_loginuid.so@session optional pam_loginuid.so@g' /etc/pam.d/sshd

RUN mkdir /root/.ssh && chmod 0700 /root/.ssh
RUN echo "$SSH_PRIVATE_KEY" >> /root/.ssh/id_rsa && chmod 600 /root/.ssh/id_rsa
RUN ssh-keyscan github.com >> /root/.ssh/known_hosts

RUN git clone git@github.com:InhuJo/Software-engineering-wanted.git /root/project

ENV NOTVISIBLE="in users profile"
RUN echo "export VISIBLE=now" >> /etc/profile

EXPOSE 22
CMD ["/usr/sbin/sshd", "-D"]