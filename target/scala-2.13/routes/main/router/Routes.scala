// @GENERATOR:play-routes-compiler
// @SOURCE:conf/routes

package router

import play.core.routing._
import play.core.routing.HandlerInvokerFactory._

import play.api.mvc._

import _root_.controllers.Assets.Asset

class Routes(
  override val errorHandler: play.api.http.HttpErrorHandler, 
  // @LINE:7
  HomeController_0: controllers.HomeController,
  // @LINE:8
  OrderController_1: controllers.OrderController,
  // @LINE:9
  KafkaController_2: controllers.KafkaController,
  val prefix: String
) extends GeneratedRouter {

  @javax.inject.Inject()
  def this(errorHandler: play.api.http.HttpErrorHandler,
    // @LINE:7
    HomeController_0: controllers.HomeController,
    // @LINE:8
    OrderController_1: controllers.OrderController,
    // @LINE:9
    KafkaController_2: controllers.KafkaController
  ) = this(errorHandler, HomeController_0, OrderController_1, KafkaController_2, "/")

  def withPrefix(addPrefix: String): Routes = {
    val prefix = play.api.routing.Router.concatPrefix(addPrefix, this.prefix)
    router.RoutesPrefix.setPrefix(prefix)
    new Routes(errorHandler, HomeController_0, OrderController_1, KafkaController_2, prefix)
  }

  private val defaultPrefix: String = {
    if (this.prefix.endsWith("/")) "" else "/"
  }

  def documentation = List(
    ("""GET""", this.prefix, """controllers.HomeController.index()"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """uploadJson""", """controllers.OrderController.uploadJson"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """kafkaMessages""", """controllers.KafkaController.index"""),
    Nil
  ).foldLeft(Seq.empty[(String, String, String)]) { (s,e) => e.asInstanceOf[Any] match {
    case r @ (_,_,_) => s :+ r.asInstanceOf[(String, String, String)]
    case l => s ++ l.asInstanceOf[List[(String, String, String)]]
  }}


  // @LINE:7
  private lazy val controllers_HomeController_index0_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix)))
  )
  private lazy val controllers_HomeController_index0_invoker = createInvoker(
    HomeController_0.index(),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.HomeController",
      "index",
      Nil,
      "GET",
      this.prefix + """""",
      """ An example controller showing a sample home page""",
      Seq()
    )
  )

  // @LINE:8
  private lazy val controllers_OrderController_uploadJson1_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("uploadJson")))
  )
  private lazy val controllers_OrderController_uploadJson1_invoker = createInvoker(
    OrderController_1.uploadJson,
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.OrderController",
      "uploadJson",
      Nil,
      "POST",
      this.prefix + """uploadJson""",
      """""",
      Seq()
    )
  )

  // @LINE:9
  private lazy val controllers_KafkaController_index2_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("kafkaMessages")))
  )
  private lazy val controllers_KafkaController_index2_invoker = createInvoker(
    KafkaController_2.index,
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.KafkaController",
      "index",
      Nil,
      "GET",
      this.prefix + """kafkaMessages""",
      """""",
      Seq()
    )
  )


  def routes: PartialFunction[RequestHeader, Handler] = {
  
    // @LINE:7
    case controllers_HomeController_index0_route(params@_) =>
      call { 
        controllers_HomeController_index0_invoker.call(HomeController_0.index())
      }
  
    // @LINE:8
    case controllers_OrderController_uploadJson1_route(params@_) =>
      call { 
        controllers_OrderController_uploadJson1_invoker.call(OrderController_1.uploadJson)
      }
  
    // @LINE:9
    case controllers_KafkaController_index2_route(params@_) =>
      call { 
        controllers_KafkaController_index2_invoker.call(KafkaController_2.index)
      }
  }
}
