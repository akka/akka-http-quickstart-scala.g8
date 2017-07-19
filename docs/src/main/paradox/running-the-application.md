Running the application
-----------------------

You can run the Hello World application from the command line or an IDE. The final topic in this guide describes how to run it from IntelliJ IDEA. However, before we get there, let’s take a quick look at the build tool: sbt.

## The build files

sbt uses a build.sbt file to handle the project. This project’s build.sbt file looks like this:

@@snip [build.sbt]($g8root$/build.sbt)

## Running the project

We run the application from a console/terminal window and enter the following commands:

OSX/Linux
: ```
$ cd akka-http-quickstart-scala
$ ./sbt
> run
```

Windows
: ```
$ cd akka-http-quickstart-scala
$ sbt.bat
> run
```

The output should look like this:

```
...
[info] Running com.lightbend.akka.http.sample.QuickstartServer
Server online at http://localhost:8080/
Press RETURN to stop...
```

## Interacting with the application

The Akka HTTP server is now running, and we will use the [cURL](https://en.wikipedia.org/wiki/CURL) command to test the application. If you prefer to use your browser to test the service then a tool like [RESTClient](http://restclient.net/) may be good to install.

Open another console/terminal window to investigate the functionality of the application.

We start by looking at the existing users (there should be none as we just launched the application):

```
$ curl http://localhost:8080/users
{"users":[]}
```

The next step is to add a couple of users:
```
$ curl -H "Content-type: application/json" -X POST -d '{"name": "MrX", "age": 31, "countryOfResidence": "Canada"}' http://localhost:8080/user
User MrX created.

$ curl -H "Content-type: application/json" -X POST -d '{"name": "Anonymous", "age": 55, "countryOfResidence": "Iceland"}' http://localhost:8080/user
User Anonymous created.

$ curl -H "Content-type: application/json" -X POST -d '{"name": "Bill", "age": 67, "countryOfResidence": "USA"}' http://localhost:8080/user
User Bill created.
```

We can try to retrieve user information for various users now:

```
$ curl http://localhost:8080/user/MrX
{"name":"MrX","age":31,"countryOfResidence":"Canada"}

$ curl http://localhost:8080/user/SomeUnknownUser
User SomeUnknownUser is not registered.
```

Now, when we inquire the system for all existing users it looks like this:

```
$ curl http://localhost:8080/users
{"users":[{"name":"Anonymous","age":55,"countryOfResidence":"Iceland"},{"name":"MrX","age":31,"countryOfResidence":"Canada"},{"name":"Bill","age":67,"countryOfResidence":"USA"}]}
```

Next, we should make sure that the delete functionality works as expected:

```
$ curl -X DELETE http://localhost:8080/user/Bill
User Bill deleted.

$ curl http://localhost:8080/user/Bill
User Bill is not registered.

$ curl http://localhost:8080/users
{"users":[{"name":"Anonymous","age":55,"countryOfResidence":"Iceland"},{"name":"MrX","age":31,"countryOfResidence":"Canada"}]}
```

We have now tried all the functionality available in this sample. The next step is to see how we can use an IDE to work with the application.
