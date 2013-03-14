package controllers;

import controllers.Utils.Constantes;
import controllers.Utils.JsonUtils;
import models.Utilisateur;
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

    @Security.Authenticated(SecuriteAPI.class)
    public static Result majUtilisateur() {
        ObjectNode root = (ObjectNode) request().body().asJson();
        Utilisateur u = Utilisateur.construire(root, false);

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
                        routes.javascript.Aedi.annuaireExisteEntreprise(),
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