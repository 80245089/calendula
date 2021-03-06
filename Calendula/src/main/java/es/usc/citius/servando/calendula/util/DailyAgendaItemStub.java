package es.usc.citius.servando.calendula.util;

import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.usc.citius.servando.calendula.persistence.DailyScheduleItem;
import es.usc.citius.servando.calendula.persistence.Medicine;
import es.usc.citius.servando.calendula.persistence.Presentation;
import es.usc.citius.servando.calendula.persistence.Routine;
import es.usc.citius.servando.calendula.persistence.ScheduleItem;


/**
 * Created by joseangel.pineiro on 11/15/13.
 */
public class DailyAgendaItemStub {

    public static final String TAG = DailyAgendaItemStub.class.getName();
    public boolean isSpacer = false;
    public boolean isEmptyPlaceholder = false;
    public boolean isNext = false;
    //public int hour;
    public boolean hasEvents;
    public List<DailyAgendaItemStubElement> meds;
    public int primaryColor = -1;
    public int secondaryColor = -1;
    public boolean hasColors = false;
    public String time = "";
    public String title = "";
    public Long id = -1l;
    public int hour;
    public int minute;


    public DailyAgendaItemStub(String time) {
        this.time = time;
    }


    public static List<DailyAgendaItemStub> fromHour(int hour) {


        List<Routine> routines = Routine.findInHour(hour);
        List<DailyAgendaItemStub> items = new ArrayList<DailyAgendaItemStub>(routines.size());

        if (!routines.isEmpty()) {
            for (Routine r : routines) {
                // create an ItemStub for the current hour
                DailyAgendaItemStub item = new DailyAgendaItemStub(r.time().toString("kk:mm"));
                // find routines in this our

                // Find doses off all routines in this hour
                List<ScheduleItem> doses = r.scheduleItems();
                item.id = r.getId();
                item.title = r.name();
                item.hour = r.time().getHourOfDay();
                item.minute = r.time().getMinuteOfHour();
                if (doses.size() > 0) {
                    item.hasEvents = true;
                    item.meds = new ArrayList<DailyAgendaItemStubElement>();
                    for (ScheduleItem scheduleItem : doses) {
                        int minute = r.time().getMinuteOfHour();
                        Medicine med = scheduleItem.schedule().medicine();
                        DailyAgendaItemStubElement el = new DailyAgendaItemStubElement();
                        el.medName = med.name();
                        el.dose = scheduleItem.dose();
                        el.displayDose = scheduleItem.displayDose();
                        el.res = med.presentation().getDrawable();
                        el.presentation = med.presentation();
                        el.minute = minute < 10 ? "0" + minute : String.valueOf(minute);
                        el.taken = DailyScheduleItem.findByScheduleItem(scheduleItem).takenToday();
                        item.meds.add(el);
                    }
                    Collections.sort(item.meds);
                }
                items.add(item);
            }
        } else {
            items.add(new DailyAgendaItemStub(new LocalTime(hour, 0).toString("kk:mm")));
        }

        // create an ItemStub for the current hour
//        DailyAgendaItemStub item = new DailyAgendaItemStub(hour);
//        // find routines in this our
//
//        // Find doses off all routines in this hour
//        List<ScheduleItem> doses = new ArrayList<ScheduleItem>();
//
//        for (Routine routine : routines) {
//            doses.addAll(routine.scheduleItems());
//        }
//
//        if (doses.size() > 0) {
//            item.hasEvents = true;
//            item.meds = new ArrayList<DailyAgendaItemStubElement>();
//            for (ScheduleItem scheduleItem : doses) {
//
//                int minute = scheduleItem.routine().time().getMinuteOfHour();
//
//                Medicine med = scheduleItem.schedule().medicine();
//                DailyAgendaItemStubElement el = new DailyAgendaItemStubElement();
//                el.medName = med.name();
//                el.dose = (int) scheduleItem.dose();
//                el.res = med.presentation().getDrawable();
//                el.presentation = med.presentation();
//                el.minute = minute < 10 ? "0" + minute : String.valueOf(minute);
//                el.taken = DailyScheduleItem.findByScheduleItem(scheduleItem).takenToday();
//                item.meds.add(el);
//            }
//            Collections.sort(item.meds);
//        }
//        Log.d(TAG, "Schedules in hour: " + doses.size());
        return items;
    }

    public static class DailyAgendaItemStubElement implements Comparable<DailyAgendaItemStubElement> {

        public String medName;
        public String minute;
        public String displayDose;
        public double dose;

        public boolean taken;
        public Presentation presentation;
        public int res;


        @Override
        public int compareTo(DailyAgendaItemStubElement other) {
            int result = minute.compareTo(other.minute);
            if (result == 0)
                result = taken ? 0 : 1;
            if (result == 0)
                result = medName.compareTo(other.medName);

            return result;
        }
    }

}
