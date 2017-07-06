JSON
----

In the server class, we saw that JSON is somehow converted into Scala classes and vice versa. In this section, we shall look at how this is implemented and what you need to do to make it work.

Let's take a look at the server class definition once again:

@@snip [QuickstartServer.scala]($g8src$/scala/com/lightbend/akka/http/sample/QuickstartServer.scala) { #main-class }

See the `JsonSupport` up there? This is a trait that we have created and it looks like this:

@@snip [JsonSupport.scala]($g8src$/scala/com/lightbend/akka/http/sample/JsonSupport.scala)

The above trait defines two implicit values; `userJsonFormat` and `usersJsonFormat`. To do so we use the `jsonFormatX` methods, from [Spray Json](https://github.com/spray/spray-json),  where X is representing the number of parameters in the underlying case classes:

@@snip [UserRegistryActor.scala]($g8src$/scala/com/lightbend/akka/http/sample/UserRegistryActor.scala) { #user-case-classes }

By defining the Formatters as `implicit`, we ensure that the compiler can map the formatting functionality with the case classes we want to convert. We won't go into the underlying functionality for how the formatters are implemented. All we have to remember for now is to define the formatters as implicit and that the Formatter used should map the number of parameters of "its" case class.
