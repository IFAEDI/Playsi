package models;

import com.avaje.ebean.validation.Length;
import org.codehaus.jackson.node.ObjectNode;
import play.libs.Json;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created with IntelliJ IDEA.
 * User: Benjamin
 * Date: 12/03/13
 * Time: 16:53
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class ContactEntreprise extends Personne {

    @Id
    private Long id;

    @Length(max=35)
    private String fonction;

    private Integer priorite;

    private String commentaire;

    private Ville ville;

    public ObjectNode toJson() {
        ObjectNode json = Json.newObject();

        // TODO constantes JSON
        json.put("id_contact", id);

        ObjectNode personne = super.toJson();
        personne = super.jsonAjouterTelMails(json);
        json.put("personne", personne);

        json.put("ville", ville.toJson());
        json.put("commentaire", commentaire);
        json.put("fonction", fonction);
        json.put("priorite", priorite);
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
}
