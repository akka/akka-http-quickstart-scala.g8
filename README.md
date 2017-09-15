A [Giter8][g8] template for Akka HTTP in a docker image

Prerequisites:
- JDK 8
- sbt 0.13.13 or higher
- Docker 

Open a console and run the following command to apply this template:
 ```
sbt -Dsbt.version=0.13.15 new https://github.com/araspitzu/akka-http-docker.g8
 ```

This template will prompt for the following parameters. Press `Enter` if the default values suit you:
- `name`: Becomes the name of the project.
- `scala_version`: Specifies the Scala version for this project.
- `akka_http_version`: Specifies which version of Akka HTTP should be used for this project.
- `akka_version`: Specifies which version of Akka should be used for this project.
- `organization`: Specifies the organization for this project.
