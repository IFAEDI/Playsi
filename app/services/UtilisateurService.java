package services;

import controllers.SecuriteAPI;
import models.Mail;
import models.Personne;
import models.Telephone;
import models.Utilisateur;
import play.Logger;
import play.db.ebean.Model;

import java.util.ArrayList;
import java.util.List;

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
        UTILISATEUR_NON_TROUVE,
        LOGIN_DEJA_PRIS,
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

    public static class LabelsResult {
        public List<String> types_auth = new ArrayList<String>();
        public List<String> roles = new ArrayList<String>();
    }

    public static LabelsResult getLabels() {
        LabelsResult labelsResult = new LabelsResult();
        for( Utilisateur.TYPE_AUTH t: Utilisateur.TYPE_AUTH.values() ) {
            labelsResult.types_auth.add( t.getIntitule() );
        }
        for( Personne.Role r: Personne.Role.values() ) {
            labelsResult.roles.add( r.getIntitule() );
        }
        return labelsResult;
    }

    public static List<Utilisateur> utilisateurs() {
        return finder.all();
    }

    public static class InfoUtilisateurResult {
        public Utilisateur utilisateur;
        public Statut statut;
    }

    public static InfoUtilisateurResult infoUtilisateur(Long id) {
        InfoUtilisateurResult result = new InfoUtilisateurResult();
        result.utilisateur = finder.byId(id);
        if( result.utilisateur == null ) {
            result.statut = Statut.UTILISATEUR_NON_TROUVE;
        } else {
            result.statut = Statut.OK;
        }
        return result;
    }

    public static Statut adminMajUtilisateur(Utilisateur nouveau, boolean estNouvelUtilisateur) {
        Utilisateur ancien = finder.byId(nouveau.getId());
        if( ancien == null ) {
            if( !estNouvelUtilisateur ) {
                return Statut.UTILISATEUR_NON_TROUVE;
            } else {
                Utilisateur memeLogin = finder.where().eq(Utilisateur.DB_LOGIN, nouveau.getLogin()).findUnique();
                if( memeLogin != null ) {
                    return Statut.LOGIN_DEJA_PRIS;
                }

                // Les nouveaux utilisateurs doivent s'authentifier par login / password
                ancien = new Utilisateur();
                ancien.setAuth_service(Utilisateur.TYPE_AUTH.REGULIERE);
            }
        }

        ancien.setRole( nouveau.getRole() );
        ancien.setLogin( nouveau.getLogin() );

        if( majUtilisateur(ancien, nouveau) ) {
            return Statut.OK;
        } else {
            return Statut.ERREUR_INTERNE;
        }
    }

    public static Statut supprimerUtilisateur(Long id, boolean supprimerPersonne) {

        // TODO what if... l'admin se supprime lui-même?
        // TODO what if... l'admin supprime le dernier des admins?

        Utilisateur utilisateur = finder.byId(id);
        if( utilisateur == null ) {
            return Statut.UTILISATEUR_NON_TROUVE;
        } else {
            if(!supprimerPersonne) {
                Personne p = utilisateur;
                p.save(); // TODO vérifier utilité?
            }
            utilisateur.delete();
            return Statut.OK;
        }
    }
}
