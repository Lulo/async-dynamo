# async-dynamo

This fork was created to work with AWS 1.5, AKKA 2.1, and Play 2.1.  

With this fork you can publish to your local maven repo so it can easily be included in your own projects without waiting on others to setup new releases.

## Overview

async-dynamo is an asynchronous scala client for Amazon Dynamo database. It is based on Akka library and provides asynchronous API.

## Quick Start

For detailed information please read [User Guide][user_guide].

### SBT

First checkout this fork, make any changes you want/need, then run sbt publish to generate the jar in your local repo

In any project you want to utilize the jar in, just add this to your `built.sbt` file:

```scala
resolvers += "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository"

libraryDependencies += "asyncdynamo" %% "async-dynamo" % "1.7.2"
```

### Example

```scala

import asyncdynamo._
import nonblocking._
import scala.concurrent.duration._
import akka.util.Timeout

object QuckStart extends App{
  implicit val dynamo = Dynamo( DynamoConfig( System.getProperty("amazon.accessKey"), System.getProperty("amazon.secret"), tablePrefix = "devng_", endpointUrl = System.getProperty("dynamo.url", "https://dynamodb.eu-west-1.amazonaws.com") ), connectionCount = 3)
  implicit val timeout = Timeout(10 seconds)

  try{
    case class Person(id :String, name: String, email: String)
    implicit val personDO = DynamoObject.of3(Person) // make Person dynamo-enabled

    if (! TableExists[Person]()) //implicit kicks in to execute operation as blocking
      CreateTable[Person](5,5).blockingExecute(dynamo, 1 minute) // overriding implicit timeout

    val julian = Person("123", "Julian", "julian@gmail.com")
    val saved : Option[Person] = Save(julian) andThen Read[Person](julian.id) // implicit automatically executes and blocks for convenience
    assert(saved == Some(julian))

  } finally dynamo ! 'stop
}
```

### Asynchronous version

```scala
val operation = for {
  _ <- Save(julian)
  saved <- Read[Person]("123")
  _ <- DeleteById[Person]("123")
} yield saved

(operation executeOn dynamo)
  .onSuccess { case person => println("Saved [%s]" format person)}
  .onComplete{ case _ => dynamo ! 'stop }
```

### Explicit type class definition

If you need more flexibility when mapping your object to Dynamo table you can define the type class yourself, i.e.
```scala
    case class Account(id: String, balance: Double, lastModified: Date)

    implicit val AccoundDO : DynamoObject[Account] = new DynamoObject[Account]{
        val table = "account"
        def toDynamo( a : Account)  = Map( "id" -> a.id,
                  "balance" -> a.balance.toString,
                  "lastModified" -> formatter.toString(a.lastModified )

        def fromDynamo(f: Map[String, AttributeValue]) =
            Account( f("id").getS, f("balance").getS.toDouble, formatter.parse(f("lastModified").getS) )
    }
```

## Information for developers

### Documentation

For detailed information please read [User Guide][user_guide].

### AWS Credentials

In order for tests to be able to connect to Dynamo you have to open Amazon AWS account and pass the AWS credentials to scala via properties.
The easiest way to do this is to add them to SBT_OPTS variable, i.e.

    export SBT_OPTS="$SBT_OPTS -Damazon.accessKey=... -Damazon.secret=..."

To build async-dynamo run:

     sbt clean test

## Copyright and license

Copyright 2012 2ndlanguage Limited. This product includes software developed at 2ndlanguage Limited.

Licensed under the [Apache License, Version 2.0] [license] (the "License");
you may not use this software except in compliance with the License.

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

[user_guide]: doc/user_guide.md "User Guide"
[license]: http://www.apache.org/licenses/LICENSE-2.0
