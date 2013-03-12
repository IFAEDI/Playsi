package models;

import com.avaje.ebean.validation.Length;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created with IntelliJ IDEA.
 * User: Benjamin
 * Date: 12/03/13
 * Time: 16:42
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Ville extends Model {

    @Id
    private Long id;

    @Length(max=75)
    private String libelle;

    @Length(max=8)
    private String codePostal;

    @Length(max=30)
    private String pays;

    // généré par l'IDE

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }
}
