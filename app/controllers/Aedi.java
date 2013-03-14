package controllers;

import controllers.Utils.DateUtils;
import controllers.Utils.JsonUtils;
import models.ContactEntreprise;
import models.Entreprise;
import models.Personne;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import services.AnnuaireService;
import views.html.aedi.annuaire;

import java.util.List;

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
        try {
            if( !utilisateurEstAuthorise() ) {
                return unauthorized();
            }

            AnnuaireService.GetEntrepriseResult result = AnnuaireService.getEntreprise(id);
            if( result.statut == AnnuaireService.Statut.ENTREPRISE_NON_TROUVEE ) {
                return ok(JsonUtils.genererReponseJson(JsonUtils.JsonStatut.ERREUR, "Entreprise non trouvée."));
            } else if( result.statut == AnnuaireService.Statut.ERREUR_INTERNE ) {
                return ok(JsonUtils.genererReponseJson(JsonUtils.JsonStatut.ERREUR, "Erreur interne."));
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
        } catch (Exception e) {
          return ok(JsonUtils.genererReponseJson(JsonUtils.JsonStatut.ERREUR, "Erreur interne."));
        }
    }

    @Security.Authenticated(SecuriteAPI.class)
    public static Result annuaireMajEntreprise(Long id, String nom, String secteur, String description) {
        if( !utilisateurEstAuthorise() ) {
            return unauthorized();
        }

        AnnuaireService.CreationEntrepriseResult result = AnnuaireService.ajouterOuModifierEntreprise(id, nom, secteur, description);

        ObjectNode json = JsonUtils.genererReponseJson(JsonUtils.JsonStatut.OK, "L'entreprise a bien été modifiée.");
        if( result.estCreation ) {
            json.put("id", result.id);
        } else {
            json.put("id", -1);
        }

        // attendu: {statut, id >=0 si ajout et == -1 si modification}
        return ok(json);
    }

    @Security.Authenticated(SecuriteAPI.class)
    public static Result annuaireSupprimerEntreprise(Long id) {
        if( !utilisateurEstAuthorise() ) {
            return unauthorized();
        }

        AnnuaireService.Statut statut = AnnuaireService.supprimerEntreprise(id);
        if( statut == AnnuaireService.Statut.ENTREPRISE_NON_TROUVEE ) {
            return ok(JsonUtils.genererReponseJson(JsonUtils.JsonStatut.ERREUR, "L'entreprise n'existe pas."));
        } else {
            return ok(JsonUtils.genererReponseJson(JsonUtils.JsonStatut.OK, "Suppression effectuée."));
        }
    }

    @Security.Authenticated(SecuriteAPI.class)
    public static Result annuaireNouveauCommentaire(Long idEntreprise, String contenu, Integer categorie) {

        if( !utilisateurEstAuthorise() ) {
            return unauthorized();
        }

        AnnuaireService.CreationCommentaireResult result = AnnuaireService.ajouterCommentaire(idEntreprise, contenu, categorie);
        if( result.statut == AnnuaireService.Statut.ENTREPRISE_NON_TROUVEE ) {
            return ok(JsonUtils.genererReponseJson(JsonUtils.JsonStatut.ERREUR, "L'entreprise n'existe pas."));
        } else {
            ObjectNode json = JsonUtils.genererReponseJson(JsonUtils.JsonStatut.OK, "Création effectuée.");
            json.put("id", result.id);
            json.put("personne", Securite.utilisateur().toJsonMinimal());
            json.put("timestamp", DateUtils.formaterDate(result.timestamp));
            return ok(json);
        }

        // attendu: {statut, id, personne: {prenom, nom, role}}
    }

    @Security.Authenticated(SecuriteAPI.class)
    public static Result annuaireSupprimerCommentaire(Long id) {
        if( !utilisateurEstAuthorise() ) {
            return unauthorized();
        }

        AnnuaireService.Statut statut = AnnuaireService.supprimerCommentaire(id);
        if( statut == AnnuaireService.Statut.COMMENTAIRE_NON_TROUVE ) {
            return ok(JsonUtils.genererReponseJson(JsonUtils.JsonStatut.ERREUR, "Le commentaire n'existe pas."));
        } else {
            return ok(JsonUtils.genererReponseJson(JsonUtils.JsonStatut.OK, "Suppression effectuée."));
        }
    }

    @Security.Authenticated(SecuriteAPI.class)
    public static Result annuaireRechercheContacts(String motsclesStr) {
        if( !utilisateurEstAuthorise() ) {
            return unauthorized();
        }

        List<Entreprise> entreprises = AnnuaireService.rechercherContacts(motsclesStr);

        ObjectNode json = JsonUtils.genererReponseJson(JsonUtils.JsonStatut.OK, "Résultats trouvés.");
        ArrayNode ejson = new ArrayNode(JsonNodeFactory.instance);
        for( Entreprise e: entreprises ) {
            ejson.add(e.toJson());
        }
        json.put("entreprises", ejson);

        return ok(json);
    }

    @Security.Authenticated(SecuriteAPI.class)
    public static Result annuaireMajContact() {
        if( !utilisateurEstAuthorise() ) {
            return unauthorized();
        }

        /*
        // à la réception
        var  nouveauContact = {
                id_contact,
                id_entreprise,
                fonction,
                personne : {
                    id,
                    nom,
                    prenom,
                    mails : [emails],
                    telephones : [tels]
                },
                ville : {
                    code_postal,
                    libelle,
                    pays
                },
                commentaire :,
                priorite
        };
         */

        // 1) reconstruction de la requête
        ObjectNode root = (ObjectNode) request().body().asJson();
        Long idEntreprise = root.get("id_entreprise").asLong();
        ContactEntreprise nouveau = ContactEntreprise.construire(root);
        if( nouveau == null ) {
            return ok(JsonUtils.genererReponseJson(JsonUtils.JsonStatut.ERREUR, "Arguments manquants"));
        }

        AnnuaireService.MajContactResult result = AnnuaireService.majContact(nouveau, idEntreprise);
        if( result.statut == AnnuaireService.Statut.OK ) {
            ObjectNode json = JsonUtils.genererReponseJson(JsonUtils.JsonStatut.OK, "Création effectuée");
            json.put("id", result.nouvelId);
            json.put("id_personne", result.nouvelId); // TODO id_personne utilisé?
            return ok(json);
        } else if( result.statut == AnnuaireService.Statut.ENTREPRISE_NON_TROUVEE ) {
            return ok(JsonUtils.genererReponseJson(JsonUtils.JsonStatut.ERREUR, "Entreprise non trouvée."));
        } else {
            return ok(JsonUtils.genererReponseJson(JsonUtils.JsonStatut.ERREUR, "Statut inconnu."));
        }

        // attendu: {id, id_personne, statut}
    }

    @Security.Authenticated(SecuriteAPI.class)
    public static Result annuaireSupprimerContact(Long id) {
        if( !utilisateurEstAuthorise() ) {
            return unauthorized();
        }

        AnnuaireService.Statut statut = AnnuaireService.supprimerContact(id);
        if( statut == AnnuaireService.Statut.CONTACT_NON_TROUVE ) {
            return ok(JsonUtils.genererReponseJson(JsonUtils.JsonStatut.ERREUR, "Le contact n'existe pas."));
        } else {
            return ok(JsonUtils.genererReponseJson(JsonUtils.JsonStatut.OK, "Suppression effectuée."));
        }
    }
}
