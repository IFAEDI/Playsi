package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.main;
import views.html.entreprises.rifs;

public class Entreprises extends Controller {

    public static Result rifs() {
        return ok(main.render("Entreprises - Rencontres IFs", rifs.render()));
    }

    public static Result entretiens() {
        // TODO
        return TODO;
    }

    public static Result conferences() {
        // TODO
        return TODO;
    }

    public static Result parrainage() {
        // TODO
        return TODO;
    }
}