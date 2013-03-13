package controllers;

import models.ContactEntreprise;
import models.Entreprise;
import models.Personne;
import play.db.ebean.Model;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Benjamin
 * Date: 12/03/13
 * Time: 17:15
 * To change this template use File | Settings | File Templates.
 */
public class Aedi extends Controller {

    private static boolean utilisateurEstAuthorise() {
        Personne.Role role = Securite.utilisateur().getRole();
        return ( role == Personne.Role.ADMIN || role == Personne.Role.AEDI );
    }

    @Security.Authenticated(Securite.class)
    public static Result annuaire() {
        if( !utilisateurEstAuthorise() ) {
            return unauthorized();
        }

        // TODO service
        // Get lists
        Entreprise.Finder<Long, Entreprise> finder = new Model.Finder<Long, Entreprise>(Long.class, Entreprise.class);
        List<Entreprise> entreprises = finder.all();

        Set<String> secteursUniques = new HashSet<String>();
        for( Entreprise e : entreprises ) {
            secteursUniques.add( e.getSecteur() );
        }
        List<String> secteurs = new ArrayList<String>(secteursUniques);


        ContactEntreprise.Finder<Long, ContactEntreprise> ceFinder = new Model.Finder<Long, ContactEntreprise>(Long.class, ContactEntreprise.class);
        // TODO meilleure requête pour récupérer les fonctions uniques (SELECT DISTINCT fonction)
        List<ContactEntreprise> contacts = ceFinder.all();
        Set<String> fonctionsUniques = new HashSet<String>();
        for( ContactEntreprise ce : contacts ) {
            fonctionsUniques.add(ce.getFonction());
        }
        List<String> fonctions = new ArrayList<String>(fonctionsUniques);

        // TODO dernier argument
        return ok( views.html.aedi.annuaire.render(entreprises, secteurs, fonctions, true) );
    }

    @Security.Authenticated(SecuriteAPI.class)
    public static Result annuaireInfosEntreprise(Long id) {
        if( !utilisateurEstAuthorise() ) {
            return unauthorized();
        }
        return TODO;
    }

    @Security.Authenticated(SecuriteAPI.class)
    public static Result annuaireMajEntreprise(Long id, String nom, String secteur, String description, String commentaire) {
        if( !utilisateurEstAuthorise() ) {
            return unauthorized();
        }
        return TODO;
    }

    @Security.Authenticated(SecuriteAPI.class)
    public static Result annuaireSupprimerEntreprise(Long id) {
        if( !utilisateurEstAuthorise() ) {
            return unauthorized();
        }
        return TODO;
    }

    @Security.Authenticated(SecuriteAPI.class)
    public static Result annuaireNouveauCommentaire(Long id, String contenu, Integer categorie) {
        if( !utilisateurEstAuthorise() ) {
            return unauthorized();
        }
        return TODO;
    }

    @Security.Authenticated(SecuriteAPI.class)
    public static Result annuaireSupprimerCommentaire(Long id) {
        if( !utilisateurEstAuthorise() ) {
            return unauthorized();
        }
        return TODO;
    }

    @Security.Authenticated(SecuriteAPI.class)
    public static Result annuaireRechercheContacts(String motscles) {
        if( !utilisateurEstAuthorise() ) {
            return unauthorized();
        }
        return TODO;
    }

    @Security.Authenticated(SecuriteAPI.class)
    public static Result annuaireMajContact() {
        if( !utilisateurEstAuthorise() ) {
            return unauthorized();
        }
        return TODO;
    }

    @Security.Authenticated(SecuriteAPI.class)
    public static Result annuaireSupprimerContact(Long id) {
        if( !utilisateurEstAuthorise() ) {
            return unauthorized();
        }
        return TODO;
    }
}
