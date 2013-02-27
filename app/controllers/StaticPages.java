package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.a_propos;
import views.html.index;
import views.html.main;

public class StaticPages extends Controller {

    public static Result index() {
        return ok(main.render("Accueil", index.render()));
    }

    public static Result apropos() {
        return ok(main.render("A propos", a_propos.render()));
    }

    public static Result contact() {
        return TODO;
    }

}
