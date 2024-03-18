package controllers

import javax.inject._
import play.api.mvc._
import play.api.http.Writeable.wByteArray
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  // Action to render the landing page
  def index = Action { implicit request =>
    Ok(views.html.index())
  }
}
