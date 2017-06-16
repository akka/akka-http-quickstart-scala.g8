package com.lightbend.akka.http.sample

import akka.actor.{ Actor, ActorLogging, Props }
import scala.collection.mutable.Set

case class UserInfo(maybeUser: Option[User])
//#user-case-classes
case class User(name: String, age: Int, countryOfResidence: String)
case class Users(users: Seq[User])
//#user-case-classes

object UserRegistryActor {
  final case object GetUsers
  final case class CreateUser(user: User)
  final case class GetUser(name: String)
  final case class DeleteUser(name: String)

  def props: Props = Props[UserRegistryActor]
}

class UserRegistryActor extends Actor with ActorLogging {
  import UserRegistryActor._

  val users: Set[User] = Set.empty[User]

  def receive = {
    case GetUsers => sender ! Users(users.toSeq)
    case CreateUser(user) => users += user
    case GetUser(name) => sender ! UserInfo(users.find(_.name == name))
    case DeleteUser(name) => users.find(_.name == name) map { user => users -= user }
  }
}
