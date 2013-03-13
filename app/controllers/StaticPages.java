package controllers;

import controllers.Utils.Constantes;
import controllers.Utils.JsonUtils;
import models.Mail;
import models.Personne;
import models.Telephone;
import models.Utilisateur;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import play.Routes;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import services.UtilisateurService;
import views.html.a_propos;
import views.html.auth;
import views.html.contact;
import views.html.index;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public static Result authRequise() {
        return ok(auth.render());
    }

    public static Result logout() {
        Cache.remove(session().get(Constantes.SESSION_ID).concat(Constantes.SESSION_UTILISATEUR_SUFFIXE));
        session().clear();
        return redirect(routes.StaticPages.index());
    }

    public static Result login(String username, String password) {
        UtilisateurService.LoginResult loginResult = UtilisateurService.loginRegulier(username, password);
        if( loginResult.statut == UtilisateurService.Statut.VARIABLES_MANQUANTES ) {

            return badRequest(JsonUtils.genererReponseJson(JsonUtils.JsonStatut.ERREUR, "Variables manquantes"));

        } else if( loginResult.statut == UtilisateurService.Statut.OK ) {

            session().clear();
            String sessionId = UUID.randomUUID().toString();
            session(Constantes.SESSION_ID, sessionId);
            Cache.set(sessionId.concat(Constantes.SESSION_UTILISATEUR_SUFFIXE), loginResult.utilisateur);
            return ok( JsonUtils.genererReponseJson(JsonUtils.JsonStatut.OK, "Authentification réussie."));

        } else if( loginResult.statut == UtilisateurService.Statut.IDENTIFIANTS_INVALIDES ) {

            return ok( JsonUtils.genererReponseJson(JsonUtils.JsonStatut.ERREUR, "Identifiants non valides"));
        } else if( loginResult.statut == UtilisateurService.Statut.ERREUR_INTERNE ) {

            return ok( JsonUtils.genererReponseJson(JsonUtils.JsonStatut.ERREUR, "Erreur interne du serveur. Les administrateurs ont été prévenus."));
        }
        return badRequest();
    }

    /**
     * Parse l'utilisateur depuis le corps de la requête, sans le sauver en bdd.
     * Factorisé pour être utilisé dans StaticPages.majUtilisateur et Admin.infoUtilisateur
     *
     * @param entierRequis Faux si seules les infos accessibles pour la modification de son propre profil
     *                     sont nécessaires, vrai autrement (toutes les infos sauf type_auth sont nécessaires).
     * @return Un utilisateur si tous les arguments minimaux étaient présents, null sinon (ie
     * il manque un argument).
     */
    public static Utilisateur parseUtilisateur(boolean entierRequis) {
        // parse de l'utilisateur, utilisé dans StaticPages.majUtilisateur
        // et Admin.majUtilisateur
        Utilisateur u = new Utilisateur();
        // récupération des arguments par body parsing
        JsonNode root = request().body().asJson();

        if( !root.has(Constantes.JSON_NOM) || !root.has(Constantes.JSON_PRENOM) || !root.has(Constantes.JSON_PASSWORD)
                || !root.has(Constantes.JSON_MAILS) || !root.has(Constantes.JSON_TELEPHONES)) {

            return null;
        }

        if( entierRequis && (!root.has(Constantes.JSON_ID) || !root.has(Constantes.JSON_ROLE) || !root.has(Constantes.JSON_LOGIN)) ) {

            return null;
        }

        if( entierRequis ) {
            u.setId(root.get(Constantes.JSON_ID).asLong());
            u.setRole(Personne.Role.parOrdinal(root.get(Constantes.JSON_ROLE).asInt()));
            u.setLogin(root.get(Constantes.JSON_LOGIN).asText());
        }

        u.setNom(root.get(Constantes.JSON_NOM).asText());
        u.setPrenom(root.get(Constantes.JSON_PRENOM).asText());
        u.setPasswd(root.get(Constantes.JSON_PASSWORD).asText());

        List<Mail> mails = new ArrayList<Mail>();
        // Reconstruction des emails: récupérer
        for( JsonNode jn : (ArrayNode) root.get(Constantes.JSON_MAILS) ) {
            ObjectNode on = (ObjectNode) jn;
            if( !on.has(Constantes.JSON_EMAIL) ) {
                // un objet "email" en json doit avoir au moins la clé "email"
                return null;
            }
            Mail m = new Mail(on);
            // ajoute l'email seulement s'il est rempli (non vide)
            if( !m.getEmail().isEmpty() )
                mails.add( m );
        }
        u.setMails(mails);

        // idem que reconstruction emails
        List<Telephone> tels = new ArrayList<Telephone>();
        for( JsonNode jn : (ArrayNode) root.get(Constantes.JSON_TELEPHONES) ) {
            ObjectNode on = (ObjectNode) jn;
            if( !on.has(Constantes.JSON_NUMERO) ) {
                return null;
            }
            Telephone t = new Telephone(on);
            if( !t.getNumero().isEmpty() )
                tels.add(t);
        }
        u.setTelephones(tels);

        return u;
    }

    @Security.Authenticated(SecuriteAPI.class)
    public static Result majUtilisateur() {
        Utilisateur u = parseUtilisateur(false);
        if( u == null ) {
            return ok(JsonUtils.genererReponseJson(JsonUtils.JsonStatut.ERREUR, "Arguments manquants"));
        }

        if( UtilisateurService.majUtilisateur(SecuriteAPI.utilisateur(), u) ) {
            return ok(JsonUtils.genererReponseJson(JsonUtils.JsonStatut.OK, "Mise à jour réussie."));
        } else {
            return ok(JsonUtils.genererReponseJson(JsonUtils.JsonStatut.ERREUR, "Erreur interne lors de la mise à jour du profil."));
        }

    }

    public static Result javascriptRoutes() {
        response().setContentType("text/javascript");
        return ok(
                Routes.javascriptRouter("jsRoutes",
                        // login
                        routes.javascript.Etudiants.apiStages(),
                        routes.javascript.StaticPages.login(),
                        // admin: utilisateurs
                        routes.javascript.Admin.listeUtilisateurs(),
                        routes.javascript.Admin.labelsUtilisateurs(),
                        routes.javascript.Admin.infoUtilisateur(),
                        routes.javascript.Admin.supprimerUtilisateur(),
                        // aedi: annuaire
                        routes.javascript.Aedi.annuaireInfosEntreprise(),
                        routes.javascript.Aedi.annuaireMajEntreprise(),
                        routes.javascript.Aedi.annuaireSupprimerEntreprise(),
                        routes.javascript.Aedi.annuaireNouveauCommentaire(),
                        routes.javascript.Aedi.annuaireSupprimerCommentaire(),
                        routes.javascript.Aedi.annuaireRechercheContacts(),
                            // Aedi.annuaireMajContact reconstruit l'objet json
                        routes.javascript.Aedi.annuaireSupprimerContact()
                )
        );
    }
}