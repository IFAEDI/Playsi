package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.main;

public class StaticPages extends Controller {

    public static Result index() {
        return ok(main.render("Accueil", index.render()));
    }

}
