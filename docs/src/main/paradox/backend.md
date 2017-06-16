Backend
-------

It is probably an exaggeration to call this for a "backend" since it, in this sample, only consists of a basic actor. In a real system, we would have many actors interacting with each other and perhaps a database or microservices. However, since the focus of this tutorial is on Akka Http, it is not very important what this backend does but more how to interact with a backend from within Akka Http. Hopefully, you already have a good grasp of how to communicate back and forth between Akka Http and actors by now.

If you feel you should brush up your Akka Actor knowledge, then the [getting started guide]((http://developer.lightbend.com/guides/akka-quickstart-scala/)) for Akka actors tutorial is a good start.

The sample code in the `UserRegistryActor` is very simple. It keeps registered users in a `Set`. Once it receives messages it will match those into what action it should take:

@@snip [UserRegistryActor.scala]($g8src$/scala/com/lightbend/akka/http/sample/UserRegistryActor.scala)
