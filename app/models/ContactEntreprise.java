package models;

import com.avaje.ebean.validation.Length;
import controllers.Utils.Constantes;
import org.codehaus.jackson.node.ObjectNode;
import play.libs.Json;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: Benjamin
 * Date: 12/03/13
 * Time: 16:53
 * To change this template use File | Settings | File Templates.
 */
@Entity
@DiscriminatorValue("2")
public class ContactEntreprise extends Personne {

    @Length(max=35)
    private String fonction;

    private Integer priorite;

    private String commentaire;

    @OneToOne(cascade = CascadeType.ALL)
    private Ville ville;

    @ManyToOne
    private Entreprise entreprise;

    public static final String DB_FONCTION = "fonction";

    public static ContactEntreprise construire(ObjectNode root) {

        if( !root.has(Constantes.JSON_ID_CONTACT) || !root.has(Constantes.JSON_PERSONNE)
                || !root.get(Constantes.JSON_PERSONNE).has(Constantes.JSON_PRENOM)
                || !root.get(Constantes.JSON_PERSONNE).has(Constantes.JSON_NOM)
                || !root.has(Constantes.JSON_FONCTION) ) {
            return null;
        }

        ObjectNode personneJson = (ObjectNode) root.get(Constantes.JSON_PERSONNE);
        Personne p = Personne.construire(personneJson, false);
        ContactEntreprise nouveau = new ContactEntreprise();

        nouveau.setNom(p.getNom());
        nouveau.setPrenom(p.getPrenom());
        nouveau.setMails(p.getMails());
        nouveau.setTelephones(p.getTelephones());

        Long idContact = root.get(Constantes.JSON_ID_CONTACT).asLong();
        String fonction = root.get(Constantes.JSON_FONCTION).asText();
        String commentaire = root.get(Constantes.JSON_COMMENTAIRE).asText();
        Integer priorite = root.get(Constantes.JSON_PRIORITE).asInt();

        ObjectNode villeJson = (ObjectNode) root.get(Constantes.JSON_VILLE);
        Ville ville = Ville.construire(villeJson);

        nouveau.setVille(ville);
        nouveau.setCommentaire(commentaire);
        nouveau.setFonction(fonction);
        nouveau.setId(idContact);
        nouveau.setPriorite(priorite);

        return nouveau;
    }

    public ObjectNode toJson() {
        ObjectNode json = Json.newObject();

        json.put(Constantes.JSON_ID_CONTACT, id);

        ObjectNode personneJson = super.toJson();
        personneJson = super.jsonAjouterTelMails(personneJson);
        json.put(Constantes.JSON_PERSONNE, personneJson);

        if( ville != null ) {
            json.put(Constantes.JSON_VILLE, ville.toJson());
        }
        json.put(Constantes.JSON_COMMENTAIRE, commentaire);
        json.put(Constantes.JSON_FONCTION, fonction);
        json.put(Constantes.JSON_PRIORITE, priorite);
        return json;
    }

    // généré par l'IDE

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFonction() {
        return fonction;
    }

    public void setFonction(String fonction) {
        this.fonction = fonction;
    }

    public Integer getPriorite() {
        return priorite;
    }

    public void setPriorite(Integer priorite) {
        this.priorite = priorite;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Ville getVille() {
        return ville;
    }

    public void setVille(Ville ville) {
        this.ville = ville;
    }

    public Entreprise getEntreprise() {
        return entreprise;
    }

    public void setEntreprise(Entreprise entreprise) {
        this.entreprise = entreprise;
    }
}
