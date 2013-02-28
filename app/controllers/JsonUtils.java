package controllers;

import org.codehaus.jackson.node.ObjectNode;
import play.libs.Json;

/**
 * Created with IntelliJ IDEA.
 * User: Benjamin
 * Date: 28/02/13
 * Time: 13:21
 * To change this template use File | Settings | File Templates.
 */
public class JsonUtils {
    public enum Statut {
        ERREUR,
        OK
    }

    private static final String ERREUR_STR = "error";
    private static final String OK_STR = "ok";

    public static ObjectNode genererReponseJson(Statut statut, String message) {
        ObjectNode json = Json.newObject();
        String statut_str = null;
        if( statut == Statut.ERREUR ) {
            statut_str = ERREUR_STR;
        } else if( statut == Statut.OK ) {
            statut_str = OK_STR;
        }
        json.put("statut", statut_str);
        json.put("mesg", message);
        return json;
    }
}
