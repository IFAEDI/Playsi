package controllers;

import controllers.Utils.Constantes;
import controllers.Utils.JsonUtils;
import models.Mail;
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
        }
        return badRequest();
    }

    @Security.Authenticated(SecuriteAPI.class)
    public static Result majUtilisateur() {
        // TODO service
        // TODO constantiser tous les noms

        JsonNode root = request().body().asJson();

        JsonNode reponseBadRequest = JsonUtils.genererReponseJson(JsonUtils.JsonStatut.ERREUR, "Arguments manquants");
        if( !root.has("nom") || !root.has("prenom") || !root.has("password")
                || !root.has("mails") || !root.has("telephones")) {
            return badRequest(reponseBadRequest);
        }

        String nom = root.get("nom").asText();
        String prenom = root.get("prenom").asText();
        String password = root.get("password").asText();

        List<Mail> mails = new ArrayList<Mail>();
        // Reconstruction des emails: récupérer
        for( JsonNode jn : (ArrayNode) root.get("mails") ) {
            ObjectNode on = (ObjectNode) jn;
            if( !on.has("email") ) {
                // un objet "email" en json doit avoir au moins la clé "email"
                return badRequest(reponseBadRequest);
            }
            Mail m = new Mail(on);
            // ajoute l'email seulement s'il est rempli (non vide)
            if( !m.getEmail().isEmpty() )
                mails.add( m );
        }

        // idem que reconstruction emails
        List<Telephone> tels = new ArrayList<Telephone>();
        for( JsonNode jn : (ArrayNode) root.get("telephones") ) {
            ObjectNode on = (ObjectNode) jn;
            if( !on.has("numero") ) {
                return badRequest(reponseBadRequest);
            }
            Telephone t = new Telephone(on);
            if( !t.getNumero().isEmpty() )
                tels.add(t);
        }

        Utilisateur utilisateur = SecuriteAPI.utilisateur();
        utilisateur.setNom(nom);
        utilisateur.setPrenom(prenom);
        // met à jour le mot de passe si nécessaire
        if( !password.isEmpty() ) {
            utilisateur.setPasswd(password);
        }

        // Suppression des anciennes valeurs de tels et mails
        // TODO y a peut-être mieux? (trier téléphones actuels, trier téléphones du json, faire une comparaison 1 à 1)

        for( Telephone t : utilisateur.getTelephones() ) {
            t.delete();
        }
        for( Mail m: utilisateur.getMails() ) {
            m.delete();
        }

        utilisateur.setMails( mails );
        utilisateur.setTelephones( tels );
        utilisateur.save();

        return ok(JsonUtils.genererReponseJson(JsonUtils.JsonStatut.OK, "Mise à jour réussie."));
    }

    public static Result javascriptRoutes() {
        response().setContentType("text/javascript");
        return ok(
                Routes.javascriptRouter("jsRoutes",
                        routes.javascript.Etudiants.apiStages(),
                        routes.javascript.StaticPages.login(),
                        routes.javascript.StaticPages.majUtilisateur()
                )
        );
    }
}
