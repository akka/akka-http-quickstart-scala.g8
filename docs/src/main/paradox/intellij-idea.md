IntelliJ IDEA
-------------

[IntelliJ](https://www.jetbrains.com/idea/) from JetBrains is one of the leading IDEs in the Java/Scala community, and it has excellent support for Akka Http. This section will guide you through setting up, testing and running the sample project.

## Setting up the project

Open IntelliJ and select File -> Open... and point to the directory where you have installed the sample project. There should be a pop-up like this:

![Open Project](images/idea-open-project.png)

Fill out the settings according to the above and press `OK` to import the project. If IntelliJ will warn about missing Scala SDK, it is only to follow the instructions to add support.

## Inspecting the code

If we open up the file `src/main/scala/com/lightbend/akka/http/sample/QuickstartServer.scala` we can see a lot of lines beginning with //# .... These lines are used as directives for this documentation. To get rid of these lines from the source code we can utilize the awesome Find/Replace functionality in IntelliJ. Select Edit -> Find -> Replace in Path.... Check the Regex box and add the following regex [//#].* and click on Replace in Find Window.... Select to replace all occurrences and voila the lines are gone!

## Running the application

Right click on the file `src/main/scala/com/lightbend/akka/http/sample/QuickstartServer.scala` and select Run 'QuickstartServer' and the output should look like this:

![Running Project](images/idea-running-project.png)

## Tutorial done!

Congratulations! We have now learned enough concepts to get started with building real-world Akka Http applications. Of course, there is plenty of more that we can do with Akka Http and the [documentation](http://doc.akka.io/docs/akka-http/current/scala/http/index.html) is a good starting point if there is something more you need.
