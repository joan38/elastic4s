package com.sksamuel.elastic4s.cat

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{FlatSpec, Matchers}

class CatIndexesTest extends FlatSpec with Matchers with SharedElasticSugar with ElasticDsl {

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  http.execute {
    bulk(
      indexInto("catindex1/landmarks").fields("name" -> "hampton court palace"),
      indexInto("catindex2/landmarks").fields("name" -> "hampton court palace"),
      indexInto("catindex3/landmarks").fields("name" -> "hampton court palace")
    ).refresh(RefreshPolicy.IMMEDIATE)
  }.await


  "cats indexes" should "return all indexes" in {
    http.execute {
      catIndices()
    }.await.map(_.index).toSet shouldBe Set("catindex1", "catindex2", "catindex3")
  }

  it should "use health param" in {
    val result = http.execute {
      catIndices(Health.Red)
    }.await.isEmpty shouldBe true
  }

}
