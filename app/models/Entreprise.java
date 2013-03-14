package models;

import com.avaje.ebean.validation.Length;
import controllers.Utils.Constantes;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import play.db.ebean.Model;
import play.libs.Json;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Benjamin
 * Date: 12/03/13
 * Time: 16:55
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Entreprise extends Model {

    @Id
    private Long id;

    @Length(max=35)
    private String nom;

    private String description;

    @Length(max=50)
    private String secteur;

    private String commentaire; // TODO vérifier que le champ commentaire de Entreprise est utilisé?

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "entreprise")
    private List<ContactEntreprise> contacts;

    @OneToMany(cascade = CascadeType.ALL)
    private List<CommentaireEntreprise> commentaires;

    public final static String DB_NOM = "nom";

    public ObjectNode toJson() {
        ObjectNode json = Json.newObject();

        ObjectNode jsonDescription = Json.newObject();
        jsonDescription.put(Constantes.JSON_ID_ENTREPRISE, id);
        jsonDescription.put(Constantes.JSON_NOM, nom);
        jsonDescription.put(Constantes.JSON_SECTEUR, secteur);
        jsonDescription.put(Constantes.JSON_DESCRIPTION, description);
        jsonDescription.put(Constantes.JSON_COMMENTAIRE, commentaire);
        json.put(Constantes.JSON_ENTREPRISE, jsonDescription);

        ArrayNode jsonContacts = new ArrayNode(JsonNodeFactory.instance);
        for( ContactEntreprise ce : contacts ) {
            jsonContacts.add( ce.toJson() );
        }
        json.put(Constantes.JSON_CONTACTS, jsonContacts);

        ArrayNode jsonComm = new ArrayNode(JsonNodeFactory.instance);
        for( CommentaireEntreprise ce: commentaires ) {
            jsonComm.add( ce.toJson() );
        }
        json.put(Constantes.JSON_COMMENTAIRES, jsonComm );

        return json;
    }

    // généré par l'IDE

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSecteur() {
        return secteur;
    }

    public void setSecteur(String secteur) {
        this.secteur = secteur;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public List<ContactEntreprise> getContacts() {
        return contacts;
    }

    public void setContacts(List<ContactEntreprise> contacts) {
        this.contacts = contacts;
    }

    public List<CommentaireEntreprise> getCommentaires() {
        return commentaires;
    }

    public void setCommentaires(List<CommentaireEntreprise> commentaires) {
        this.commentaires = commentaires;
    }
}
