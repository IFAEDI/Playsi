package models;

import com.avaje.ebean.validation.Length;
import play.db.ebean.Model;

import javax.persistence.Entity;

/**
 * Created with IntelliJ IDEA.
 * User: Benjamin
 * Date: 28/02/13
 * Time: 22:04
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Telephone extends Model {

    @Length(max=15)
    private String intitule;

    @Length(max=15)
    private String numero;

    private Integer priorite;

    // Généré par IDE

    public String getIntitule() {
        return intitule;
    }

    public void setIntitule(String intitule) {
        this.intitule = intitule;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Integer getPriorite() {
        return priorite;
    }

    public void setPriorite(Integer priorite) {
        this.priorite = priorite;
    }
}
