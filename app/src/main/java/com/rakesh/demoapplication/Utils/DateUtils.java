package com.rakesh.demoapplication.Utils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;

import com.rakesh.demoapplication.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {


    public static String getEventDate(String date) {
        String result = "";
        if (date != null && date.trim().length() > 0) {
            SimpleDateFormat inputformatter = new SimpleDateFormat("dd/MM/yyyy", Locale
                    .ENGLISH);
            SimpleDateFormat outputformatter = new SimpleDateFormat("dd,EEEE,MMM yyyy", Locale
                    .ENGLISH);
            try {
                Date dt = inputformatter.parse(date);
                result = outputformatter.format(dt);
            } catch (ParseException e) {
                e.printStackTrace();
                return " ,  , ";
            }
            return result;
        } else {
            return " ,  , ";
        }
    }


    public static String getEventDate(String sdate, String eDate) {
        String result = "";
        SimpleDateFormat inputformatter = new SimpleDateFormat("dd/MM/yyyy", Locale
                .ENGLISH);
        SimpleDateFormat outputformatter1 = new SimpleDateFormat("dd", Locale
                .ENGLISH);
        SimpleDateFormat outputformatter2 = new SimpleDateFormat("dd MMM yyyy", Locale
                .ENGLISH);
        Date mStartDate, mEndDate;
        try {
            mStartDate = inputformatter.parse(sdate);
            mEndDate = inputformatter.parse(eDate);
            String stmp = "", eTmp = "";
            stmp = outputformatter1.format(mStartDate);
            eTmp = outputformatter2.format(mEndDate);
            if (!sdate.equalsIgnoreCase(eDate)) {
                result = stmp + " - " + eTmp;
            } else {
                result = eTmp;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return " ,  , ";
        }
        return result;
    }


    public static String getNewsDate(String date) {
        String result = "";
        if (date != null && date.trim().length() > 0) {
            SimpleDateFormat inputformatter = new SimpleDateFormat("dd/MM/yyyy", Locale
                    .ENGLISH);
            SimpleDateFormat outputformatter = new SimpleDateFormat("MMMM dd, yyyy", Locale
                    .ENGLISH);
            try {
                Date dt = inputformatter.parse(date);
                result = outputformatter.format(dt);
            } catch (ParseException e) {
                e.printStackTrace();
                return "";
            }
            return result;
        } else {
            return "";
        }

    }

    public static String getMeetingsDate(String date) {
        String result = "";
        if (date != null && date.trim().length() > 0) {
            SimpleDateFormat inputformatter = new SimpleDateFormat("dd/MM/yyyy", Locale
                    .ENGLISH);
            SimpleDateFormat outputformatter = new SimpleDateFormat("dd,MMMM", Locale
                    .ENGLISH);
            try {
                Date dt = inputformatter.parse(date);
                result = outputformatter.format(dt);
            } catch (ParseException e) {
                e.printStackTrace();
                return " , ";
            }
            return result;
        } else {
            return " , ";
        }
    }


    public static void addEventToCalender(Context mContext, String EventStartDate, String
            EventStartTime, String
                                                  EventEndDate, String EventEndTime,
                                          String EventPrograme, String EventLocation) {

        if (TextUtils.isEmpty(EventStartDate))
            return;

        Logger.e("Granted", "Granted");
        //Add To Calender
        long startTime = 0, endTime = 0;
        try {

            if (EventStartDate != null && EventStartDate.trim().indexOf(" ") > 0) {
                EventStartDate = EventStartDate.split(" ")[0];
            }
            if (EventEndDate != null && EventEndDate.trim().indexOf(" ") > 0) {
                EventEndDate = EventEndDate.split(" ")[0];
            }

            if (EventStartTime != null && EventStartTime.trim().length() <= 0) {
                EventStartTime = "8:00";
            }
            if (EventEndTime != null && EventEndTime.trim().length() <= 0) {
                EventEndTime = "20:00";
            }
            startTime = getMilliSeconds(EventStartDate + " " + EventStartTime);
            endTime = getMilliSeconds(EventEndDate + " " + EventEndTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Intent intent = null;
        long eventID = getIdByName(mContext, EventPrograme);
        Logger.e("id", "" + eventID);

        if (Build.VERSION.SDK_INT >= 14) {
            if (eventID == 0) {
                intent = new Intent(Intent.ACTION_EDIT)
                        .setData(CalendarContract.Events.CONTENT_URI);
            } else {
                Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
                intent = new Intent(Intent.ACTION_VIEW)
                        .setData(uri);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(CalendarContract.Reminders.EVENT_ID,
                    CalendarContract.Events._ID);
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime);
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime);
            intent.putExtra(CalendarContract.Events.TITLE, EventPrograme);
            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, EventLocation);
            mContext.startActivity(intent);
        } else {
            intent = new Intent(Intent.ACTION_INSERT);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setType("vnd.android.cursor.item/event");
            intent.putExtra("beginTime", startTime);
            intent.putExtra("endTime", endTime);
            intent.putExtra("title", EventPrograme);
            intent.putExtra("allDay", true);
            intent.putExtra("eventLocation", EventLocation);
            mContext.startActivity(intent);
        }
    }

    private static int getIdByName(Context mContext, String eventtitle) {
        Uri eventUri;
        if (Build.VERSION.SDK_INT <= 7) {
            // the old way
            eventUri = Uri.parse("content://calendar/events");
        } else {
            // the new way
            eventUri = Uri.parse("content://com.android.calendar/events");
        }
        int result = 0;
        String projection[] = {"_id", "title"};
        Cursor cursor = mContext.getContentResolver().query(eventUri, null, null, null,
                null);
        if (cursor.moveToFirst()) {
            String calName;
            String calID;
            int nameCol = cursor.getColumnIndex(projection[1]);
            int idCol = cursor.getColumnIndex(projection[0]);
            do {
                calName = cursor.getString(nameCol);
                calID = cursor.getString(idCol);
                if (calName != null && calName.contains(eventtitle)) {
                    result = Integer.parseInt(calID);
                    break;
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return result;

    }

    public static long getMilliSeconds(String newsDate) throws ParseException {
        Date date = null;
        try {
            date = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(newsDate);
        } catch (Exception e) {
            Log.e("exc", e.toString());
        }
        return date.getTime();
    }

    public void addEventToCalender(Context mContext, String EventStartDate, String EventEndDate,
                                   String EventPrograme, String EventLocation, String Flag) {

        Logger.e("Granted", "Granted");
        //Add To Calender

        long startTime = 0, endTime = 0;
        try {
            startTime = getMilliSeconds(EventStartDate);
            if (Flag.equals("1")) {
                endTime = getMilliSeconds(EventEndDate);
            } else {
                endTime = getMilliSeconds(EventEndDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Intent intent = null;
        long eventID = getIdByName(mContext, EventPrograme);
        Logger.e("id", "" + eventID);
        if (Build.VERSION.SDK_INT >= 14) {
            if (eventID == 0) {
                intent = new Intent(Intent.ACTION_EDIT)
                        .setData(CalendarContract.Events.CONTENT_URI);
            } else {
                Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
                intent = new Intent(Intent.ACTION_VIEW)
                        .setData(uri);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(CalendarContract.Reminders.EVENT_ID,
                    CalendarContract.Events._ID);
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime);
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime);
            intent.putExtra(CalendarContract.Events.TITLE, EventPrograme);
            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, EventLocation);
            mContext.startActivity(intent);
        } else {
            intent = new Intent(Intent.ACTION_INSERT);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setType("vnd.android.cursor.item/event");
            intent.putExtra("beginTime", startTime);
            intent.putExtra("endTime", endTime);
            intent.putExtra("title", EventPrograme);
            intent.putExtra("allDay", true);
            // intent.putExtra("rrule", "FREQ=YEARLY");
            intent.putExtra("eventLocation", EventLocation);
            mContext.startActivity(intent);
        }
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        SimpleDateFormat monthformat = new SimpleDateFormat("MMMM", Locale
                .ENGLISH);
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale
                .ENGLISH);
        TextView edtMonth;
        TextView edtYear;
        Date date = null;


        public void setInput(TextView edtMonth, TextView edtYear, String date) {
            this.edtMonth = edtMonth;
            this.edtYear = edtYear;
        }


        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dpd = new DatePickerDialog(getActivity(), R.style.appCompatDialog
                    , this, year, month, day);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                dpd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }

            if (date != null) {
                dpd.getDatePicker().setMinDate(date.getTime());
            }

            return dpd;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(0);
            cal.set(year, month, day, 0, 0, 0);
            Date chosenDate = cal.getTime();
            String formattedMonth = monthformat.format(chosenDate);
            String formattedYear = yearFormat.format(chosenDate);
            edtMonth.setText(formattedMonth);
            edtYear.setText(formattedYear);
        }


    }


}
