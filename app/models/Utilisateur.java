package models;

import com.avaje.ebean.validation.Length;
import controllers.Utils.Constantes;

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
        REGULIERE,
        CAS
    }

    public static final String DB_LOGIN = "login";
    public static final String DB_PASSWORD = "passwd";
    public static final String DB_AUTH_SERVICE = "auth_service";
    public static final String DB_BANNI = "banni";

    public String getRoleString() {
        String roleStr = null;
        if( role == Role.ADMIN ) {
            roleStr = Constantes.ROLE_ADMIN;
        } else if( role == Role.AEDI ) {
            roleStr = Constantes.ROLE_AEDI;
        } else if( role == Role.ENSEIGNANT ) {
            roleStr = Constantes.ROLE_ENSEIGNANT;
        } else if( role == Role.ENTREPRISE ) {
            roleStr = Constantes.ROLE_ENTREPRISE;
        } else if( role == Role.ETUDIANT ) {
            roleStr = Constantes.ROLE_ETUDIANT;
        } else {
            roleStr = Constantes.ROLE_INCONNU;
        }
        return roleStr;
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
