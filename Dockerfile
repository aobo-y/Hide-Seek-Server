FROM gradle:4.10-jre11

WORKDIR /usr/src/app

COPY . .

EXPOSE 8080

USER root

CMD [ "gradle", "appStart" ]
