import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;

/**
 * Created by iwha on 10/2/2016.
 */
public final class Calendar implements Iterable<LocalDate> {
    private final LocalDate initDate;

    public Calendar(LocalDate initDate) {
        this.initDate = initDate;
    }

    @Override
    public Iterator iterator() {
        return new Itr();
    }

    private final class Itr implements Iterator<LocalDate> {
        private LocalDate forwardDate = initDate;

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public LocalDate next() {
            do{
                forwardDate = forwardDate.plus(1, ChronoUnit.DAYS);
            }while(!forwardDate.getDayOfWeek().equals(DayOfWeek.WEDNESDAY) && !forwardDate.getDayOfWeek().equals(DayOfWeek.FRIDAY));
            return forwardDate;
        }
    }
}
