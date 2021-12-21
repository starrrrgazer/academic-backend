FROM java:8

EXPOSE 8083

ADD Community-0.0.1-SNAPSHOT.jar community.jar
RUN bash -c 'touch /community.jar'
ENTRYPOINT ["java", "-jar", "/community.jar", "--spring.profiles.active=pro"]