package controllers;

import controllers.Utils.JsonUtils;
import models.Personne;
import org.codehaus.jackson.node.ObjectNode;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import services.AnnuaireService;
import views.html.aedi.annuaire;

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

        AnnuaireService.AnnuaireRenderInfos infos = AnnuaireService.getAnnuaireRenderInfos();

        // TODO dernier argument droit edition?
        return ok( annuaire.render(infos.entreprises, infos.secteurs, infos.fonctions, true) );
    }

    @Security.Authenticated(SecuriteAPI.class)
    public static Result annuaireExisteEntreprise(String nom) {
        if( !utilisateurEstAuthorise() ) {
            return unauthorized();
        }

        ObjectNode json = JsonUtils.genererReponseJson(JsonUtils.JsonStatut.OK, "Résultat trouvé.");
        if(AnnuaireService.nomEntrepriseEstDejaPris(nom)) {
            json.put("dejaPris", true);
        } else {
            json.put("dejaPris", false);
        }
        return ok(json);
    }

    @Security.Authenticated(SecuriteAPI.class)
    public static Result annuaireInfosEntreprise(Long id) {
        if( !utilisateurEstAuthorise() ) {
            return unauthorized();
        }

        AnnuaireService.GetEntrepriseResult result = AnnuaireService.getEntreprise(id);
        if( result.statut == AnnuaireService.Statut.ENTREPRISE_NON_TROUVEE ) {
            return ok(JsonUtils.genererReponseJson(JsonUtils.JsonStatut.ERREUR, "Entreprise non trouvée."));
        }

        ObjectNode json = JsonUtils.genererReponseJson(JsonUtils.JsonStatut.OK, "L'entreprise a été trouvée.");
        json.put("entreprise", result.entreprise.toJson());

        // attendu:
        /*
        { statut, entreprise:
            { description:
                {nom, secteur, description, commentaire},
            contacts:
                [
                {
                id_contact,
                personne:
                    {nom, prenom, mails:[(libellé, email)], telephones:[(libellé, tél)]},
                ville:
                    {code_postal, libelle, pays},
                commentaire,
                fonction,
                priorite,
                }
                ],
            commentaires:
            [{
                id_commentaire,
                contenu,
                categorie,
                timestamp,
                personne: { nom, prenom, role }
            }]

        }
        */

        return ok(json);
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
