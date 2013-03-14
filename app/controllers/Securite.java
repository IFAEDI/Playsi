package controllers;

import controllers.Utils.Constantes;
import models.Utilisateur;
import play.cache.Cache;
import play.mvc.Http;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Created with IntelliJ IDEA.
 * User: Benjamin
 * Date: 28/02/13
 * Time: 13:54
 */
public class Securite extends Security.Authenticator {

    @Override
    public String getUsername(Context ctx) {
        String sid = (String) ctx.session().get(Constantes.SESSION_ID);
        if( sid != null ) {
            Utilisateur utilisateur = (Utilisateur) Cache.get(sid.concat(Constantes.SESSION_UTILISATEUR_SUFFIXE));
            if( utilisateur == null ) {
                ctx.session().clear();
                return null;
            } else {
                return sid;
            }
        } else {
            return null;
        }
    }

    @Override
    public Result onUnauthorized(Context ctx) {
        return redirect(routes.StaticPages.authRequise());
    }

    public static Boolean estConnecte() {
        String sid = (String) Http.Context.current().session().get(Constantes.SESSION_ID);
        if( sid != null ) {
            Utilisateur utilisateur = (Utilisateur) Cache.get(sid.concat(Constantes.SESSION_UTILISATEUR_SUFFIXE));
            if( utilisateur == null ) {
                Http.Context.current().session().clear();
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public static Utilisateur utilisateur() {
        String sid = (String) Http.Context.current().session().get(Constantes.SESSION_ID);
        Utilisateur utilisateur = (Utilisateur) Cache.get(sid.concat(Constantes.SESSION_UTILISATEUR_SUFFIXE));
        return utilisateur;
    }
}