FROM openjdk:8-jdk-alpine
MAINTAINER ang00/websocket zhang10092009@hotmail.com
VOLUME /tmp
ADD websocket 0.0.1-SNAPSHOT app.jar/
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' >/etc/timezone
CMD ["java", "Xmx1G", "-jar", "/tmp/app.jar"]
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]