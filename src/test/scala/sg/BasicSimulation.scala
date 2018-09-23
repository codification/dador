package sg

import io.gatling.core.Predef._ // 2
import io.gatling.http.Predef._
import scala.concurrent.duration._

class BasicSimulation extends Simulation {
  val httpConf = http.baseUrl("https://api.football-data.org/")
  val scn = scenario("Basic Simulation")
    .exec(http("request_1")
      .get("v1/teams/73"))
    .pause(5)
  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)
}