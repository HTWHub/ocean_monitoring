package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.Logger

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  val logger: Logger = Logger(this.getClass)

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    logger.info("FIND ME")
    logger.warn("FIND ME")
    Ok(views.html.index())
  }
}
