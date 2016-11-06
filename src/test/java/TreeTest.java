import model.Corporate;
import model.Node;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Created by iwha on 11/1/2016.
 */
public class TreeTest {
    @Test
    public void conversionTest() {
        Corporate ceo = new Corporate("Prezes Paweł");
        Corporate projectManager = new Corporate("Karolina");
        Corporate techLeadLR = new Corporate("Mateusz");
        Corporate techLeadHS = new Corporate("Karol");
        Corporate seniorLROne = new Corporate("Michał");
        Corporate seniorLRTwo = new Corporate("Marcin");
        Corporate seniorHSOne = new Corporate("Arkadiusz");
        Corporate devLROne = new Corporate("Kamil");
        Corporate devLRTwo = new Corporate("Filip");
        Corporate devHSOne = new Corporate("Maciej");
        Corporate devHSTwo = new Corporate("Janusz");
        Corporate devHSThree = new Corporate("Radek");
        ceo.addChildren(projectManager);
        projectManager.addChildren(techLeadLR, techLeadHS);
        techLeadLR.addChildren(seniorLROne, seniorLRTwo);
        techLeadHS.addChildren(seniorHSOne);
        seniorLROne.addChildren(devLROne);
        seniorLRTwo.addChildren(devLRTwo);
        seniorHSOne.addChildren(devHSOne, devHSTwo, devHSThree);

        Iterable<String> iterable = Tree.convert(ceo);

        assertThat(iterable).contains(
                "Prezes Paweł", "Karolina", "Mateusz", "Michał", "Kamil", "Marcin",
                "Karol", "Arkadiusz", "Maciej", "Janusz", "Radek"
        );
    }
}
