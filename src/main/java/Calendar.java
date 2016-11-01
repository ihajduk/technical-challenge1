import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Iterator;

import static java.time.DayOfWeek.WEDNESDAY;

final class Calendar implements Iterable<LocalDate> {
    private final LocalDate initDate;

    Calendar(LocalDate initDate) {
        this.initDate = initDate;
    }

    @Override
    public Iterator<LocalDate> iterator() {
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
            if(forwardDate.getYear()>2016) return null;
            switch (forwardDate.getDayOfWeek()) {
                case WEDNESDAY:
                    forwardDate = forwardDate.with(TemporalAdjusters.next(DayOfWeek.FRIDAY)); break;
                case FRIDAY:
                    forwardDate = forwardDate.with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY)); break;
                default: {
                    forwardDate = forwardDate.getDayOfWeek().equals(DayOfWeek.THURSDAY) ? forwardDate.with(TemporalAdjusters.next(DayOfWeek.FRIDAY))
                            : forwardDate.with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY));
                }
            }
            return forwardDate;
        }
    }
}
