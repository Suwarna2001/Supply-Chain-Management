package controllers

import play.api.mvc._

import javax.inject._
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  // Action to render the landing page
  def index = Action { implicit request =>
    Ok(views.html.index("Supply Chain Management"))
  }
}