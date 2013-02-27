/**
 * Created with IntelliJ IDEA.
 * User: Benjamin
 * Date: 27/02/13
 * Time: 21:17
 * To change this template use File | Settings | File Templates.
 */

import com.avaje.ebean.Ebean;
import models.Stage;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.db.ebean.Model;
import play.libs.Yaml;

import java.util.List;
import java.util.Map;

public class Global extends GlobalSettings {

    static public void loadFixtures() {
        Logger.info("Insertion de valeurs en bdd...");
        Map<String, List<Object> > fixtures = (Map<String, List<Object>>) Yaml.load("fixtures.yml");

        Model.Finder<Long, Stage> finder = new Model.Finder<Long, Stage>(Long.class, Stage.class);
        if(finder.findRowCount() == 0) {
            Ebean.save(fixtures.get("stages"));
        }

        Logger.info("Insertion de valeurs en bdd: Fait.");
    }

    @Override
    public void onStart(Application app) {
        // en mode développement, insérer des valeurs en bdd
        if( app.isDev() ) {
            loadFixtures();
        }
    }
}
