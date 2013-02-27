package controllers;

import play.mvc.Controller;
import play.mvc.Result;

import views.html.etudiants.evenements;
import views.html.main;

/**
 * Created with IntelliJ IDEA.
 * User: Benjamin
 * Date: 27/02/13
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class Etudiants extends Controller {

    public static Result entretiens() {
        // TODO
        return TODO;
    }

    public static Result stages() {
        // TODO
        return TODO;
    }

    public static Result cvs() {
        // TODO
        return TODO;
    }

    public static Result evenements() {
        return ok(main.render("Evènements étudiants", evenements.render()));
    }
}
