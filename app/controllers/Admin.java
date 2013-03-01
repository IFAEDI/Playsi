package controllers;

import controllers.Utils.JsonUtils;
import models.Personne;
import models.Utilisateur;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import play.db.ebean.Model;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
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

        // TODO service?
        ArrayNode auth = new ArrayNode(JsonNodeFactory.instance);
        for( Utilisateur.TYPE_AUTH t: Utilisateur.TYPE_AUTH.values() ) {
            auth.add( t.getIntitule() );
        }

        ArrayNode roles = new ArrayNode(JsonNodeFactory.instance);
        for(Personne.Role r: Personne.Role.values()) {
            roles.add( r.getIntitule() );
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

        // TODO service
        Model.Finder<Long, Utilisateur> finder = new Model.Finder<Long, Utilisateur>(Long.class, Utilisateur.class);
        List<Utilisateur> utilisateurs = finder.all();

        ObjectNode json = JsonUtils.genererReponseJson(JsonUtils.JsonStatut.OK, utilisateurs.size() + " utilisateur(s) trouvés.");
        ArrayNode jsonUtilisateurs = new ArrayNode(JsonNodeFactory.instance);
        for( Utilisateur u: utilisateurs ) {
            jsonUtilisateurs.add( u.toJson() );
        }
        json.put("utilisateurs", jsonUtilisateurs);

        // attendu: msg.statut, msg.utilisateurs = [{id, login, service, type, nom, prenom}]
        return ok(json);
    }

    @Security.Authenticated(SecuriteAPI.class)
    static public Result infoUtilisateur(Long id) {
        if( SecuriteAPI.utilisateur().getRole() != Utilisateur.Role.ADMIN ) {
            return unauthorized();
        }
        // attendu: msg.statut, msg.utilisateur.{login, nom, prenom, role, mails: [][libellé, email], telephones: [][libellé, email]
        return ok();
    }

    @Security.Authenticated(SecuriteAPI.class)
    static public Result modifierUtilisateur() {
        if( SecuriteAPI.utilisateur().getRole() != Utilisateur.Role.ADMIN ) {
            return unauthorized();
        }
        // attendu: msg.statut
        return ok();
    }

    @Security.Authenticated(SecuriteAPI.class)
    static public Result supprimerUtilisateur(Long id) {
        if( SecuriteAPI.utilisateur().getRole() != Utilisateur.Role.ADMIN ) {
            return unauthorized();
        }
        // attendu: msg.statut
        return ok();
    }
}
