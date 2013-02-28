package services;

import controllers.Securite;
import models.Utilisateur;
import play.db.ebean.Model;

/**
 * Created with IntelliJ IDEA.
 * User: Benjamin
 * Date: 28/02/13
 * Time: 14:14
 * To change this template use File | Settings | File Templates.
 */
public class UtilisateurService {

    public enum Statut {
        VARIABLES_MANQUANTES,
        IDENTIFIANTS_INVALIDES,
        OK
    }

    public final static String SESSION_ID = "session_id";
    public final static String UTILISATEUR = ".utilisateur";

    private static Model.Finder<Long, Utilisateur> finder = new Model.Finder<Long, Utilisateur>(Long.class, Utilisateur.class);

    public static class LoginResult {
        public Statut statut;
        public Utilisateur utilisateur;
    }

    public static LoginResult loginRegulier(String username, String password) {
        LoginResult resultat = new LoginResult();
        if( username.isEmpty() || password.isEmpty() ) {
            resultat.statut = Statut.VARIABLES_MANQUANTES;
        } else {
            Utilisateur utilisateur =
                finder.where().eq("login", username)
                        .eq("passwd", password)
                        .eq("auth_service", Securite.AUTH_REGULIERE)
                        .findUnique();

            if( utilisateur != null ) {
                resultat.statut = Statut.OK;
                resultat.utilisateur = utilisateur;
            } else {
                resultat.statut = Statut.IDENTIFIANTS_INVALIDES;
            }
        }
        return resultat;
    }
}