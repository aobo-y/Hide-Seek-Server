# Hide-Seek-Server

## Start
Make sure you have installed [Gradle](https://gradle.org/). Start the server by

```shell
gradle appRunWar
```

### Data

Please download the files from this [link](https://drive.google.com/drive/folders/0BzHmOSvMbftoclhlSGVRcWE4Mmc) into the `/data` folder of the project root. They will be packed into and used by the built `war`.

### DB

For conveniences, you can use the following Docker command to start a MySql database required by the server. A GUI admin tool `adminer` will also run at `localhost:8081`.

```shell
docker-compose -f mysql_stack.yml up
```
