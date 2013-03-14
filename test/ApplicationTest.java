import controllers.Utils.Constantes;
import controllers.routes;
import models.*;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import play.db.ebean.Model;
import play.libs.Json;
import play.mvc.Result;
import services.StageService;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;


/**
*
* Simple (JUnit) tests that can call all parts of a play app.
* If you are interested in mocking a whole application, see the wiki for more details.
*
*/
public class ApplicationTest {

    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
        Global.loadFixtures();
    }

    @Test
    public void testRechercheStages() {
        List<Stage> stages = StageService.chercherStages(null, null, null, null, null);
        assertThat(stages).isNotNull();
        assertThat(stages).isNotEmpty();
        assertThat(stages.get(0).getTitre()).isEqualTo("Implémentation d'un algorithme de kéké");

        assertThat(StageService.chercherStages(null, 5, null, null, null)).isNotEmpty();
        assertThat(StageService.chercherStages(null, null, 3, null, null)).isNotEmpty();
        assertThat(StageService.chercherStages(null, null, null, "Paris", null)).isNotEmpty();

        String[] mots_cles = {"implémentation", "algo"};
        assertThat(StageService.chercherStages(mots_cles, null, null, null, null)).hasSize(2);

        assertThat(StageService.chercherStages(mots_cles, null, null, "Paris", null)).isEmpty();
        assertThat(StageService.chercherStages(mots_cles, null, null, "Lyon", null)).hasSize(1);
    }

    @Test
    public void testLoginOk() {
        Result result = callAction(
                routes.ref.StaticPages.login("root", "435b41068e8665513a20070c033b08b9c66e4332")
        );
        assert( status(result) == 200 );
        ObjectNode collectedJson = (ObjectNode) Json.parse(contentAsString(result));
        assertThat( collectedJson.has(Constantes.JSON_STATUT) );
        assertThat( collectedJson.get(Constantes.JSON_STATUT).asText() ).isEqualTo(Constantes.JSON_OK_STR);
        assertThat( session(result).containsKey(Constantes.SESSION_ID) );
        assertThat( session(result).get(Constantes.SESSION_ID) ).isNotEmpty();
    }

    @Test
    public void testLoginNok() {
        Result result = callAction(
                routes.ref.StaticPages.login("root", "tort")
                // et le tort tue
                // et le tue meurt
                // et le meurt trie
                // et le tri, c'est Raptor
                // et le tort tue, etc.
        );

        assert( status(result) == 200 );
        ObjectNode collectedJson = (ObjectNode) Json.parse(contentAsString(result));
        assertThat( collectedJson.has(Constantes.JSON_STATUT) );
        assertThat( collectedJson.get(Constantes.JSON_STATUT).asText() ).isNotEqualTo(Constantes.JSON_OK_STR);
        assertThat( ! session(result).containsKey(Constantes.SESSION_ID) );
    }

    @Test
    public void testSaveContact() {

        Entreprise entreprise = new Entreprise();
        entreprise.setNom("Corp");
        entreprise.setDescription("Une entreprise spécialisée dans qqch de spécial.");
        entreprise.setSecteur("Conseil");
        entreprise.save();

        ContactEntreprise ce1 = new ContactEntreprise();
        ce1.setNom("Patrick");
        ce1.setPrenom("John");
        ce1.setCommentaire("Il a la classe avec un nom pareil.");

        List<Mail> mce1 = new ArrayList<Mail>();
        Mail ce1m1 = new Mail();
        ce1m1.setIntitule("Personnel");
        ce1m1.setEmail("john@patrick.com");
        mce1.add(ce1m1);

        Mail ce1m2 = new Mail();
        ce1m2.setIntitule("Pro");
        ce1m2.setEmail("john.patrick@pro.org");
        mce1.add(ce1m2);

        ce1.setMails(mce1);
        assertThat(ce1.getMails()).isNotNull();

        Model.Finder<Long, Entreprise> finder = new Model.Finder<Long, Entreprise>(Long.class, Entreprise.class);
        Entreprise chargee = finder.byId(1L);

        assertThat(chargee).isNotNull();
        assertThat(chargee.getNom()).isEqualTo("Corp");

        List<ContactEntreprise> sesContacts = chargee.getContacts();
        assertThat(sesContacts).isEmpty();
        sesContacts = new ArrayList<ContactEntreprise>();
        sesContacts.add(ce1);
        chargee.setContacts(sesContacts);
        chargee.save();

        Entreprise rechargee = finder.byId(1L);
        assertThat(rechargee).isNotNull();
        assertThat(rechargee.getContacts()).isNotEmpty();
        assertThat(rechargee.getContacts().size()).isEqualTo(1);
        assertThat(rechargee.getContacts().get(0).getNom()).isEqualTo("Patrick");
        assertThat(rechargee.getContacts().get(0).getMails()).isNotEmpty();
        assertThat(rechargee.getContacts().get(0).getMails().size()).isEqualTo(2);
        assertThat(rechargee.getContacts().get(0).getMails().get(0).getEmail()).isEqualTo("john@patrick.com");
    }

    @Test
    public void testSaveUtilisateur() {

        Utilisateur ce1 = new Utilisateur();
        ce1.setNom("Patrick");
        ce1.setPrenom("John");

        assertThat(ce1.getId()).isNotNull();
        assertThat(ce1.getId()).isEqualTo(2L); // déjà l'administrateur dans la base

        List<Mail> mce1 = new ArrayList<Mail>();
        Mail ce1m1 = new Mail();
        ce1m1.setIntitule("Personnel");
        ce1m1.setEmail("john@patrick.com");
        mce1.add(ce1m1);

        Mail ce1m2 = new Mail();
        ce1m2.setIntitule("Pro");
        ce1m2.setEmail("john.patrick@pro.org");
        mce1.add(ce1m2);

        ce1.setMails(mce1);
        assertThat(ce1.getMails()).isNotNull();

        ce1.save();

        Model.Finder<Long, Utilisateur> finder = new Model.Finder<Long, Utilisateur>(Long.class, Utilisateur.class);
        Utilisateur charge = finder.where().eq("nom", "Patrick").findUnique();

        assertThat(charge).isNotNull();
        assertThat(charge.getNom()).isEqualTo("Patrick");
        assertThat(charge.getMails()).isNotEmpty();
        assertThat(charge.getMails().get(0).getEmail()).isEqualTo("john@patrick.com");
    }
}
