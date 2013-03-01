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
 * Time: 22:04
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Telephone extends Model {

    @Id
    private Long id;

    @Length(max=15)
    private String intitule;

    @Length(max=15)
    private String numero;

    private Integer priorite;

    public Telephone() {

    }

    public Telephone(ObjectNode json) {
        numero = json.get("numero").asText();
        intitule = json.get("intitule").asText();
    }

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
