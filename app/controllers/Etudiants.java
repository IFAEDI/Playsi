package controllers;

import models.Stage;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.StageService;
import views.html.etudiants.evenements;
import views.html.etudiants.stages;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Benjamin
 * Date: 27/02/13
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class Etudiants extends Controller {

    public static Result entretiens() {
        // TODO
        return TODO;
    }

    public static Result stages() {
        return ok(stages.render());
    }

    public static Result apiStages() {
        // TODO try to see if there's less complicated

        String[] mots_cles = null;
        Integer annee = null;
        Integer duree = null;
        String lieu = null;
        String entreprise = null;

        Map<String,String[]> map = request().body().asFormUrlEncoded();
        if( map.containsKey("mots_cles") ) {
            String mots_cles_str = map.get("mots_cles")[0];
            if( mots_cles_str.isEmpty() ) {
                mots_cles = null;
            } else {
                mots_cles = map.get("mots_cles")[0].split(" ");
            }
        }
        if( map.containsKey("annee") ) {
            String anneeStr = map.get("annee")[0];
            if( !anneeStr.isEmpty() )
                annee = Integer.parseInt( map.get("annee")[0] );
            else
                duree = null;
        }
        if( map.containsKey("duree") ) {
            String dureeStr = map.get("duree")[0];
            if( !dureeStr.isEmpty() )
                duree = Integer.parseInt( map.get("duree")[0] );
            else
                duree = null;
        }
        if( map.containsKey("lieu") ) {
            lieu = map.get("lieu")[0];
            if( lieu.isEmpty() ) {
                lieu = null;
            }
        }
        if( map.containsKey("entreprise") ) {
            entreprise = map.get("entreprise")[0];
            if( entreprise.isEmpty() ) {
                entreprise = null;
            }
        }

        List<Stage> stages = StageService.chercherStages(mots_cles, annee, duree, lieu, entreprise);

        // conversion en json
        ObjectNode json = Json.newObject();
        json.put("code", "ok");
        ArrayNode jsonStages = new ArrayNode(JsonNodeFactory.instance);
        for( Stage s : stages ) {
            jsonStages.add( s.toJson() );
        }
        json.put("stages", jsonStages);
        return ok(json);
    }

    public static Result cvs() {
        // TODO
        return TODO;
    }

    public static Result evenements() {
        return ok(evenements.render());
    }
}
