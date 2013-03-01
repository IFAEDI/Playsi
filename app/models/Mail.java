package models;

import com.avaje.ebean.validation.Length;
import org.codehaus.jackson.node.ObjectNode;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created with IntelliJ IDEA.
 * User: Benjamin
 * Date: 28/02/13
 * Time: 21:58
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Mail extends Model {

    @Id
    private Long id;

    @Length(max=15)
    private String intitule;

    @Length(max=50)
    private String email;

    private Integer priorite;

    public Mail() {

    }

    public Mail( ObjectNode json ) {
        this.email = json.get("email").asText();
        this.intitule = json.get("intitule").asText();
    }

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
