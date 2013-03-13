package models;

import com.avaje.ebean.validation.Length;
import controllers.Utils.Constantes;
import org.codehaus.jackson.node.ObjectNode;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created with IntelliJ IDEA.
 * User: Benjamin
 * Date: 28/02/13
 * Time: 00:02
 * To change this template use File | Settings | File Templates.
 */
@Entity
@DiscriminatorValue("U")
public class Utilisateur extends Personne {

    @Length(max=35)
    private String login;

    @Length(max=50)
    private String passwd;
    private TYPE_AUTH auth_service;
    private Boolean banni;

    public enum TYPE_AUTH {
        REGULIERE("Authentification régulière"),
        CAS("CAS");

        private String intitule = null;
        TYPE_AUTH(String nom) {
            this.intitule = nom;
        }

        public String getIntitule() {
            return this.intitule;
        }
    }

    public static final String DB_LOGIN = "login";
    public static final String DB_PASSWORD = "passwd";
    public static final String DB_AUTH_SERVICE = "auth_service";
    public static final String DB_BANNI = "banni";

    public String getRoleString() {
        return role.getIntitule();
    }

    public String getAuthServerString() {
        return auth_service.getIntitule();
    }

    public ObjectNode toJsonMinimal() {
        ObjectNode json = super.toJson();
        json.put(Constantes.JSON_LOGIN, login);
        json.put(Constantes.JSON_SERVICE, auth_service.ordinal());
        json.put(Constantes.JSON_ROLE_ORDINAL, role.ordinal());
        json.put(Constantes.JSON_ID, id);
        return json;
    }

    /**
     * Utilisateur au format JSON, avec tous les champs (mails et tels en plus)
     * @return json minimal + mails[][ intitule, email ] et tels[][ intitule, numero ]
     */
    public ObjectNode toJsonFull() {
        ObjectNode json = toJsonMinimal();
        json = jsonAjouterTelMails(json);
        return json;
    }

    // généré automatiquement

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public TYPE_AUTH getAuth_service() {
        return auth_service;
    }

    public void setAuth_service(TYPE_AUTH auth_service) {
        this.auth_service = auth_service;
    }

    public Boolean getBanni() {
        return banni;
    }

    public void setBanni(Boolean banni) {
        this.banni = banni;
    }
}
