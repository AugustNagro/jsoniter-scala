package com.github.plokhotnyuk.jsoniter_scala.macros

import java.nio.charset.StandardCharsets.UTF_8
import java.time._
import java.util.{Objects, UUID}

import com.github.plokhotnyuk.jsoniter_scala.core._
import com.github.plokhotnyuk.jsoniter_scala.macros.JsonCodecMaker._
import org.scalatest.exceptions.TestFailedException

import scala.annotation.switch
import scala.util.hashing.MurmurHash3


enum TrafficLight {
  case Red, Yellow, Green
}

enum MediaType(val value: Long, name: String) {
  case `text/json` extends MediaType(1L, "text/json")
  case `text/html` extends MediaType(2L, "text/html")
  case `application/jpeg` extends MediaType(3L, "application/jpeg")
}


class JsonCodecMakerEnumSpec extends VerifyingSpec {
  import NamespacePollutions._

  "JsonCodecMakerNeEnum.make generate codes which" should {
    "serialize and deserialize Scala3 enums" in {
      verifySerDeser(make[List[TrafficLight]](CodecMakerConfig.withDiscriminatorFieldName(None)),
        List(TrafficLight.Red, TrafficLight.Yellow, TrafficLight.Green), """["Red","Yellow","Green"]""")

      implicit val codecOfMediaType: JsonValueCodec[MediaType] = new JsonValueCodec[MediaType] {
        override val nullValue: MediaType = null

        override def decodeValue(in: JsonReader, default: MediaType): MediaType = in.readLong() match {
          case 1L => MediaType.`text/json`
          case 2L => MediaType.`text/html`
          case 3L => MediaType.`application/jpeg`
          case x => in.decodeError(s"unexpected number value: $x")
        }

        override def encodeValue(x: MediaType, out: JsonWriter): _root_.scala.Unit = out.writeVal(x.value)
      }

      verifySerDeser[List[MediaType]](make[List[MediaType]],
        List(MediaType.`text/json`, MediaType.`text/html`, MediaType.`application/jpeg`), """[1,2,3]""")
    }
  }

}
