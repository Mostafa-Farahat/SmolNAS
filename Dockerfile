FROM tomcat

#copy source files from host
COPY ./src /usr/local/SmolNAS/src
COPY ./web /usr/local/SmolNAS/web
COPY ./build.xml /usr/local/SmolNAS/

#set env variables needed for SmolNAS
RUN mkdir /opt/NAS_DATA
ENV NAS_DATAROOT=/opt/NAS_DATA/

#install ant for building
ENV ANT_HOME=/opt/ant
ENV PATH=/opt/ant/bin:/bin:/usr/bin:/usr/local/bin:/usr/local/tomcat/bin
RUN mkdir /opt/ant
RUN curl https://dlcdn.apache.org//ant/binaries/apache-ant-1.10.15-bin.tar.gz --output /opt/ant.tar.gz
RUN tar -xzf /opt/ant.tar.gz -C /opt/ant --strip-components=1

#build from source
WORKDIR /usr/local/SmolNAS
RUN /opt/ant/bin/ant deploy

EXPOSE 8080






