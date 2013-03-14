package controllers;

import controllers.Utils.Constantes;
import controllers.Utils.JsonUtils;
import models.Utilisateur;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import services.UtilisateurService;
import views.html.admin.utilisateurs;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Benjamin
 * Date: 01/03/13
 * Time: 14:45
 * To change this template use File | Settings | File Templates.
 */
public class Admin extends Controller {

    @Security.Authenticated(Securite.class)
    static public Result utilisateurs() {
        if( Securite.utilisateur().getRole() != Utilisateur.Role.ADMIN ) {
            return unauthorized();
        }
        return ok(utilisateurs.render());
    }

    @Security.Authenticated(SecuriteAPI.class)
    static public Result labelsUtilisateurs() {
        if( Securite.utilisateur().getRole() != Utilisateur.Role.ADMIN ) {
            return unauthorized();
        }

        ObjectNode json = JsonUtils.genererReponseJson(JsonUtils.JsonStatut.OK, "Récupération des labels effectuée");

        UtilisateurService.LabelsResult labelsResult = UtilisateurService.getLabels();
        ArrayNode auth = new ArrayNode(JsonNodeFactory.instance);
        for( String s: labelsResult.types_auth ) {
            auth.add( s );
        }

        ArrayNode roles = new ArrayNode(JsonNodeFactory.instance);
        for( String s: labelsResult.roles) {
            roles.add( s );
        }

        json.put("services", auth);
        json.put("roles", roles);

        // attendu: msg.statut, msg.services, msg.roles
        return ok(json);
    }

    @Security.Authenticated(SecuriteAPI.class)
    static public Result listeUtilisateurs() {
        if( SecuriteAPI.utilisateur().getRole() != Utilisateur.Role.ADMIN ) {
            return unauthorized();
        }

        List<Utilisateur> utilisateurs = UtilisateurService.utilisateurs();
        ObjectNode json = JsonUtils.genererReponseJson(JsonUtils.JsonStatut.OK, utilisateurs.size() + " utilisateur(s) trouvés.");
        ArrayNode jsonUtilisateurs = new ArrayNode(JsonNodeFactory.instance);
        for( Utilisateur u: utilisateurs ) {
            jsonUtilisateurs.add( u.toJsonMinimal() );
        }
        json.put(Constantes.JSON_UTILISATEURS, jsonUtilisateurs);

        // attendu: msg.statut, msg.utilisateurs = [{id, login, service, type, nom, prenom}]
        return ok(json);
    }

    @Security.Authenticated(SecuriteAPI.class)
    static public Result infoUtilisateur(Long id) {
        if( SecuriteAPI.utilisateur().getRole() != Utilisateur.Role.ADMIN ) {
            return unauthorized();
        }

        UtilisateurService.InfoUtilisateurResult result = UtilisateurService.infoUtilisateur(id);
        if( result.statut == UtilisateurService.Statut.UTILISATEUR_NON_TROUVE ) {
            return ok(JsonUtils.genererReponseJson(JsonUtils.JsonStatut.ERREUR, "Aucun utilisateur avec cet id trouvé."));
        }

        ObjectNode json = JsonUtils.genererReponseJson(JsonUtils.JsonStatut.OK, "Utilisateur trouvé.");
        json.put(Constantes.JSON_UTILISATEUR, result.utilisateur.toJsonFull() );

        // attendu: msg.statut, msg.utilisateur.{login, nom, prenom, role, mails: [{libellé, email}], telephones: [{libellé, email}]
        return ok(json);
    }

    @Security.Authenticated(SecuriteAPI.class)
    static public Result majUtilisateur() {
        if( SecuriteAPI.utilisateur().getRole() != Utilisateur.Role.ADMIN ) {
            return unauthorized();
        }

        ObjectNode root = (ObjectNode) request().body().asJson();
        Utilisateur nouveau = Utilisateur.construire(root, true);
        if( nouveau == null ) {
            return ok(JsonUtils.genererReponseJson(JsonUtils.JsonStatut.ERREUR, "Arguments manquants."));
        }

        UtilisateurService.Statut statut = UtilisateurService.adminMajUtilisateur(nouveau, nouveau.getId() == Constantes.JSON_ID_UTILISATEUR_INEXISTANT);
        if( statut == UtilisateurService.Statut.UTILISATEUR_NON_TROUVE ) {
            return ok(JsonUtils.genererReponseJson(JsonUtils.JsonStatut.ERREUR, "Utilisateur non trouvé."));
        } else if( statut == UtilisateurService.Statut.LOGIN_DEJA_PRIS ) {
            return ok(JsonUtils.genererReponseJson(JsonUtils.JsonStatut.ERREUR, "Nom d'utilisateur déjà pris."));
        } else if( statut == UtilisateurService.Statut.ERREUR_INTERNE ) {
            return ok(JsonUtils.genererReponseJson(JsonUtils.JsonStatut.ERREUR, "Erreur interne. Les administrateurs ont été prévenus."));
        } else if( statut == UtilisateurService.Statut.OK ) {
            return ok(JsonUtils.genererReponseJson(JsonUtils.JsonStatut.OK, "Modification effectuée"));
        } else {
            Logger.error("Controllers/Admin - majUtilisateur - statut inconnu: " + statut.name());
            return ok(JsonUtils.genererReponseJson(JsonUtils.JsonStatut.ERREUR, "Erreur interne. Les administrateurs ont été prévenus."));
        }
        // attendu: msg.statut
    }

    @Security.Authenticated(SecuriteAPI.class)
    static public Result supprimerUtilisateur(Long id, Boolean supprPersonne) {
        if( SecuriteAPI.utilisateur().getRole() != Utilisateur.Role.ADMIN ) {
            return unauthorized();
        }

        UtilisateurService.Statut statut = UtilisateurService.supprimerUtilisateur(id, supprPersonne);
        if( statut == UtilisateurService.Statut.UTILISATEUR_NON_TROUVE ) {
            return ok(JsonUtils.genererReponseJson(JsonUtils.JsonStatut.ERREUR, "Utilisateur non trouvé."));
        } else if( statut == UtilisateurService.Statut.OK ) {
            return ok(JsonUtils.genererReponseJson(JsonUtils.JsonStatut.OK, "Utilisateur supprimé."));
        } else {
            Logger.error("Controllers/Admin - majUtilisateur - statut inconnu: " + statut.name());
            return ok(JsonUtils.genererReponseJson(JsonUtils.JsonStatut.ERREUR, "Erreur interne. Les administrateurs ont été prévenus."));
        }
        // attendu: msg.statut
    }

    public static Result rifs() {
        return TODO;
    }

    public static Result simulations() {
        return TODO;
    }

    public static Result journal() {
        return TODO;
    }
}
