package services;

import controllers.SecuriteAPI;
import models.Mail;
import models.Telephone;
import models.Utilisateur;
import play.Logger;
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
        ERREUR_INTERNE,
        OK
    }

    private static Model.Finder<Long, Utilisateur> finder = new Model.Finder<Long, Utilisateur>(Long.class, Utilisateur.class);

    public static class LoginResult {
        public Statut statut;
        public Utilisateur utilisateur;
    }

    public static LoginResult loginRegulier(String username, String password) {

        LoginResult resultat = new LoginResult();
        try {
            if( username.isEmpty() || password.isEmpty() ) {
                resultat.statut = Statut.VARIABLES_MANQUANTES;
            } else {
                Utilisateur utilisateur =
                    finder.where().eq(Utilisateur.DB_LOGIN, username)
                            .eq(Utilisateur.DB_PASSWORD, password)
                            .eq(Utilisateur.DB_AUTH_SERVICE, Utilisateur.TYPE_AUTH.REGULIERE)
                            .findUnique();

                if( utilisateur != null ) {
                    resultat.statut = Statut.OK;
                    resultat.utilisateur = utilisateur;
                } else {
                    resultat.statut = Statut.IDENTIFIANTS_INVALIDES;
                }
            }
        } catch( Exception e ) {
            Logger.error("Erreur lors de la récupération d'un utilisateur par auth normale. Login = " + username + " / Password = " + password);
            Logger.error( e.getMessage() );
            resultat.statut = Statut.ERREUR_INTERNE;
        } finally {
            return resultat;
        }
    }

    public static boolean majUtilisateur(Utilisateur ancien, Utilisateur nouveau) {
        try {
            Utilisateur utilisateur = ancien;
            utilisateur.setNom(nouveau.getNom());
            utilisateur.setPrenom(nouveau.getPrenom());
            // met à jour le mot de passe si nécessaire
            if( utilisateur.getAuth_service() == Utilisateur.TYPE_AUTH.REGULIERE && !nouveau.getPasswd().isEmpty() ) {
                utilisateur.setPasswd(nouveau.getPasswd());
            }

            // Suppression des anciennes valeurs de tels et mails
            // TODO y a peut-être mieux? (trier téléphones actuels, trier téléphones du json, faire une comparaison 1 à 1)
            for( Telephone t : utilisateur.getTelephones() ) {
                t.delete();
            }
            for( Mail m: utilisateur.getMails() ) {
                m.delete();
            }

            utilisateur.setMails( nouveau.getMails() );
            utilisateur.setTelephones( nouveau.getTelephones() );
            utilisateur.save();
            return true;
        } catch (Exception e) {
            Logger.error("Erreur lors de la mise à jour d'un utilisateur. Utilisateur id = " + SecuriteAPI.utilisateur().getId());
            Logger.error( e.getMessage() );
            return false;
        }
    }
}
