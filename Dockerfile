FROM gradle

WORKDIR /usr/src/app

COPY . .

EXPOSE 8080

USER root

CMD [ "gradle", "appRunWar" ]
