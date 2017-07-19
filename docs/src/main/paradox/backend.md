Backend logic
-------------

In this example, the backend only uses one basic actor. In a real system, we would have many actors interacting with each other and perhaps, multiple data stores and microservices. However, the focus of this tutorial is on how to interact with a backend from within Akka HTTP -- not on the actor itself.

The sample code in the `UserRegistryActor` is very simple. It keeps registered users in a `Set`. Once it receives messages it matches them to the defined cases to determine which action to take:

@@snip [UserRegistryActor.scala]($g8src$/scala/com/lightbend/akka/http/sample/UserRegistryActor.scala)

If you feel you need to brush up on your Akka Actor knowledge, the [Getting Started Guide]((http://doc.akka.io/docs/akka/current/scala/guide/index.html)) reviews actor concepts in the context of a simple Internet of Things (IoT) example.
