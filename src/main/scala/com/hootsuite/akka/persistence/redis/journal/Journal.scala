package com.hootsuite.akka.persistence.redis.journal

import akka.util.ByteString
import com.hootsuite.akka.persistence.redis.SerializationException
import redis.ByteStringFormatter
import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._

/**
 * Journal entry that can be serialized and deserialized to JSON
 * JSON in turn is serialized to ByteString so it can be stored in Redis with Rediscala
 */
case class Journal(sequenceNr: Long, persistentRepr: Array[Byte], deleted: Boolean)

object Journal {

  implicit val byteStringFormatter = new ByteStringFormatter[Journal] {
    override def serialize(data: Journal): ByteString = {
      ByteString(data.asJson.noSpaces)
    }

    override def deserialize(bs: ByteString): Journal = {
      parser.decode[Journal](bs.utf8String).toEither match  {
        case Right(v:Journal) => v
        case Left(e:Error) => throw SerializationException("Error deserializing Journal.", e)
      }
    }
  }
}
