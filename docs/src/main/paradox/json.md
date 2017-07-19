JSON conversion
---------------

When exercising the app, you interacted with JSON payloads. How does the example app convert data between JSON format and data that can be used by Scala classes? The answer begins in the server class definition `JsonSupport` trait:

@@snip [QuickstartServer.scala]($g8src$/scala/com/lightbend/akka/http/sample/QuickstartServer.scala) { #main-class }

This trait is implemented in the `JsonSupport.scala` source file:

@@snip [JsonSupport.scala]($g8src$/scala/com/lightbend/akka/http/sample/JsonSupport.scala)

To handle the two different payloads, the trait defines two implicit values; `userJsonFormat` and `usersJsonFormat`. Defining the formatters as `implicit` ensures that the compiler can map the formatting functionality with the case classes to convert.

The `jsonFormatX` methods come from [Spray Json](https://github.com/spray/spray-json). The `X` represents the number of parameters in the underlying case classes:

@@snip [UserRegistryActor.scala]($g8src$/scala/com/lightbend/akka/http/sample/UserRegistryActor.scala) { #user-case-classes }

We won't go into how the formatters are implemented. All you need to remember for now is to define the formatters as implicit and that the formatter used should map the number of parameters belonging to the case class it converts.

Comment: I was a bit confused by the previous paragraph. Does the user have to write their own formatters or are these available as libraries?

Now that we've examined the example app thoroughly, let's test a few the remaining use cases.
