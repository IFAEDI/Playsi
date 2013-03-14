package services;

import controllers.Securite;
import models.CommentaireEntreprise;
import models.ContactEntreprise;
import models.Entreprise;
import play.db.ebean.Model;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Benjamin
 * Date: 13/03/13
 * Time: 13:41
 * To change this template use File | Settings | File Templates.
 */
public class AnnuaireService {

    private static Entreprise.Finder<Long, Entreprise> efinder = new Model.Finder<Long, Entreprise>(Long.class, Entreprise.class);
    private static ContactEntreprise.Finder<Long, ContactEntreprise> contFinder = new Model.Finder<Long, ContactEntreprise>(Long.class, ContactEntreprise.class);
    private static CommentaireEntreprise.Finder<Long, CommentaireEntreprise> commFinder = new Model.Finder<Long, CommentaireEntreprise>(Long.class, CommentaireEntreprise.class);

    public static class AnnuaireRenderInfos {
        public List<Entreprise> entreprises;
        public List<String> secteurs;
        public List<String> fonctions;
    }

    public static AnnuaireRenderInfos getAnnuaireRenderInfos() {
        AnnuaireRenderInfos infos = new AnnuaireRenderInfos();
        infos.entreprises = efinder.orderBy(Entreprise.DB_NOM).findList();

        Set<String> secteursUniques = new HashSet<String>();
        for( Entreprise e : infos.entreprises ) {
            secteursUniques.add( e.getSecteur() );
        }
        infos.secteurs = new ArrayList<String>(secteursUniques);

        // TODO meilleure requête pour récupérer les fonctions uniques (SELECT DISTINCT fonction)
        List<ContactEntreprise> contacts = contFinder.all();
        Set<String> fonctionsUniques = new HashSet<String>();
        for( ContactEntreprise ce : contacts ) {
            fonctionsUniques.add(ce.getFonction());
        }
        infos.fonctions = new ArrayList<String>(fonctionsUniques);

        return infos;
    }

    public static boolean nomEntrepriseEstDejaPris( String nom ) {
        return efinder.where().eq(Entreprise.DB_NOM, nom).findUnique() != null;
    }

    public enum Statut {
        OK,
        ENTREPRISE_NON_TROUVEE,
        ERREUR_INTERNE,
        COMMENTAIRE_NON_TROUVE,
        CONTACT_NON_TROUVE,
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

    public static Statut supprimerEntreprise(Long id) {
        Entreprise entreprise = efinder.byId(id);
        if( entreprise == null ) {
            return Statut.ENTREPRISE_NON_TROUVEE;
        } else {
            entreprise.delete();
            return Statut.OK;
        }
    }

    public static Statut supprimerCommentaire(Long id) {
        CommentaireEntreprise comm = commFinder.byId(id);
        if( comm == null ) {
            return Statut.COMMENTAIRE_NON_TROUVE;
        } else {
            comm.delete();
            return Statut.OK;
        }
    }

    public static Statut supprimerContact(Long id) {
        ContactEntreprise contact = contFinder.byId(id);
        if( contact == null ) {
            return Statut.CONTACT_NON_TROUVE;
        } else {
            contact.delete();
            return Statut.OK;
        }
    }

    public static class CreationEntrepriseResult {
        public Statut statut;
        public boolean estCreation;
        public Long id;
    }

    public static CreationEntrepriseResult ajouterOuModifierEntreprise(Long id, String nom, String secteur, String description) {
        CreationEntrepriseResult result = new CreationEntrepriseResult();
        Entreprise entreprise = efinder.byId(id);
        if( entreprise == null ) {
            // création
            result.estCreation = true;
            entreprise = new Entreprise();
        } else {
            // modification
            result.estCreation = false;
        }

        entreprise.setDescription(description);
        entreprise.setNom(nom);
        entreprise.setSecteur(secteur);

        entreprise.save();
        result.statut = Statut.OK;
        result.id = entreprise.getId();

        return result;
    }

    public static class CreationCommentaireResult {
        public Statut statut;
        public Long id;
    }

    public static CreationCommentaireResult ajouterCommentaire(Long idEntreprise, String contenu, Integer categorie) {
        CreationCommentaireResult result= new CreationCommentaireResult();

        Entreprise entreprise = efinder.byId(idEntreprise);
        if( entreprise == null ) {
            result.statut = Statut.ENTREPRISE_NON_TROUVEE;
        } else {
            List<CommentaireEntreprise> commentaires = entreprise.getCommentaires();

            CommentaireEntreprise nouveauCommentaire = new CommentaireEntreprise();
            nouveauCommentaire.setAuteur(Securite.utilisateur());
            nouveauCommentaire.setCategorie(categorie);
            nouveauCommentaire.setContenu(contenu);
            nouveauCommentaire.setDate(new Date());

            commentaires.add( nouveauCommentaire );
            entreprise.setCommentaires( commentaires );
            entreprise.save();

            result.statut = Statut.OK;
            result.id = nouveauCommentaire.getId(); // TODO vérifier id bien reçu
        }

        return result;
    }
}
