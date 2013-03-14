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

    public static ContactEntreprise construire(ObjectNode root) {

        // TODO constantes json
        if( !root.has("id_contact") || !root.has("personne")
                || !root.get("personne").has("prenom")
                || !root.get("personne").has("nom")
                || !root.has("fonction") ) {
            return null;
        }

        ObjectNode personneJson = (ObjectNode) root.get("personne");
        Personne p = Personne.construire(personneJson, false);
        ContactEntreprise nouveau = new ContactEntreprise();

        nouveau.setNom(p.getNom());
        nouveau.setPrenom(p.getPrenom());
        nouveau.setMails(p.getMails());
        nouveau.setTelephones(p.getTelephones());

        // TODO constantes json
        Long idContact = root.get("id_contact").asLong();
        String fonction = root.get("fonction").asText();
        String commentaire = root.get("commentaire").asText();
        Integer priorite = root.get("priorite").asInt();

        ObjectNode villeJson = (ObjectNode) root.get("ville");
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
