package $package$

import $package$.UserRegistry.ActionPerformed

//#json-formats
import spray.json.RootJsonFormat
import spray.json.DefaultJsonProtocol

object JsonFormats  {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  implicit val userJsonFormat: RootJsonFormat[User] = jsonFormat3(User.apply)
  implicit val usersJsonFormat: RootJsonFormat[Users] = jsonFormat1(Users.apply)

  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
}
//#json-formats
