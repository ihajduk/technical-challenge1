package eu.iwha.prework;

import org.junit.Test;

import java.time.LocalDate;
import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarTest {

    @Test
    public void shouldContainItems(){

        Calendar calendar = new Calendar(LocalDate.of(2016, 9, 19));

        assertThat(calendar).contains(
                LocalDate.of(2016, 9, 21),
                LocalDate.of(2016, 9, 23),
                LocalDate.of(2016, 9, 28),
                LocalDate.of(2016, 9, 30)
        );
    }

    @Test
    public void shouldProduceIndependentIterable(){
        Calendar calendar = new Calendar(LocalDate.of(2016, 9, 19));

        Iterator<LocalDate> it = calendar.iterator();
        calendar.iterator().next();

        assertThat(it.next()).isEqualTo(LocalDate.of(2016, 9, 21));

    }
}
