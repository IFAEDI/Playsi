package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.a_propos;
import views.html.contact;
import views.html.index;

public class StaticPages extends Controller {

    public static Result index() {
        return ok(index.render());
    }

    public static Result apropos() {
        return ok(a_propos.render());
    }

    public static Result contact() {
        return ok(contact.render());
    }

}
