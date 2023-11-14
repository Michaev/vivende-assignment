package no.vivende.workdaycalendar.workdaycalendar;

import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class WorkdayCalendarImpl implements WorkdayCalendar {

    private List<Calendar> holidays;
    private List<Calendar> recurringHolidays;

    private Calendar workdayStart;
    private Calendar workdayStop;

    public WorkdayCalendarImpl() {

        holidays = new ArrayList<>();
        recurringHolidays = new ArrayList<>();

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
        int milliSeconds = (int) (rest * getWorkdayInMilliseconds());

        Calendar newDate = new GregorianCalendar();
        newDate.setTime(startDate);

        adjustToWorkingHours(newDate, reverse, milliSeconds);

        for(int i = 0; i < fullDays; i++) {
            newDate.add(Calendar.DAY_OF_MONTH, reverse ? -1 : 1);
            if(!isValidDate(newDate)) {
                i--;
            }
        }

        while(!isValidDate(newDate)) {
            newDate.add(Calendar.DAY_OF_MONTH, 1);
        }

        return newDate.getTime();
    }

    private boolean isValidDate(Calendar date) {

        boolean onWeekend = date.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || date.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;

        if(onWeekend) return false;

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

    private void adjustToWorkingHours(Calendar date, boolean reverse, int milliseconds) {

        if(reverse) {
            if(date.get(Calendar.HOUR_OF_DAY) < workdayStart.get(Calendar.HOUR_OF_DAY)) {
                date.add(Calendar.DAY_OF_MONTH, -1);
                setTimeToWorkdayStop(date);

            } else if (date.get(Calendar.HOUR_OF_DAY) > workdayStop.get(Calendar.HOUR_OF_DAY)) {
                setTimeToWorkdayStop(date);
            } else {
                int elapsedMs = (int) getElapsedMillisecondsOfWorkday(date);
                if(milliseconds > elapsedMs) {
                    milliseconds -= elapsedMs;
                    date.add(Calendar.DAY_OF_MONTH, -1);
                    setTimeToWorkdayStop(date);
                }
            }

            date.add(Calendar.MILLISECOND, -milliseconds);

        } else {
            if(date.get(Calendar.HOUR_OF_DAY) < workdayStart.get(Calendar.HOUR_OF_DAY)) {
                setTimeToWorkdayStart(date);

            } else if(date.get(Calendar.HOUR_OF_DAY) > workdayStop.get(Calendar.HOUR_OF_DAY)) {
                date.add(Calendar.DAY_OF_MONTH, 1);
                setTimeToWorkdayStart(date);
            } else {
                int remainingMs = (int) getRemainingMillisecondsOfWorkday(date);
                if(milliseconds > remainingMs) {
                    milliseconds -= remainingMs;
                    date.add(Calendar.DAY_OF_MONTH, 1);
                    setTimeToWorkdayStart(date);
                }
            }

            date.add(Calendar.MILLISECOND, milliseconds);
        }

    }

    public long getWorkdayInMilliseconds() {
        return this.workdayStop.getTimeInMillis() - this.workdayStart.getTimeInMillis();
    }

    private void setTimeToWorkdayStart(Calendar date) {
        date.set(Calendar.HOUR_OF_DAY, workdayStart.get(Calendar.HOUR_OF_DAY));
        date.set(Calendar.MINUTE, workdayStart.get(Calendar.MINUTE));
        date.set(Calendar.SECOND, workdayStart.get(Calendar.SECOND));
    }

    private void setTimeToWorkdayStop(Calendar date) {
        date.set(Calendar.HOUR_OF_DAY, workdayStop.get(Calendar.HOUR_OF_DAY));
        date.set(Calendar.MINUTE, workdayStop.get(Calendar.MINUTE));
        date.set(Calendar.SECOND, workdayStop.get(Calendar.SECOND));
    }

    private long getRemainingMillisecondsOfWorkday(Calendar date) {
        Calendar tempDate = (Calendar) workdayStop.clone();

        tempDate.set(Calendar.HOUR_OF_DAY, date.get(Calendar.HOUR_OF_DAY));
        tempDate.set(Calendar.MINUTE, date.get(Calendar.MINUTE));
        tempDate.set(Calendar.SECOND, date.get(Calendar.SECOND));

        return workdayStop.getTimeInMillis() - tempDate.getTimeInMillis();
    }

    private long getElapsedMillisecondsOfWorkday(Calendar date) {
        Calendar tempDate = (Calendar) workdayStart.clone();

        tempDate.set(Calendar.HOUR_OF_DAY, date.get(Calendar.HOUR_OF_DAY));
        tempDate.set(Calendar.MINUTE, date.get(Calendar.MINUTE));
        tempDate.set(Calendar.SECOND, date.get(Calendar.SECOND));

        return tempDate.getTimeInMillis() - workdayStart.getTimeInMillis();
    }
}
