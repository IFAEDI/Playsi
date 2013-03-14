package models;

import org.codehaus.jackson.node.ObjectNode;
import play.db.ebean.Model;
import play.libs.Json;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Benjamin
 * Date: 12/03/13
 * Time: 16:51
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class CommentaireEntreprise extends Model{

    @Id
    private Long id;

    private String contenu;

    private Date date;

    private Integer categorie;

    @ManyToOne
    private Utilisateur auteur;

    public ObjectNode toJson() {
        ObjectNode json = Json.newObject();
        // TODO constantes JSON
        json.put("id_commentaire", id);
        json.put("contenu", contenu);
        json.put("categorie", categorie);
        json.put("timestamp", date.toString()); // TODO vérifier que ça marche, sinon date format
        json.put("personne", auteur.toJsonMinimal() ); // TODO vérifier rôle ordinal ou rôle chaine
        return json;
    }

    // généré par l'IDE

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getCategorie() {
        return categorie;
    }

    public void setCategorie(Integer categorie) {
        this.categorie = categorie;
    }

    public Utilisateur getAuteur() {
        return auteur;
    }

    public void setAuteur(Utilisateur auteur) {
        this.auteur = auteur;
    }
}
