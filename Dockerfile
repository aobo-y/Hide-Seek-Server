FROM gradle:4.10-jdk8

WORKDIR /usr/src/app

COPY . .

EXPOSE 8080

USER root

CMD [ "gradle", "appStartWar" ]
