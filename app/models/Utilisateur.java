package models;

import controllers.Securite;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created with IntelliJ IDEA.
 * User: Benjamin
 * Date: 28/02/13
 * Time: 00:02
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Utilisateur extends Model {
    @Id
    private Long id;

    private String login;
    private String passwd;
    private Integer auth_service;
    private Boolean banni;

    // TODO prenom, nom, role, méthode d'authentification
    public String getPrenom() { return "John"; }
    public String getNom() { return "Doe";}
    public String getRole() { return "Admin"; }
    public int getAuthentificationMethode() { return Securite.AUTH_REGULIERE; }

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

    public Integer getAuth_service() {
        return auth_service;
    }

    public void setAuth_service(Integer auth_service) {
        this.auth_service = auth_service;
    }

    public Boolean getBanni() {
        return banni;
    }

    public void setBanni(Boolean banni) {
        this.banni = banni;
    }
}
