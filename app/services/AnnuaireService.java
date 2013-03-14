package services;

import com.avaje.ebean.Ebean;
import controllers.Securite;
import models.*;
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

    public static List<Entreprise> rechercherContacts(String motsclesStr) {
        // TODO renvoyer au format attendu
        // TODO faire la recherche par tous les champs
        // TODO trouve les entreprises qui vérifient les conditions, n'isole pas les contacts.

        String[] motscles = motsclesStr.split(" ");
        /*
        Model.Finder<Long, ContactEntreprise> finder = new Model.Finder<Long, ContactEntreprise>(Long.class, ContactEntreprise.class);
        List<ContactEntreprise> contacts =
                finder.fetch("entreprise")
                .where()
                    .ilike("nom", "%"+motscles[0]+"%")
                .findList();
        */
        Model.Finder<Long, Entreprise> finder = new Model.Finder<Long, Entreprise>(Long.class, Entreprise.class);
        List<Entreprise> entreprises = finder.fetch("contacts").where().ilike("contacts.nom", "%"+motscles[0]+"%").findList();

        return entreprises;
    }

    public static class MajContactResult {
        public Statut statut;
        public Long nouvelId;
    }

    public static MajContactResult majContact(ContactEntreprise arguments, Long idEntreprise) {
        ContactEntreprise contact = null;
        contact = Ebean.find(ContactEntreprise.class).where().eq("id", arguments.getId()).findUnique();
        boolean estMiseAJour = contact != null;

        if( estMiseAJour ) {
            contact.setNom(arguments.getNom());
            contact.setPrenom(arguments.getPrenom());
            contact.setCommentaire(arguments.getCommentaire());
            contact.setFonction(arguments.getFonction());
            contact.setPriorite(arguments.getPriorite());

            for( Mail m: contact.getMails() ) {
                m.delete();
            }
            for( Telephone t: contact.getTelephones() ) {
                t.delete();
            }

            contact.setMails(arguments.getMails());
            contact.setTelephones(arguments.getTelephones());
        } else {
            contact = arguments;
        }

        Model.Finder<Long, Entreprise> finder = new Model.Finder<Long, Entreprise>(Long.class, Entreprise.class);
        Entreprise e = finder.byId( idEntreprise );
        if( e== null) {
            MajContactResult result = new MajContactResult();
            result.statut = Statut.ENTREPRISE_NON_TROUVEE;
            return result;
        }

        // mise à jour de la ville
        if( arguments.getVille() != null ) {
            if( estMiseAJour ) {
                Ville ancienneVille = contact.getVille();
                if( ancienneVille == null ) {
                    ancienneVille = new Ville();
                }

                boolean modificationVille = false;
                if( ancienneVille.getCodePostal() == null || !ancienneVille.getCodePostal().equals(arguments.getVille().getCodePostal()) ) {
                    modificationVille = true;
                    ancienneVille.setCodePostal(arguments.getVille().getCodePostal());
                }
                if( ancienneVille.getLibelle() == null || !ancienneVille.getLibelle().equals(arguments.getVille().getLibelle()) ) {
                    modificationVille = true;
                    ancienneVille.setLibelle(arguments.getVille().getLibelle());
                }
                if( ancienneVille.getPays() == null || !ancienneVille.getPays().equals(arguments.getVille().getPays()) ) {
                    modificationVille = true;
                    ancienneVille.setPays(arguments.getVille().getPays());
                }

                if( modificationVille ) {
                    contact.setVille(ancienneVille);
                }
            } else {
                contact.setVille(arguments.getVille());
            }
        } else {
            if( estMiseAJour && contact.getVille() != null ) {
                Ville aSupprimer = contact.getVille();
                contact.setVille(null);
                contact.save();
                aSupprimer.delete();
            }
        }

        if( estMiseAJour ) {
            contact.save();
        } else {
            List<ContactEntreprise> contacts = e.getContacts();
            if( contacts == null ) {
                contacts = new ArrayList<ContactEntreprise>();
            }
            contacts.add(contact);
            e.setContacts(contacts);
            e.save();
        }

        MajContactResult result = new MajContactResult();
        result.statut = Statut.OK;
        if( estMiseAJour ) {
            result.nouvelId = -1L;
        } else {
            result.nouvelId = contact.getId();
        }
        return result;
    }
}
