package models;

import com.avaje.ebean.validation.Length;
import controllers.Utils.Constantes;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import play.db.ebean.Model;
import play.libs.Json;

import javax.persistence.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Benjamin
 * Date: 28/02/13
 * Time: 17:01
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Personne extends Model {

    public Personne() {

    }

    public enum Role {
        ETUDIANT("Etudiant"),
        ENSEIGNANT("Enseignant"),
        ENTREPRISE("Entreprise"),
        ADMIN("Administrateur"),
        AEDI("Membre de l'AEDI");

        private String intitule = null;
        Role( String nom ) {
            this.intitule = nom;
        }

        public static Role parOrdinal( int ordinal ) {
            for( Role r : Role.values() ) {
                if( r.ordinal() == ordinal ) {
                    return r;
                }
            }
            return null;
        }

        public String getIntitule() {
            return this.intitule;
        }
    }

    @Id
    protected Long id;

    @Length(max=35)
    protected String nom;

    @Length(max=35)
    protected String prenom;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "proprietaire")
    protected List<Mail> mails;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "proprietaire")
    protected List<Telephone> telephones;

    protected Boolean premiereConnexion;
    protected Role role;

    public ObjectNode toJson() {
        ObjectNode json = Json.newObject();
        json.put(Constantes.JSON_NOM, nom);
        json.put(Constantes.JSON_PRENOM, prenom);
        return json;
    }

    public ObjectNode jsonAjouterTelMails( ObjectNode json ) {
        ArrayNode jMails = new ArrayNode(JsonNodeFactory.instance);
        for( Mail m : mails ) {
            jMails.add( m.toJson() );
        }
        json.put(Constantes.JSON_MAILS, jMails);

        ArrayNode jTels = new ArrayNode(JsonNodeFactory.instance);
        for( Telephone t : telephones ) {
            jTels.add( t.toJson() );
        }
        json.put(Constantes.JSON_TELEPHONES, jTels);
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

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Boolean getPremiereConnexion() {
        return premiereConnexion;
    }

    public void setPremiereConnexion(Boolean premiereConnexion) {
        this.premiereConnexion = premiereConnexion;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Mail> getMails() {
        return mails;
    }

    public void setMails(List<Mail> mails) {
        this.mails = mails;
    }

    public List<Telephone> getTelephones() {
        return telephones;
    }

    public void setTelephones(List<Telephone> telephones) {
        this.telephones = telephones;
    }
}
