package controllers
import akka.actor.ActorSystem
import akka.stream.Materializer
import com.google.inject.Inject
import play.api.i18n.Lang.logger
import javax.inject._
import play.api.mvc._
import stream.OrderSupplyStreamProcessor

@Singleton
class StreamStarter @Inject()(cc: ControllerComponents, system: ActorSystem, materializer: Materializer,
                               orderSupplyStreamProcessor: OrderSupplyStreamProcessor
                             ) extends AbstractController(cc) {
  def startProcessing: Action[AnyContent] = Action {
    try {
      orderSupplyStreamProcessor.startProcessing()
      Ok("Stream processing started")
    } catch {
      case e: Exception =>
        // Log the exception for debugging
        logger.error("Error starting stream processing", e)
        // Consider returning a more informative error response (e.g., InternalServerError)
        InternalServerError("An error occurred while starting stream processing.")
    }
  }
}

