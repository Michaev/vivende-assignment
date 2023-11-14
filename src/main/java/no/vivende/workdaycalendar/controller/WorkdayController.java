package no.vivende.workdaycalendar.controller;

import no.vivende.workdaycalendar.workdaycalendar.WorkdayCalendarImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@RestController
public class WorkdayController {

    @Autowired
    private WorkdayCalendarImpl workdayCalendarImpl;

    @GetMapping("/workday")
    public Date getSortedAds(@RequestParam @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date inputDate, @RequestParam float increment) {

        workdayCalendarImpl.setRecurringHoliday(new GregorianCalendar(2023, Calendar.MAY, 17));
        workdayCalendarImpl.setHoliday(new GregorianCalendar(2023, Calendar.MAY, 27));

        workdayCalendarImpl.setWorkdayStartAndStop(
                new GregorianCalendar(2023, Calendar.JANUARY, 1, 8, 0),
                new GregorianCalendar(2023, Calendar.JANUARY, 1, 16, 0)
        );

        Date endDate = workdayCalendarImpl.getWorkdayIncrement(
                inputDate,
                increment
        );

        return endDate;
    }

}
