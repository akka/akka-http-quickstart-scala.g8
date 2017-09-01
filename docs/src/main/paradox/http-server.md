HTTP Server logic
-----------------

The main class, `QuickstartServer`, is runnable because it extends `App`, as shown in the following snippet. 
This class is intended to "bring it all together", it is the main class that will run the application, as well 
as the class that should bootstrap all actors and other dependencies (database connections etc). 

@@snip [QuickstartServer.scala]($g8src$/scala/com/lightbend/akka/http/sample/QuickstartServer.scala) { #main-class }

Notice that we've separated out the `UserRoutes` trait, in which we'll put all our actual route definitions.
This is a good pattern to follow, especially once your application starts to grow and you'll need some form of 
compartmentalizing them into groups of routes handling specific parts of the exposed API.


## Binding endpoints

Each Akka HTTP `Route` contains one or more `akka.http.scaladsl.server.Directives`, such as: `path`, `get`, `post`, `complete`, etc. There is also a [low-level API](http://doc.akka.io/docs/akka-http/current/scala/http/low-level-server-side-api.html) that allows to inspect requests and create responses manually. For the user registry service, the example needs to support the actions listed below. For each, we can identify a path, the HTTP method, and return value:

| Functionality      | HTTP Method | Path       | Returns              |
|--------------------|-------------|------------|----------------------|
| Create a user      | POST        | /users     | Confirmation message |
| Retrieve a user    | GET         | /users/$ID | JSON payload         |
| Remove a user      | DELETE      | /users/$ID | Confirmation message |
| Retrieve all users | GET         | /users     | JSON payload         |

In the `QuickstartServer` source file, the definition of the `Route` delegates to the routes defined in `UserRoutes`:
`lazy val routes: Route = userRoutes`.

In larger applications this is where we'd combine the various routes of our application into a big route that is concatenating
the various routes of our services. We'd do this using the concat directive like this: `val route = concat(userRoutes, healthCheckRoutes, ...)`

Let's look at the pieces of the example `Route` that bind the endpoints, HTTP methods, and message or payload for each action.

### Retrieving and creating users

The definition of the endpoint to retrieve and create users look like the following:

@@snip [UserRoutes.scala]($g8src$/scala/com/lightbend/akka/http/sample/UserRoutes.scala) { #users-get-post }

A Route is constructed by nesting various *directives* which route an incoming request to the apropriate handler block.
Note the following building blocks from the snippet:

**Generic functionality**

The following directives are used in the above example:

* `pathPrefix("users")` : the path that is used to match the incoming request against.
* `pathEnd` : used on an inner-level to discriminate “path already fully matched” from other alternatives. Will, in this case, match on the "users" path.
* `concat`: concatenates two or more route alternatives. Routes are attempted one after another. If a route rejects a request, the next route in the chain is attempted. This continues until a route in the chain produces a response. If all route alternatives reject the request, the concatenated route rejects the route as well. In that case, route alternatives on the next higher level are attempted. If the root level route rejects the request as well, then an error response is returned that contains information about why the request was rejected.
    * This can also be achieved using the `~` operator, like this: `exampleRoute ~ anotherRoute`. 
    However this method is slightly more error-prone since forgetting to add the `~` between routes in subsequent lines 
    will not result in a compile error (as it would when using the `concat` directive) resulting in only the "last" route to be returned. <br/>
    <br/>
    In short other words: you may see the `~` operator used in Akka HTTP apps, however it is recommended to use the `concat` directive as safer alternative. 

**Retrieving users**

* `get` : matches against `GET` HTTP method.
* `complete` : completes a request which means creating and returning a response from the arguments.

**Creating a user**

* `post` : matches against `POST` HTTP method.
* `entity(as[User])` : converts the HTTP request body into a domain object of type User. Implicitly, we assume that the request contains application/json content. We will look at how this works in the @ref:[JSON](json.md) section.
* `complete` : completes a request which means creating and returning a response from the arguments. Note, how the tuple `(StatusCodes.Created, "...")` of type `(StatusCode, String)` is implicitly converted to a response with the given status code and a text/plain body with the given string.

### Retrieving and removing a user

Next, the example defines how to retrieve and remove a user. In this case, the URI must include the user's id in the form: `/users/$ID`. See if you can identify the code that handles that in the following snippet. This part of the route includes logic for both the GET and the DELETE methods.

@@snip [QuickstartServer.scala]($g8src$/scala/com/lightbend/akka/http/sample/UserRoutes.scala) { #users-get-delete }

This part of the `Route` contains the following:

**Generic functionality**

The following directives are used in the above example:

* `pathPrefix("users")` : the path that is used to match the incoming request against.
* `concat`: concatenates two or more route alternatives. Routes are attempted one after another. If a route rejects a request, the next route in the chain is attempted. This continues until a route in the chain produces a response. 
* `path(Segment) { => user` : this bit of code matches against URIs of the exact format `/users/$ID` and the `Segment` is automatically extracted into the `user` variable so that we can get to the value passed in the URI. For example `/users/Bruce` will populate the `user` variable with the value "Bruce." There is plenty of more features available for handling of URIs, see [pattern matchers](http://doc.akka.io/docs/akka-http/current/scala/http/routing-dsl/path-matchers.html#basic-pathmatchers) for more information.

**Retrieving a user**

* `get` : matches against `GET` HTTP method.
* `complete` : completes a request which means creating and returning a response from the arguments.

Let's break down the logic handling the incoming request:

@@snip [UserRoutes.scala]($g8src$/scala/com/lightbend/akka/http/sample/UserRoutes.scala) { #retrieve-user-info }

The `rejectEmptyResponse` here above is a convenience method that automatically unwraps a future, handles an `Option` by converting `Some` into a successful response, returns a HTTP status code 404 for `None`, and passes on to the `ExceptionHandler` in case of an error, which returns the HTTP status code 500 by default.

**Deleting a user**

* `delete` : matches against the Http directive `DELETE`.

The logic for handling delete requests is as follows:

@@snip [UserRoutes.scala]($g8src$/scala/com/lightbend/akka/http/sample/UserRoutes.scala) { #users-delete-logic }

So we send an instruction about removing a user to the user registry actor, wait for the response and return an appropriate HTTP status code to the client.


## The complete Route

Below is the complete `Route` definition from the sample application:

@@snip [UserRoutes.scala]($g8src$/scala/com/lightbend/akka/http/sample/UserRoutes.scala) { #all-routes }

Note that one might want to separate those routes into smaller route values and `concat` them together into the `userRoutes`
value - in a similar fashion like we do in the `QuickstartServer` leading to a bit less "dense" code.

## Binding the HTTP server

At the beginning of the `main` class, the example defines some implicit values that will be used by the Akka HTTP server:

@@snip [QuickstartServer.scala]($g8src$/scala/com/lightbend/akka/http/sample/QuickstartServer.scala) { #server-bootstrapping }

Akka Streams uses these values:

* `ActorSystem` : provides a context in which actors will run. What actors, you may wonder? Akka Streams uses actors under the hood, and the actor system defined in this `val` will be picked up and used by Streams.
* `ActorMaterializer` : while the ActorSystem is the host of all thread pools and live actors, an ActorMaterializer is specific to Akka Streams and is what makes them run. The ActorMaterializer interprets stream descriptions into executable entities which are run on actors, and this is why it requires an ActorSystem to function.

Further down in `QuickstartServer.scala`, you will find the code to instantiate the server:

@@snip [QuickstartServer.scala]($g8src$/scala/com/lightbend/akka/http/sample/QuickstartServer.scala) { #http-server }

The `bindAndhandle` method only takes three parameters; `routes`, the hostname, and the port. That's it! When this program runs--as you've seen--it starts an Akka HTTP server on localhost port 8080. Note that startup happens asynchronously and therefore the `bindAndHandle` method returns a `Future`.

The code for stopping the server includes the `StdIn.readLine()` method that will wait until RETURN is pressed on the keyboard. When that happens, `flatMap` uses the `Future` returned when we started the server to get to the `unbind()` method. Unbinding is also an asynchronous function. When the `Future` returned by `unbind()` completes, the example code makes sure that the actor system is properly terminated.

## The complete server code

Here is the complete server code used in the sample:

@@snip [QuickstartServer.scala]($g8src$/scala/com/lightbend/akka/http/sample/QuickstartServer.scala)

Let's move on to the actor that handles registration.
