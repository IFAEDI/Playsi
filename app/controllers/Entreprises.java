package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.entreprises.entretiens;
import views.html.main;
import views.html.entreprises.rifs;
import views.html.entreprises.conferences;

public class Entreprises extends Controller {

    public static Result rifs() {
        return ok(main.render("Entreprises - Rencontres IFs", rifs.render()));
    }

    public static Result entretiens_inscription() {
        // TODO
        return TODO;
    }

    public static Result entretiens_presentation() {
        return ok(main.render("Entreprises - Simulations d'entretiens", entretiens.render()));
    }

    public static Result conferences() {
        return ok(main.render("Entreprises - Conférences", conferences.render()));
    }

    public static Result parrainage() {
        // TODO
        return TODO;
    }
}