package sg
import io.gatling.app.Gatling
import io.gatling.core.config.GatlingPropertiesBuilder

object RunTests {
  def main(args: Array[String]) {
    val props = new GatlingPropertiesBuilder
    props.simulationClass(classOf[BasicSimulation].getName)
    props.runDescription("descriptor")

    Gatling.fromMap(props.build)
  }
}
