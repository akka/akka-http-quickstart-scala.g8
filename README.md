A [Giter8][g8] template for Akka HTTP in Scala

Prerequisites:
- JDK 8
- sbt 0.13.13 or higher

Open a console and run the following command to apply this template:
 ```
sbt -Dsbt.version=0.13.15 new https://github.com/akka/akka-http-scala-seed.g8
 ```

This template will prompt for the following parameters. Press `Enter` if the default values suit you:
- `name`: Becomes the name of the project.
- `scala_version`: Specifies the Scala version for this project.
- `akka_http_version`: Specifies which version of Akka HTTP should be used for this project.
- `akka_version`: Specifies which version of Akka should be used for this project.
- `organization`: Specifies the organization for this project.

This template comes with 2 different ways to implement an Akka HTTP server, along with their respective tests.

- WebServer: This is the manual way to start a server. For this case, routes are defined in separated classes: the object `com.example.routes.BaseRoutes`
and the trait `com.example.routes.SimpleRoutes`.
- WebServerHttpApp: This server is started using the recently introduced `HttpApp`, which bootstraps a server with just a few lines.
For this particular case, the routes are defined in the same class, but you can also define them in separated traits or objects.

Once inside the project folder, to run this code, you can use any of the following commands:
```
sbt run
```
This will prompt you with the 2 different servers, so you can pick the one you prefer.

Alternatively, you can run:

```
sbt runMain com.example.WebServer
```
or
```
sbt runMain com.example.WebServerHttpApp
```
to directly run the specified file.


Template license
----------------
Written in 2017 by Lightbend, Inc.

To the extent possible under law, the author(s) have dedicated all copyright and related
and neighboring rights to this template to the public domain worldwide.
This template is distributed without any warranty. See <http://creativecommons.org/publicdomain/zero/1.0/>.

[g8]: http://www.foundweekends.org/giter8/
