package models;

import com.avaje.ebean.validation.Length;
import controllers.Utils.Constantes;
import org.codehaus.jackson.node.ObjectNode;
import play.db.ebean.Model;
import play.libs.Json;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

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

    @ManyToOne(fetch = FetchType.LAZY)
    private Personne proprietaire;

    @Length(max=15)
    private String intitule;

    @Length(max=50)
    private String email;

    private Integer priorite;

    public Mail() {

    }

    public Mail( ObjectNode json ) {
        this.email = json.get(Constantes.JSON_EMAIL).asText();
        this.intitule = json.get(Constantes.JSON_INTITULE).asText();
    }

    public ObjectNode toJson() {
        ObjectNode json = Json.newObject();
        json.put(Constantes.JSON_EMAIL, email);
        json.put(Constantes.JSON_INTITULE, intitule);
        json.put(Constantes.JSON_PRIORITE, priorite);
        return json;
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
