package controllers.Utils;

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
    public enum JsonStatut {
        ERREUR,
        OK
    }

    public static ObjectNode genererReponseJson(JsonStatut statut, String message) {
        ObjectNode json = Json.newObject();
        String statut_str = null;
        if( statut == JsonStatut.ERREUR ) {
            statut_str = Constantes.JSON_ERREUR_STR;
        } else if( statut == JsonStatut.OK ) {
            statut_str = Constantes.JSON_OK_STR;
        }
        json.put(Constantes.JSON_STATUT, statut_str);
        json.put(Constantes.JSON_MESSAGE, message);
        return json;
    }
}
