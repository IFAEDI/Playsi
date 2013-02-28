package models;

import com.avaje.ebean.validation.Length;
import play.db.ebean.Model;

import javax.persistence.Entity;

/**
 * Created with IntelliJ IDEA.
 * User: Benjamin
 * Date: 28/02/13
 * Time: 21:58
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Mail extends Model {

    @Length(max=15)
    private String intitule;

    @Length(max=50)
    private String email;

    private Integer priorite;

    // générés par l'IDE

    public String getIntitule() {
        return intitule;
    }

    public void setIntitule(String intitule) {
        this.intitule = intitule;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getPriorite() {
        return priorite;
    }

    public void setPriorite(Integer priorite) {
        this.priorite = priorite;
    }
}
