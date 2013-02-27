package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.entreprises.conferences;
import views.html.entreprises.entretiens;
import views.html.entreprises.parrainage;
import views.html.entreprises.rifs;

public class Entreprises extends Controller {

    public static Result rifs() {
        return ok(rifs.render());
    }

    public static Result entretiens_inscription() {
        // TODO
        return TODO;
    }

    public static Result entretiens_presentation() {
        return ok(entretiens.render());
    }

    public static Result conferences() {
        return ok(conferences.render());
    }

    public static Result parrainage() {
        return ok(parrainage.render());
    }
}