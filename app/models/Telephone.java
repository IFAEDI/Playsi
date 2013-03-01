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
 * Time: 22:04
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Telephone extends Model {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Personne proprietaire;

    @Length(max=15)
    private String intitule;

    @Length(max=15)
    private String numero;

    private Integer priorite;

    public Telephone() {

    }

    public Telephone(ObjectNode json) {
        numero = json.get(Constantes.JSON_NUMERO).asText();
        intitule = json.get(Constantes.JSON_INTITULE).asText();
    }

    public ObjectNode toJson() {
        ObjectNode json = Json.newObject();
        json.put(Constantes.JSON_NUMERO, numero);
        json.put(Constantes.JSON_INTITULE, intitule);
        json.put(Constantes.JSON_PRIORITE, priorite);
        return json;
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
