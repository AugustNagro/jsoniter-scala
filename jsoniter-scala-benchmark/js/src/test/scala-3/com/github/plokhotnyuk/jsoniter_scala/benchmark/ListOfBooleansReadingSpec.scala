package com.github.plokhotnyuk.jsoniter_scala.benchmark

import java.nio.charset.StandardCharsets.UTF_8

class ListOfBooleansReadingSpec extends BenchmarkSpecBase {
  def benchmark: ListOfBooleansReading = new ListOfBooleansReading {
    setup()
  }

  "ListOfBooleansReading" should {
    "read properly" in {
      benchmark.borer() shouldBe benchmark.obj
      benchmark.jsoniterScala() shouldBe benchmark.obj
      benchmark.smithy4sJson() shouldBe benchmark.obj
    }
    "fail on invalid input" in {
      val b = benchmark
      b.jsonBytes = "[{}]".getBytes(UTF_8)
      intercept[Throwable](b.borer())
      intercept[Throwable](b.jsoniterScala())
      intercept[Throwable](b.smithy4sJson())
    }
  }
}