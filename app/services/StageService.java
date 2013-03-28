package services;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Expression;
import com.avaje.ebean.ExpressionList;
import models.Stage;
import play.db.ebean.Model.Finder;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Benjamin
 * Date: 27/02/13
 * Time: 18:29
 * To change this template use File | Settings | File Templates.
 */
public class StageService {

    private static Finder<Long, Stage> finder = new Finder<Long, Stage>(Long.class, Stage.class);

    // TODO discuter de la fonctionnalité chercher par durée?
    public static List<Stage> chercherStages(String[] mots_cles, Integer annee, Integer duree, String lieu, String entreprise) {
        ExpressionList<Stage> exp = finder.where();

        // Créé une chaîne de 'or' pour les mots clés
        // Les mots clés sont cherchés dans titre et description
        if( mots_cles != null && mots_cles.length > 0 ) {
            boolean premierOr = true;
            Expression chaineOr = null;
            for( String m : mots_cles ) {
                Expression e = Expr.or( Expr.ilike(Stage.DB_TITRE, "%".concat(m.concat("%"))),
                        Expr.ilike(Stage.DB_DESCRIPTION, "%".concat(m.concat("%"))));
                if( premierOr ) {
                    chaineOr = e;
                    premierOr = false;
                } else {
                    chaineOr = Expr.or( chaineOr, e );
                }
            }
            exp.add(chaineOr);
        }

        if(annee != null) {
            // TODO ajouter gestion des multi années comme présent en bdd du département (7, 9)
            exp.eq(Stage.DB_ANNEE, annee);
        }

        if( duree != null ) {
            exp.gt(Stage.DB_DUREE, duree);
        }

        if( lieu != null ) {
            exp.ilike(Stage.DB_LIEU, "%".concat(lieu.concat("%")));
        }

        if( entreprise != null ) {
            exp.ilike(Stage.DB_ENTREPRISE, entreprise);
        }

        return exp.findList();
    }
}
