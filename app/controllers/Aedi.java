package controllers;

import com.avaje.ebean.Ebean;
import controllers.Utils.Constantes;
import controllers.Utils.JsonUtils;
import models.*;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import play.db.ebean.Model;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import services.AnnuaireService;
import views.html.aedi.annuaire;

import java.util.ArrayList;
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
            json.put("id", -1); // TODO constantes json
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
    public static Result annuaireRechercheContacts(String motscles) {
        if( !utilisateurEstAuthorise() ) {
            return unauthorized();
        }
        // TODO
        return TODO;
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

        // TODO gérer arguments manquants

        JsonNode root = request().body().asJson();
        // TODO constantes json
        Long idContact = root.get("id_contact").asLong();
        Long idEntreprise = root.get("id_entreprise").asLong();
        String fonction = root.get("fonction").asText();
        String commentaire = root.get("commentaire").asText();
        Integer priorite = root.get("priorite").asInt();

        ObjectNode personne = (ObjectNode) root.get("personne");

        // TODO factoriser récupération personne
        String nomPersonne = personne.get("nom").asText();
        String prenomPersonne = personne.get("prenom").asText();

        ContactEntreprise nouvellePersonne = null;
        nouvellePersonne = Ebean.find(ContactEntreprise.class).where().eq("id", idContact).findUnique();
        boolean estMiseAJour = nouvellePersonne != null;
        if( !estMiseAJour ) {
            nouvellePersonne = new ContactEntreprise();
        }

        for( Mail m: nouvellePersonne.getMails() ) {
            m.delete();
        }
        for( Telephone t: nouvellePersonne.getTelephones() ) {
            t.delete();
        }

        List<Mail> mails = new ArrayList<Mail>();
        // Reconstruction des emails: récupérer
        for( JsonNode jn : (ArrayNode) personne.get(Constantes.JSON_MAILS) ) {
            ObjectNode on = (ObjectNode) jn;
            if( !on.has(Constantes.JSON_EMAIL) ) {
                // un objet "email" en json doit avoir au moins la clé "email"
                return null;
            }
            Mail m = new Mail(on);
            // ajoute l'email seulement s'il est rempli (non vide)
            if( !m.getEmail().isEmpty() ) {
                mails.add(m);
            }
        }

        // idem que reconstruction emails
        List<Telephone> tels = new ArrayList<Telephone>();
        for( JsonNode jn : (ArrayNode) personne.get(Constantes.JSON_TELEPHONES) ) {
            ObjectNode on = (ObjectNode) jn;
            if( !on.has(Constantes.JSON_NUMERO) ) {
                return null;
            }
            Telephone t = new Telephone(on);
            if( !t.getNumero().isEmpty() ) {
                tels.add(t);
            }
        }

        Model.Finder<Long, Entreprise> finder = new Model.Finder<Long, Entreprise>(Long.class, Entreprise.class);
        Entreprise e = finder.byId( idEntreprise ); // TODO gérer entreprise non présente en bdd

        // TODO service

        nouvellePersonne.setId(idContact);
        nouvellePersonne.setMails(mails);
        nouvellePersonne.setTelephones(tels);
        nouvellePersonne.setNom(nomPersonne);
        nouvellePersonne.setPrenom(prenomPersonne);

        nouvellePersonne.setCommentaire(commentaire);
        nouvellePersonne.setFonction(fonction);
        nouvellePersonne.setPriorite(priorite);

        ObjectNode ville = (ObjectNode) root.get("ville");
        String codePostal = ville.get("code_postal").asText();
        String libelle = ville.get("libelle").asText();
        String pays = ville.get("pays").asText();

        // mise à jour de la ville
        if( !codePostal.isEmpty() || !libelle.isEmpty() || !pays.isEmpty() ) {
            if( estMiseAJour ) {
                Ville ancienneVille = nouvellePersonne.getVille();
                if( ancienneVille == null ) {
                    ancienneVille = new Ville();
                }

                boolean modificationVille = false;
                if( ancienneVille.getCodePostal() == null || !ancienneVille.getCodePostal().equals(codePostal) ) {
                    modificationVille = true;
                    ancienneVille.setCodePostal(codePostal);
                }
                if( ancienneVille.getLibelle() == null || !ancienneVille.getLibelle().equals(libelle) ) {
                    modificationVille = true;
                    ancienneVille.setLibelle(libelle);
                }
                if( ancienneVille.getPays() == null || !ancienneVille.getPays().equals(pays) ) {
                    modificationVille = true;
                    ancienneVille.setPays(pays);
                }

                if( modificationVille ) {
                    nouvellePersonne.setVille(ancienneVille);
                }
            } else {
                Ville nouvelleVille = new Ville();
                nouvelleVille.setCodePostal(codePostal);
                nouvelleVille.setLibelle(libelle);
                nouvelleVille.setPays(pays);
                nouvellePersonne.setVille(nouvelleVille);
            }
        } else {
            if( estMiseAJour && nouvellePersonne.getVille() != null ) {
                Ville aSupprimer = nouvellePersonne.getVille();
                nouvellePersonne.setVille(null);
                nouvellePersonne.save();
                aSupprimer.delete();
            }
        }

        if( estMiseAJour ) {
            nouvellePersonne.save();
        } else {
            List<ContactEntreprise> contacts = e.getContacts();
            if( contacts == null ) {
                contacts = new ArrayList<ContactEntreprise>();
            }
            contacts.add(nouvellePersonne);
            e.setContacts(contacts);
            e.save();
        }

        ObjectNode json = JsonUtils.genererReponseJson(JsonUtils.JsonStatut.OK, "Création effectuée");
        if( estMiseAJour ) {
            json.put("id", -1);
        } else {
            json.put("id", nouvellePersonne.getId()); // TODO vérifier ça
        }
        json.put("id_personne", nouvellePersonne.getId());

        // attendu: {id, id_personne, statut}
        return ok(json);
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
