package com.hootsuite.akka.persistence.redis.snapshot

import akka.util.ByteString
import com.hootsuite.akka.persistence.redis.SerializationException
import redis.ByteStringFormatter
import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._


/**
 * Snapshot entry that can be serialized and deserialized to JSON
 * JSON in turn is serialized to ByteString so it can be stored in Redis with Rediscala
 */
case class SnapshotRecord(sequenceNr: Long, timestamp: Long, snapshot: Array[Byte])

object SnapshotRecord {

  implicit val byteStringFormatter = new ByteStringFormatter[SnapshotRecord] {
    override def serialize(data: SnapshotRecord): ByteString = {
      ByteString(data.asJson.noSpaces)
    }

    override def deserialize(bs: ByteString): SnapshotRecord = {
      parser.decode[SnapshotRecord](bs.utf8String).toEither match  {
        case Right(v:SnapshotRecord) => v
        case Left(e:Error) => throw SerializationException("Error deserializing Journal.", e)
      }
    }
  }
}