package services;

import models.ContactEntreprise;
import models.Entreprise;
import play.db.ebean.Model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Benjamin
 * Date: 13/03/13
 * Time: 13:41
 * To change this template use File | Settings | File Templates.
 */
public class AnnuaireService {

    private static Entreprise.Finder<Long, Entreprise> efinder = new Model.Finder<Long, Entreprise>(Long.class, Entreprise.class);
    private static ContactEntreprise.Finder<Long, ContactEntreprise> ceFinder = new Model.Finder<Long, ContactEntreprise>(Long.class, ContactEntreprise.class);

    public static class AnnuaireRenderInfos {
        public List<Entreprise> entreprises;
        public List<String> secteurs;
        public List<String> fonctions;
    }

    public static AnnuaireRenderInfos getAnnuaireRenderInfos() {
        AnnuaireRenderInfos infos = new AnnuaireRenderInfos();
        infos.entreprises = efinder.all();

        Set<String> secteursUniques = new HashSet<String>();
        for( Entreprise e : infos.entreprises ) {
            secteursUniques.add( e.getSecteur() );
        }
        infos.secteurs = new ArrayList<String>(secteursUniques);

        // TODO meilleure requête pour récupérer les fonctions uniques (SELECT DISTINCT fonction)
        List<ContactEntreprise> contacts = ceFinder.all();
        Set<String> fonctionsUniques = new HashSet<String>();
        for( ContactEntreprise ce : contacts ) {
            fonctionsUniques.add(ce.getFonction());
        }
        infos.fonctions = new ArrayList<String>(fonctionsUniques);

        return infos;
    }

    public static boolean nomEntrepriseEstDejaPris( String nom ) {
        return efinder.where().eq(Entreprise.DB_NOM, nom).findUnique() == null;
    }

    public enum Statut {
        OK,
        ENTREPRISE_NON_TROUVEE,
        ERREUR_INTERNE,
    }

    public static class GetEntrepriseResult {
        public Statut statut;
        public Entreprise entreprise;
    }

    public static GetEntrepriseResult getEntreprise(Long id) {
        GetEntrepriseResult result = new GetEntrepriseResult();
        Entreprise entreprise = efinder.byId(id);
        if( entreprise == null ) {
            result.statut = Statut.ENTREPRISE_NON_TROUVEE;
        } else {
            result.statut = Statut.OK;
            result.entreprise = entreprise;
        }
        return result;
    }
}
