import models.Stage;
import org.junit.Before;
import org.junit.Test;
import services.StageService;

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

        String[] mots_cles = {"Implémentation", "algo"};
        assertThat(StageService.chercherStages(mots_cles, null, null, null, null)).hasSize(2);

        assertThat(StageService.chercherStages(mots_cles, null, null, "Paris", null)).isEmpty();
        assertThat(StageService.chercherStages(mots_cles, null, null, "Lyon", null)).hasSize(1);
    }

}
