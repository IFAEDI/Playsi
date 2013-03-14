package models;

import com.avaje.ebean.validation.Length;
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

    public ObjectNode toJson() {
        ObjectNode json = Json.newObject();

        // TODO constantes JSON
        json.put("id_contact", id);

        ObjectNode personneJson = super.toJson();
        personneJson = super.jsonAjouterTelMails(personneJson);
        json.put("personne", personneJson);

        if( ville != null ) {
            json.put("ville", ville.toJson());
        }
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

    public Entreprise getEntreprise() {
        return entreprise;
    }

    public void setEntreprise(Entreprise entreprise) {
        this.entreprise = entreprise;
    }
}
