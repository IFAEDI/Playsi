package models;

import controllers.Utils.Constantes;
import controllers.Utils.DateUtils;
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
        json.put(Constantes.JSON_ID_COMMENTAIRE, id);
        json.put(Constantes.JSON_CONTENU, contenu);
        json.put(Constantes.JSON_CATEGORIE, categorie);
        json.put(Constantes.JSON_TIMESTAMP, DateUtils.formaterDate(date));
        json.put(Constantes.JSON_PERSONNE, auteur.toJsonMinimal() );
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
