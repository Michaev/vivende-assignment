package no.vivende.workdaycalendar.workdaycalendar;

import java.util.Calendar;
import java.util.Date;

public interface WorkdayCalendar {

    void setHoliday(Calendar date);

    void setRecurringHoliday(Calendar date);

    void setWorkdayStartAndStop(Calendar start, Calendar stop);

    Date getWorkdayIncrement(Date startDate, float incrementInWorkdays);

}
