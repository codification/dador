
import io.gatling.app.Gatling
import io.gatling.core.CoreComponents
import io.gatling.core.Predef._
import io.gatling.core.action.{Action, ChainableAction}
import io.gatling.core.config.{GatlingConfiguration, GatlingPropertiesBuilder}
import io.gatling.core.protocol.{Protocol, ProtocolComponents, ProtocolKey}
import io.gatling.core.session.Session
import io.gatling.core.structure.ScenarioContext

class GrpcSimulation extends Simulation {
  val scn = scenario("Basic Simulation")
    .exec(GrpcActionBuilder())
    .pause(5)

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(GrpcProtocol())
}

case class GrpcProtocol() extends Protocol {
  def warmUp(): Unit = {}

  def userEnd(session: Session): Unit = {}
}

object GrpcProtocol {
  val GrpcProtocolKey: ProtocolKey[GrpcProtocol, GrpcProtocolComponents] =
    new ProtocolKey[GrpcProtocol, GrpcProtocolComponents] {
    override def protocolClass: Class[Protocol] = classOf[GrpcProtocol].asInstanceOf[Class[Protocol]]

    override def defaultProtocolValue(configuration: GatlingConfiguration): GrpcProtocol = GrpcProtocol()

    override def newComponents(coreComponents: CoreComponents): GrpcProtocol => GrpcProtocolComponents = {
      protocol => {
        GrpcProtocolComponents(coreComponents, protocol)
      }
    }
  }
}

case class GrpcActionBuilder() extends io.gatling.core.action.builder.ActionBuilder {
  override def build(ctx: ScenarioContext, next: Action): Action = {
    val components = ctx.protocolComponentsRegistry.components(GrpcProtocol.GrpcProtocolKey)
    GrpcAction("newFuncName", components.grpcProtocol, next)
  }
}

case class GrpcAction(functionName: String, protocol: GrpcProtocol, val next: Action) extends ChainableAction {
  override def name: String = "GrpcAction"

  override def execute(session: Session): Unit = {}
}

case class GrpcProtocolComponents(coreComponents: CoreComponents, grpcProtocol: GrpcProtocol)
  extends ProtocolComponents {
  override def onStart: Session => Session = ProtocolComponents.NoopOnStart
  override def onExit: Session => Unit = ProtocolComponents.NoopOnExit
}

object RunGrpc {
  def main(args: Array[String]) {
    val props = new GatlingPropertiesBuilder
    props.simulationClass(classOf[GrpcSimulation].getName)
    props.runDescription("GrpcSimulation")

    Gatling.fromMap(props.build)
  }
}

