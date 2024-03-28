// @GENERATOR:play-routes-compiler
// @SOURCE:conf/routes

package controllers;

import router.RoutesPrefix;

public class routes {
  
  public static final controllers.ReverseHomeController HomeController = new controllers.ReverseHomeController(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseOrderController OrderController = new controllers.ReverseOrderController(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseKafkaController KafkaController = new controllers.ReverseKafkaController(RoutesPrefix.byNamePrefix());

  public static class javascript {
    
    public static final controllers.javascript.ReverseHomeController HomeController = new controllers.javascript.ReverseHomeController(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseOrderController OrderController = new controllers.javascript.ReverseOrderController(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseKafkaController KafkaController = new controllers.javascript.ReverseKafkaController(RoutesPrefix.byNamePrefix());
  }

}
