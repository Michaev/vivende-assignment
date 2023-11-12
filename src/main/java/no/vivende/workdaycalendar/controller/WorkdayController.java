package no.vivende.workdaycalendar.controller;

import no.vivende.workdaycalendar.workdaycalendar.WorkdayCalendarImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class WorkdayController {

    @Autowired
    private WorkdayCalendarImpl workdayCalendarImpl;

    @GetMapping("/workday")
    public Date getSortedAds(@RequestParam @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date inputDate, @RequestParam float increment) {

        return workdayCalendarImpl.getWorkdayIncrement(
            inputDate,
            increment
        );
    }

}
