package models;

import org.codehaus.jackson.node.ObjectNode;
import play.db.ebean.Model;
import play.libs.Json;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created with IntelliJ IDEA.
 * User: Benjamin
 * Date: 27/02/13
 * Time: 18:26
 * To change this template use File | Settings | File Templates.
 */

/**
 * Cette classe s'occupe de tous les appels pouvant être effectués
 * sur le module Stages, à savoir la recherche de stages.
 *
 * Auteur : benjamin.bouvier@gmail.com (2011/2012)
 */
@Entity
public class Stage extends Model {

    @Id
    private Long id;

    private String titre;

    private Integer annee; // valeur parmi 3, 4, 5, 7 (3 et 4IF), 9 (4 et 5IF), 12 (3, 4, 5)

    private Integer duree; // entre 1 et 12 inclus (12 peut indiquer plus de 12, le cas échéant)

    private String lieu;

    private String entreprise;

    private String contact;

    private String lien_fichier;

    private String description;

    public ObjectNode toJson() {
        ObjectNode json = Json.newObject();

        json.put("titre", titre);
        json.put("annee", annee);
        json.put("duree", duree);
        json.put("lieu", lieu);
        json.put("entreprise", entreprise);
        json.put("contact", contact);
        json.put("lien_fichier", lien_fichier);
        json.put("description", description);

        return json;
    }

    // Générés par ide
    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public Integer getAnnee() {
        return annee;
    }

    public void setAnnee(Integer annee) {
        this.annee = annee;
    }

    public Integer getDuree() {
        return duree;
    }

    public void setDuree(Integer duree) {
        this.duree = duree;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public String getEntreprise() {
        return entreprise;
    }

    public void setEntreprise(String entreprise) {
        this.entreprise = entreprise;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getLien_fichier() {
        return lien_fichier;
    }

    public void setLien_fichier(String lien_fichier) {
        this.lien_fichier = lien_fichier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
