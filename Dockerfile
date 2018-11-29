FROM gradle

WORKDIR /usr/src/app

COPY . .

EXPOSE 8080

WORKDIR /usr/src/app/source

CMD [ "gradle", "appRunWar" ]
