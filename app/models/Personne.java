package models;

import com.avaje.ebean.validation.Length;
import play.db.ebean.Model;

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
@Inheritance(strategy = InheritanceType.JOINED)
public class Personne extends Model {

    public Personne() {

    }

    public enum Role {
        ETUDIANT,
        ENSEIGNANT,
        ENTREPRISE,
        ADMIN,
        AEDI
    }

    @Id
    protected Long id;

    @Length(max=35)
    protected String nom;

    @Length(max=35)
    protected String prenom;

    @OneToMany(cascade = CascadeType.ALL)
    protected List<Mail> mails;

    @OneToMany(cascade = CascadeType.ALL)
    protected List<Telephone> telephones;

    protected Boolean premiereConnexion;
    protected Role role;

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
}
