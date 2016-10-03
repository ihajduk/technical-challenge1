import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by iwha on 10/3/2016.
 */
public class CalendarTest {

    @Test
    public void testCalendar(){
        Calendar calendar = new Calendar();
        calendar.setInitDate(LocalDate.of(2016, 9, 19));
        List<LocalDate> calendarList = new ArrayList<>();
        Iterator it = calendar.iterator();
        for (int i = 0; i < 4; i++) {
            LocalDate date = (LocalDate) it.next();
            calendarList.add(date);
        }
        assertThat(calendarList).containsOnly(
                LocalDate.of(2016, 9, 21),
                LocalDate.of(2016, 9, 23),
                LocalDate.of(2016, 9, 28),
                LocalDate.of(2016, 9, 30));
    }
}
