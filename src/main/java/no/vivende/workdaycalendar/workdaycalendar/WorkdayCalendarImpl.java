package no.vivende.workdaycalendar.workdaycalendar;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class WorkdayCalendarImpl implements WorkdayCalendar {

    private List<Calendar> holidays;
    private List<Calendar> recurringHolidays;

    private Calendar workdayStart;
    private Calendar workdayStop;

    private int MILLISECONDS_IN_DAY = 1000 * 60 * 60 * 24;

    public WorkdayCalendarImpl() {

        holidays = new ArrayList<>();
        recurringHolidays = new ArrayList<>();

        setHoliday(new GregorianCalendar(2023, Calendar.NOVEMBER, 12));
        setHoliday(new GregorianCalendar(2023, Calendar.NOVEMBER, 13));

        setHoliday(new GregorianCalendar(2023, Calendar.NOVEMBER, 18));
        setHoliday(new GregorianCalendar(2023, Calendar.NOVEMBER, 19));

        setRecurringHoliday(new GregorianCalendar(2023, Calendar.MAY, 17));
        setRecurringHoliday(new GregorianCalendar(2023, Calendar.MAY, 27));
        setRecurringHoliday(new GregorianCalendar(2023, Calendar.DECEMBER, 24));

        setWorkdayStartAndStop(
            new GregorianCalendar(2023, Calendar.JANUARY, 1, 8, 0),
            new GregorianCalendar(2023, Calendar.JANUARY, 1, 16, 0)
        );

        System.out.println("init workday calendar");

    }

    @Override
    public void setHoliday(Calendar date) {
        this.holidays.add(date);
    }

    @Override
    public void setRecurringHoliday(Calendar date) {
        this.recurringHolidays.add(date);
    }

    @Override
    public void setWorkdayStartAndStop(Calendar start, Calendar stop) {
        this.workdayStart = start;
        this.workdayStop = stop;
    }

    @Override
    public Date getWorkdayIncrement(Date startDate, float increment) {

        boolean reverse = increment < 0;
        increment = Math.abs(increment);

        int fullDays = (int) Math.floor(increment);
        float rest = increment - fullDays;
        int milliSeconds = (int) (rest * MILLISECONDS_IN_DAY);

        Calendar newDate = new GregorianCalendar();
        newDate.setTime(startDate);

        newDate.add(Calendar.MILLISECOND, milliSeconds);
        adjustToWorkingHours(newDate);

        for(int i = 0; i < fullDays; i++) {
            newDate.add(Calendar.DAY_OF_MONTH, reverse ? -1 : 1);
        }

        while(!isValidDate(newDate)) {
            newDate.add(Calendar.DAY_OF_MONTH, 1);
        }

        return newDate.getTime();
    }

    private boolean isValidDate(Calendar date) {

        boolean onHoliday = this.holidays.stream().anyMatch(h -> {
            if(h.get(Calendar.DAY_OF_MONTH) != date.get(Calendar.DAY_OF_MONTH)) return false;

            if(h.get(Calendar.MONTH) != date.get(Calendar.MONTH)) return false;

            return h.get(Calendar.YEAR) == date.get(Calendar.YEAR);
        });

        if(onHoliday) return false;

        boolean onRecurringHoliday = this.recurringHolidays.stream().anyMatch(h -> {
            if(h.get(Calendar.DAY_OF_MONTH) != date.get(Calendar.DAY_OF_MONTH)) return false;

            return h.get(Calendar.MONTH) == date.get(Calendar.MONTH);
        });

        if(onRecurringHoliday) return false;

        return true;
    }

    private void adjustToWorkingHours(Calendar date) {

        if(date.get(Calendar.HOUR_OF_DAY) < workdayStart.get(Calendar.HOUR_OF_DAY)) {
            date.set(Calendar.HOUR_OF_DAY, workdayStart.get(Calendar.HOUR_OF_DAY));
        }

        if(date.get(Calendar.HOUR_OF_DAY) >= workdayStop.get(Calendar.HOUR_OF_DAY)) {
            date.add(Calendar.DAY_OF_MONTH, 1);
            date.set(Calendar.HOUR_OF_DAY, workdayStart.get(Calendar.HOUR_OF_DAY));
        }

    }

}
