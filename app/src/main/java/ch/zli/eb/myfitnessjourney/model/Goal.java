package ch.zli.eb.myfitnessjourney.model;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Date;

public class Goal implements Serializable {
    int id;
    String name;
    LocalTime time;
    boolean reminders;
    Date startDate;
    Date endDate;
    boolean started;

    public Goal(String name, LocalTime time, boolean reminders, Date startDate, Date endDate, boolean started) {
        this.name = name;
        this.time = time;
        this.reminders = reminders;
        this.startDate = startDate;
        this.endDate = endDate;
        this.started = started;
    }

    public Goal(int id, String name, LocalTime time, boolean reminders, Date startDate, Date endDate, boolean started) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.reminders = reminders;
        this.startDate = startDate;
        this.endDate = endDate;
        this.started = started;
    }

    public Goal() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public boolean isReminders() {
        return reminders;
    }

    public void setReminders(boolean reminders) {
        this.reminders = reminders;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
