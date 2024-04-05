## Akka HTTP quickstart in Scala

The Akka family of projects is managed by teams at [Lightbend](https://lightbend.com/) with help from the community.

Please see the [quickstart guide](https://developer.lightbend.com/guides/akka-http-quickstart-scala/) for a
walk through the code.

You can use [Giter8][g8] to create your own project from the quickstart. Or, download and extract the zip file(https://example.lightbend.com/v1/download/akka-quickstart-scala?name=akka-quickstart-scala) to a convenient location.

Prerequisites:
- JDK 17
- sbt 1.4.x or higher

Open a console and run the following command to apply this template:
 ```
sbt new akka/akka-http-quickstart-scala.g8
 ```

This template will prompt for the following parameters. Press `Enter` if the default values suit you:
- `name`: Becomes the name of the project.
- `scala_version`: Specifies the Scala version for this project.
- `akka_http_version`: Specifies which version of Akka HTTP should be used for this project. Must be at least 10.6.0.
- `akka_version`: Specifies which version of Akka should be used for this project. Must be at least 2.9.0.
- `organization`: Specifies the organization for this project.

The template comes with the following sources:

* `QuickstartApp.scala` -- contains the main method which bootstraps the application 
* `UserRoutes.scala` -- Akka HTTP `routes` defining exposed endpoints
* `UserRegistry.scala` -- the actor which handles the registration requests
* `JsonFormats.scala` -- converts the JSON data from requests into Scala types and from Scala types into JSON responses

Once inside the project folder use the following command to run the code:
```
sbt run
```

Template license
----------------
Written in 2017 by Lightbend, Inc.

To the extent possible under law, the author(s) have dedicated all copyright and related
and neighboring rights to this template to the public domain worldwide.
This template is distributed without any warranty. See <http://creativecommons.org/publicdomain/zero/1.0/>.

[g8]: http://www.foundweekends.org/giter8/
