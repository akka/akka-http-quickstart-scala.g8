The Akka HTTP server class
--------------------------

Let's dissect the server class, `QuickstartServer`. We make this class runnable by extending `App` (we will discuss the trait `JsonSupport` later):

@@snip [QuickstartServer.scala]($g8src$/scala/com/lightbend/akka/http/sample/QuickstartServer.scala) { #main-class }

Now that we have a class to run we should add some Akka HTTP fundamentals with which we will build our RESTful web service. The aim is to use routes to define endpoints by choosing what to do for which requests.

## Routes

For our service we want to define the following endpoints:

| Path        | Http method     | Intent             | Returns              |
|-------------|-----------------|--------------------|----------------------|
| /users      | POST            | Create a new user  | Confirmation message |
| /users      | GET             | Retrieve all users | JSON payload         |
| /users/$ID  | GET             | Retrieve a user    | JSON payload         |
| /users/$ID  | DELETE          | Remove a user      | Confirmation message |

Akka HTTP provides a [domain-specific language](https://en.wikipedia.org/wiki/Domain-specific_language) (DSL) to simplify the routes/endpoints definition. Each route is composed of one or more `akka.http.scaladsl.server.Directives`, e.g. `path`, `get`, `post`, `complete`, etc. There is also a [low-level API](http://doc.akka.io/docs/akka-http/current/scala/http/low-level-server-side-api.html) that allows to inspect requests and create responses manually.

### Creating and retrieving a user

Let us take a look at the source code for the first two endpoints, the `/users` path with `GET` and `POST` HTTP methods:

@@snip [QuickstartServer.scala]($g8src$/scala/com/lightbend/akka/http/sample/QuickstartServer.scala) { #users-get-post }

The snippet above contains a couple of interesting Akka HTTP building blocks:

**Generic functionality**

* `pathPrefix("users")` : the path that is used to match the incoming request against.
* `pathEnd` : used on an inner-level to discriminate “path already fully matched” from other alternatives. Will, in this case, match on the "users" path.
* `~`: concatenates two or more route alternatives. Routes are attempted one after another. If a route rejects a request, the next route in the chain is attempted. This continues until a route in the chain produces a response. If all route alternatives reject the request, the concatenated route rejects the route as well. In that case, route alternatives on the next higher level are attempted. If the root level route rejects the request as well, then an error response is returned that contains information about why the request was rejected.

**Retrieving users**

* `get` : matches against `GET` HTTP method.
* `complete` : completes a request which means creating and returning a response from the arguments.

**Creating a user**

* `post` : matches against `POST` HTTP method.
* `entity(as[User])` : converts the HTTP request body into a domain object of type User. Implicitly, we assume that the request contains application/json content. We will look at how this works in the @ref:[JSON](json.md) section.
* `complete` : completes a request which means creating and returning a response from the arguments. Note, how the tuple (StatusCodes.Created, "...") of type (StatusCode, String) is implicitly converted to a response with the given status code and a text/plain body with the given string.

### Retrieving and removing a user

Next we need to define how to retrieve and remove a user, i.e. for the case when the URI `/users/$ID` is used where `$ID` is the id of the user:

@@snip [QuickstartServer.scala]($g8src$/scala/com/lightbend/akka/http/sample/QuickstartServer.scala) { #users-get-delete }

This Route snippet contains a couple of interesting concepts:

**Generic functionality**

* `pathPrefix("users")` : the path that is used to match the incoming request against.
* `path(Segment) { => user` : this bit of code matches against URIs of the exact format `/users/$ID` and the `Segment` is automatically extracted into the `user` variable so that we can get to the value passed in the URI. For example `/users/Bruce` will populate the `user` variable with the value "Bruce." There is plenty of more features available for handling of URIs, see [pattern matchers](http://doc.akka.io/docs/akka-http/current/scala/http/routing-dsl/path-matchers.html#basic-pathmatchers) for more information.
* `~`: concatenates two or more route alternatives. Routes are attempted one after another. If a route rejects a request, the next route in the chain is attempted. This continues until a route in the chain produces a response. If all route alternatives reject the request, the concatenated route rejects the route as well. In that case, route alternatives on the next higher level are attempted. If the root level route rejects the request as well, then an error response is returned that contains information about why the request was rejected.

**Retrieving a user**

* `get` : matches against `GET` HTTP method.
* `complete` : completes a request which means creating and returning a response from the arguments.

Let's break down the "business logic":

@@snip [QuickstartServer.scala]($g8src$/scala/com/lightbend/akka/http/sample/QuickstartServer.scala) { #retrieve-user-info }

The `rejectEmptyResponse` here above is a convenience method that automatically unwraps a future, handles an `Option` by converting `Some` into a successful response, returns a HTTP status code 404 for `None`, and passes on to the `ExceptionHandler` in case of an error, which returns the HTTP status code 500 by default.

**Deleting a user**

* `delete` : matches against the Http directive `DELETE`.

The "business logic" for when deleting a user is straight forward; send an instruction about removing a user to the user registry actor, wait for the response and return an appropriate HTTP status code to the client.

### The complete Route

Below is the complete `Route` definition used in the sample application:

@@snip [QuickstartServer.scala]($g8src$/scala/com/lightbend/akka/http/sample/QuickstartServer.scala) { #all-routes }

So far we have referred to `Route` without explaining what it is but now is the time to do so. Under the hood, Akka HTTP uses [Akka Streams](http://doc.akka.io/docs/akka/current/scala/stream/index.html). We don't have time to cover Akka Streams here, but if you are interested, you should take a look at the Hello World sample application for Akka Streams. Since Akka HTTP is built on top of Akka Streams, it means that some concepts of Akka Streams are available for us to use. In the case of `Route` you can think of it as a flow of in- and outbound data which is a perfect fit for Akka Streams. (Technically the `Route` type is `RequestContext ⇒ Future[RouteResult]` but there is no need to worry about what that means now.)

## Http server

To set up an Akka HTTP server we must first define some implicit values that will be used by the server:

@@snip [QuickstartServer.scala]($g8src$/scala/com/lightbend/akka/http/sample/QuickstartServer.scala) { #server-bootstrapping }

What does the above mean and why do we need it?

* `ActorSystem` : the context in which actors will run. What actors, you may wonder? Akka Streams uses actors under the hood, and the actor system defined in this `val` will be picked up and used by Streams.
* `ActorMaterializer` : while the ActorSystem is the host of all thread pools and live actors, an ActorMaterializer is specific to Akka Streams and is what makes them run. The ActorMaterializer interprets stream descriptions into executable entities which are run on actors, and this is why it requires an ActorSystem to function.

With that defined we can move on to instantiate the server:

@@snip [QuickstartServer.scala]($g8src$/scala/com/lightbend/akka/http/sample/QuickstartServer.scala) { #http-server }

We provide three parameters; `routes`, the hostname, and the port. That's it! When running this program, we will have an Akka HTTP server on our machine (localhost) on port 8080. Note that starting a server happens asynchronously and therefore a `Future` is returned by the `bindAndHandle` method.

We should also add code for stopping the server. To do so we use the `StdIn.readLine()` method that will wait until RETURN is pressed on the keyboard. When that happens we `flatMap` the `Future` returned when we started the server to get to the `unbind()` method. Unbinding is also an asynchronous function and when the `Future` returned by `unbind()` is completes we make sure that the actor system is properly terminated.

## Error handling

Finally, we should take a look at how to [handle errors](http://doc.akka.io/docs/akka-http/current/scala/http/routing-dsl/exception-handling.html). We know, as the astute engineers we are, that errors will happen. We should prepare our program for this and error handling should not be an afterthought when we build systems.

In this sample, we use a very simple exception handler which catches all unexpected exceptions and responds back to the client with an `InternalServerError` (HTTP status code 500) with an error message and for what URI the exception happened. We extract the URI by using the `extractUri` directive.

@@snip [QuickstartServer.scala]($g8src$/scala/com/lightbend/akka/http/sample/QuickstartServer.scala) { #exception-handler }

## The complete server code

Here is the complete server code used in the sample:

@@snip [QuickstartServer.scala]($g8src$/scala/com/lightbend/akka/http/sample/QuickstartServer.scala)
