The main class
----------------

Let's dissect the main class, `QuickstartServer`. We make this class runnable by extending `App` (we will discuss the trait `JsonSupport` later):

@@snip [QuickstartServer.scala]($g8src$/scala/com/lightbend/akka/http/sample/QuickstartServer.scala) { #main-class }

Now that we have a class to run we should add some Akka HTTP fundamentals with which we will build our RESTful web service:

* define routes bound to endpoints and HTTP directives
* create a server bound to an IP and port that will handle all requests
* add error handling for when something goes wrong

Let us take a look at each of these steps here below.

## Routes

For our service we want to define the following endpoints:

| Path        | Http directive  | Intent             | Returns              |
|-------------|-----------------|--------------------|----------------------|
| /user       | POST            | Create a new user  | Confirmation message |
| /user/$ID   | GET             | Retrieve a user    | JSON payload         |
| /user/$ID   | DELETE          | Remove a user      | Confirmation message |
| /users      | GET             | Retrieve all users | JSON payload         |

Akka HTTP provides a [domain-specific language](https://en.wikipedia.org/wiki/Domain-specific_language) (DSL) to simplify the routes/endpoints definition. Each route is composed of one or more `akka.http.scaladsl.server.Directives`, e.g. `path`, `get`, `post`, `complete`, etc.

### Creating a new user

Let us take a look at the source code for the first endpoint, the `/user` URI with a `POST` directive, used to create a new user:

@@snip [QuickstartServer.scala]($g8src$/scala/com/lightbend/akka/http/sample/QuickstartServer.scala) { #user-post }

The snippet above contains a couple of interesting building blocks:

* `path` : matches against the incoming URI, in this case, we want to get all requests that matches `user`.
* `post` : matches against the incoming Http directive, in this case, we are matching against `POST`.
* `entity(as[User])` : automatically converts the incoming payload, in this case, we expect JSON, into an entity. We will look more at this functionality in the @ref:[JSON](json.md) section.
* `complete` : used to reply back to the request. The `StatusCodes.Created` is translated to Http response code 201. We also send back information to the caller in the form of a string.    

When this `Route` is called, we want to create a new user, and we do so by sending a message to the actor `userRegistryActor`. We will look at the implementation of this actor later.

### Retrieving and removing a user

Next we need to define how to retrieve and remove a user, i.e. for the case when the URI `/user/$ID` is used where `$ID` is the id of the user:

@@snip [QuickstartServer.scala]($g8src$/scala/com/lightbend/akka/http/sample/QuickstartServer.scala) { #user-get-delete }

This Route snippet contains a couple of interesting concepts:

* `path("user" / Segment) { => user` : this bit of code matches against URIs of the exact format `/user/$ID` and the `Segment` is automatically extracted into the `user` variable so that we can get to the value passed in the URI. For example `/user/Bruce` will populate the `user` variable with the value "Bruce."
* `get` : matches against the incoming Http directive.

Let's break down the "business logic" in the first Route:

@@snip [QuickstartServer.scala]($g8src$/scala/com/lightbend/akka/http/sample/QuickstartServer.scala) { #retrieve-user-info }

The code above uses the so-called [ask](http://doc.akka.io/docs/akka/current/scala/actors.html#send-messages) in Akka. This will send a message asynchronously and return a `Future` representing a _possible_ reply. The code above maps the reply to the type `UserInfo`. When the future completes, it will use the second part of the code to evaluate to either `Success`, with or without a result, or a `Failure`. Regardless of the outcome of the future, we should return something to the requester, and we do so by using the `complete` directive with an appropriate response code and value.

The remaining directives used for this route are:

* `~` : fuses `Route`s together - this will become more apparent when you see the complete `Route` definition here below.
* `delete` : matches against the Http directive `DELETE`.

The "business logic" for when deleting a user is straight forward; send an instruction about removing a user to the user registry actor and return a status code to the client (which in this case is `StatusCodes.OK`, i.e. Http status code 200)

### Retrieving all users

Finally we should implement functionality to retrieve all registered users:

@@snip [QuickstartServer.scala]($g8src$/scala/com/lightbend/akka/http/sample/QuickstartServer.scala) { #users-get }

This code is based on the same structure as the code above, send a message to the user registry actor using an `ask` and pass on the `Future` to the `complete` method.

Why do we not use an `onComplete` as we did for when retrieving a particular user? The difference is that when we use `GetUsers` there will always be something returned; an empty list means that there are no registered users. However, when we asked for a particular user ID, there might be the case that there is no such user recorded and we need a way to tell this to the client. When sent a `GetUser(name)` the user registry actor therefore send back an `Option[UserInfo]`. It could be that there is a `Some(UserInfo)` or a `None` which indicates that there was no match in the registry.

### The complete Route

Below is the complete `Route` definition used in the sample application:

@@snip [QuickstartServer.scala]($g8src$/scala/com/lightbend/akka/http/sample/QuickstartServer.scala) { #all-routes }

So far we have referred to `Route` without explaining what it is but now is the time to do so. Under the hood, Akka HTTP uses [Akka Streams](http://doc.akka.io/docs/akka/current/scala/stream/index.html). We don't have time to cover Akka Streams here, but if you are interested, you should take a look at the Hello World sample application for Akka Streams. Since Akka HTTP is built on top of Akka Streams, it means that some concepts of Akka Streams are available for us to use. In the case of `Route` you can think of it as a flow of in- and outbound data which is a perfect fit for Akka Streams. (Technically the `Route` type is `RequestContext ⇒ Future[RouteResult]` but there is no need to worry about what that means now.)

## Http server

To set up an Akka HTTP server we must first define some implicit values that will be used by the server:

@@snip [QuickstartServer.scala]($g8src$/scala/com/lightbend/akka/http/sample/QuickstartServer.scala) { #server-bootstrapping }

What does the above mean and why do we need it?

* `ActorSystem` : the context in which actors will run. What actors, you may wonder? Akka Streams uses actors under the hood, and the actor system defined in this `val` will be picked up and used by Streams.
* `ActorMaterializer` : also Akka Streams related - it uses the materializer to allocate all the necessary resources it needs to run.

With that defined we can move on to instantiate the server:

@@snip [QuickstartServer.scala]($g8src$/scala/com/lightbend/akka/http/sample/QuickstartServer.scala) { #http-server }

We provide three parameters; `routes`, the hostname, and the port. That's it! When running this program, we will have an Akka HTTP server on our machine (localhost) on port 8080. Note that starting a server happens asynchronously and therefore a `Future` is returned by the `bindAndHandle` method.

We should also add code for stopping the server. To do so we use the `StdIn.readLine()` method that will wait until RETURN is pressed on the keyboard. When that happens we `flatMap` the `Future` returned when we started the server to get to the `unbind()` method. Unbinding is also an asynchronous function and when the `Future` returned by `unbind()` is completes we make sure that the actor system is properly terminated.

## Error handling

Finally, we should take a look at how to handle errors. We know, as the astute engineers we are, that errors will happen. We should prepare our program for this and error handling should not be an afterthought when we build systems.

In this sample, we use a very simple exception handler which catches all unexpected exceptions and responds back to the client with an `InternalServerError` (HTTP status code 500) with an error message and for what URI the exception happened. We extract the URI by using the `extractUri` directive.

@@snip [QuickstartServer.scala]($g8src$/scala/com/lightbend/akka/http/sample/QuickstartServer.scala) { #exception-handler }

## The complete server code

Here is the complete server code used in the sample:

@@snip [QuickstartServer.scala]($g8src$/scala/com/lightbend/akka/http/sample/QuickstartServer.scala)
