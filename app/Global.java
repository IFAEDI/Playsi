/**
 * Created with IntelliJ IDEA.
 * User: Benjamin
 * Date: 27/02/13
 * Time: 21:17
 * To change this template use File | Settings | File Templates.
 */

import com.avaje.ebean.Ebean;
import models.Personne;
import models.Stage;
import models.Utilisateur;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.db.ebean.Model;
import play.libs.Yaml;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import java.util.List;
import java.util.Map;

public class Global extends GlobalSettings {

    static public void loadDevFixtures() {
        Logger.info("Insertion de valeurs en bdd...");
        Map<String, List<Object> > fixtures = (Map<String, List<Object>>) Yaml.load("fixtures.yml");

        Model.Finder<Long, Stage> stagesFinder = new Model.Finder<Long, Stage>(Long.class, Stage.class);
        if(stagesFinder.findRowCount() == 0) {
            Ebean.save(fixtures.get("stages"));
        }

        Model.Finder<Long, Utilisateur> utilisateurFinder = new Model.Finder<Long, Utilisateur>(Long.class, Utilisateur.class);
        if( utilisateurFinder.findRowCount() == 0 ) {
            Ebean.save(fixtures.get("users"));
        }

        Logger.info("Insertion de valeurs en bdd: Fait.");
    }

    static private void loadRootFixture() {
        if( Ebean.find(Utilisateur.class).where().eq("role", Personne.Role.ADMIN).findRowCount() == 0) {
            Logger.info("Insertion de l'utilisateur root...");
            Map<String, List<Object> > fixtures = (Map<String, List<Object>>) Yaml.load("root.yml");
            Ebean.save(fixtures.get("users"));
            Logger.info("Chargement de l'utilisateur root effectué.");
        }
    }

    @Override
    public void onStart(Application app) {
        // en mode développement, insérer des valeurs en bdd
        if( app.isDev() ) {
            loadDevFixtures();
        }
        loadRootFixture();
    }

    @Override
    public Result onBadRequest(Http.RequestHeader request, String error) {
        return Results.badRequest("La requête est invalide (arguments manquants, méthode incorrecte,...)");
    }

    @Override
    public Result onHandlerNotFound(Http.RequestHeader request) {
        return Results.notFound("Page non trouvée.");
    }

}
