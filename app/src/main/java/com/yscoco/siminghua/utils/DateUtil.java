package com.yscoco.siminghua.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日期工具类
 */
public class DateUtil {

    public static String[] WEEK = new String[]{"天", "一", "二", "三", "四", "五", "六"};

    private static final long ONE_SECOND = 1000;
    private static final long ONE_MINUTE = ONE_SECOND * 60;
    private static final long ONE_HOUR = ONE_MINUTE * 60;
    private static final long ONE_DAY = ONE_HOUR * 24;
    public static final String YEAR_MONTH_DAY = "yyyy-MM-dd";
    public static final String YEAR_MONTH_DAY_HOUR_MINUTE_SECOND = "yyyy-MM-dd HH:mm:ss";

    public static final int DAY_OF_YEAR = 365;

    // 日期格式为 2018-07-03 17:04:58
    public static final String PATTERN_DATE = "yyyy年MM月dd日";
    public static final String PATTERN_TIME = "HH:mm:ss";
    public static final String PATTERN_SPLIT = " ";
    public static final String PATTERN = PATTERN_DATE + PATTERN_SPLIT + PATTERN_TIME;

    /**
     * String 转换 Date
     *
     * @param str
     * @param format
     * @return
     */
    public static Date string2Date(String str, String format) {
        try {
            return new SimpleDateFormat(format).parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    /**
     * Date（long） 转换 String
     *
     * @param time
     * @param format
     * @return
     */
    public static String date2String(long time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String s = sdf.format(time);
        return s;
    }

    /**
     * long 去除 时分秒
     * 时分秒全部为0
     *
     * @param date
     * @return
     */
    public static long getYearMonthDay(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取目标时间和当前时间之间的差距
     *
     * @param date
     * @return
     */
    public static String getTimestampString(Date date) {
        Date curDate = new Date();
        long splitTime = curDate.getTime() - date.getTime();
        if (splitTime < (30 * ONE_DAY)) {
            if (splitTime < ONE_MINUTE) {
                return "刚刚";
            }
            if (splitTime < ONE_HOUR) {
                return String.format("%d分钟前", splitTime / ONE_MINUTE);
            }
            if (splitTime < ONE_DAY) {
                return String.format("%d小时前", splitTime / ONE_HOUR);
            }
            return String.format("%d天前  %s", splitTime / ONE_DAY, date2HHmm(date));
        }
        String result;
        result = "M月d日 HH:mm";
        return (new SimpleDateFormat(result, Locale.CHINA)).format(date);
    }

    /**
     * 24小时制 转换 12小时制
     *
     * @param time
     * @return
     */
    public static String time24To12(String time) {
        String str[] = time.split(":");
        int h = Integer.valueOf(str[0]);
        int m = Integer.valueOf(str[1]);
        String sx;
        if (h < 1) {
            h = 12;
            sx = "上午";
        } else if (h < 12) {
            sx = "上午";
        } else if (h < 13) {
            sx = "下午";
        } else {
            sx = "下午";
            h -= 12;
        }
        return String.format("%d:%02d%s", h, m, sx);
    }

    /**
     * Date 转换 HH
     *
     * @param date
     * @return
     */
    public static String date2HH(Date date) {
        return new SimpleDateFormat("HH").format(date);
    }

    /**
     * Date 转换 HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String date2HHmm(Date date) {
        return new SimpleDateFormat("HH:mm").format(date);
    }

    /**
     * Date 转换 HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String date2HHmmss(Date date) {
        return new SimpleDateFormat("HH:mm:ss").format(date);
    }

    /**
     * Date 转换 MM.dd
     *
     * @param date
     * @return
     */
    public static String date2MMdd(Date date) {
        return new SimpleDateFormat("MM.dd").format(date);
    }

    /**
     * Date 转换 yyyy.MM.dd
     *
     * @param date
     * @return
     */
    public static String date2yyyyMMdd(Date date) {
        return new SimpleDateFormat("yyyy.MM.dd").format(date);
    }

    /**
     * Date 转换 MM月dd日 星期
     *
     * @param date
     * @return
     */
    public static String date2MMddWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return new SimpleDateFormat("MM月dd日 星期").format(date) + WEEK[dayOfWeek - 1];
    }

    /**
     * Date 转换 yyyy年MM月dd日 星期
     *
     * @param date
     * @return
     */
    public static String date2yyyyMMddWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return new SimpleDateFormat("yyyy年MM月dd日 星期").format(date) + WEEK[dayOfWeek - 1];
    }

    /**
     * 时间毫秒转换为日期
     *
     * @param timeMillis
     * @param format
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDate(long timeMillis, String format) {
        Date date = new Date(timeMillis);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }


    public static String getShortTime(String dateStr) {
        String str;

        Date date = str2date(dateStr);
        Date curDate = new Date();

        long durTime = curDate.getTime() - date.getTime();
        int dayDiff = calculateDayDiff(date, curDate);

        if (durTime <= 10 * ONE_MINUTE) {
            str = "刚刚";
        } else if (durTime < ONE_HOUR) {
            str = durTime / ONE_MINUTE + "分钟前";
        } else if (dayDiff == 0) {
            str = durTime / ONE_HOUR + "小时前";
        } else if (dayDiff == -1) {
            str = "昨天" + DateFormat.format("HH:mm", date);
        } else if (isSameYear(date, curDate) && dayDiff < -1) {
            str = DateFormat.format("MM-dd", date).toString();
        } else {
            str = DateFormat.format("yyyy-MM", date).toString();
        }

        return str;
    }


    /**
     * 获取日期 PATTERN_DATE 部分
     */
    public static String getDate(String date) {
        if (TextUtils.isEmpty(date) || !date.contains(PATTERN_SPLIT)) {
            return "";
        }
        return date.split(PATTERN_SPLIT)[0];
    }

    /**
     * 原有日期上累加月
     *
     * @return 累加后的日期 PATTERN_DATE 部分
     */
    public static String addMonth(String date, int moonCount) {
        //如果date为空 就用当前时间
        if (TextUtils.isEmpty(date)) {
            SimpleDateFormat df = new SimpleDateFormat(PATTERN_DATE + PATTERN_SPLIT + PATTERN_TIME);
            date = df.format(new Date());
        }
        Calendar calendar = str2calendar(date);
        calendar.add(Calendar.MONTH, moonCount);
        return getDate(calendar2str(calendar));
    }

    /**
     * 计算天数差
     */
    public static int calculateDayDiff(Date targetTime, Date compareTime) {
        boolean sameYear = isSameYear(targetTime, compareTime);
        if (sameYear) {
            return calculateDayDiffOfSameYear(targetTime, compareTime);
        } else {
            int dayDiff = 0;

            // 累计年数差的整年天数
            int yearDiff = calculateYearDiff(targetTime, compareTime);
            dayDiff += yearDiff * DAY_OF_YEAR;

            // 累计同一年内的天数
            dayDiff += calculateDayDiffOfSameYear(targetTime, compareTime);

            return dayDiff;
        }
    }

    /**
     * 计算同一年内的天数差
     */
    public static int calculateDayDiffOfSameYear(Date targetTime, Date compareTime) {
        if (targetTime == null || compareTime == null) {
            return 0;
        }

        Calendar tarCalendar = Calendar.getInstance();
        tarCalendar.setTime(targetTime);
        int tarDayOfYear = tarCalendar.get(Calendar.DAY_OF_YEAR);

        Calendar compareCalendar = Calendar.getInstance();
        compareCalendar.setTime(compareTime);
        int comDayOfYear = compareCalendar.get(Calendar.DAY_OF_YEAR);

        return tarDayOfYear - comDayOfYear;
    }

    /**
     * 计算年数差
     */
    public static int calculateYearDiff(Date targetTime, Date compareTime) {
        if (targetTime == null || compareTime == null) {
            return 0;
        }

        Calendar tarCalendar = Calendar.getInstance();
        tarCalendar.setTime(targetTime);
        int tarYear = tarCalendar.get(Calendar.YEAR);

        Calendar compareCalendar = Calendar.getInstance();
        compareCalendar.setTime(compareTime);
        int comYear = compareCalendar.get(Calendar.YEAR);

        return tarYear - comYear;
    }

    /**
     * 计算月数差
     *
     * @param targetTime
     * @param compareTime
     * @return
     */
    public static int calculateMonthDiff(String targetTime, String compareTime) {
        return calculateMonthDiff(str2date(targetTime, PATTERN_DATE),
                str2date(compareTime, PATTERN_DATE));
    }

    /**
     * 计算月数差
     *
     * @param targetTime
     * @param compareTime
     * @return
     */
    public static int calculateMonthDiff(Date targetTime, Date compareTime) {
        Calendar tarCalendar = Calendar.getInstance();
        tarCalendar.setTime(targetTime);
        int tarYear = tarCalendar.get(Calendar.YEAR);
        int tarMonth = tarCalendar.get(Calendar.MONTH);

        Calendar compareCalendar = Calendar.getInstance();
        compareCalendar.setTime(compareTime);
        int comYear = compareCalendar.get(Calendar.YEAR);
        int comMonth = compareCalendar.get(Calendar.MONTH);
        return ((tarYear - comYear) * 12 + tarMonth - comMonth);

    }

    /**
     * 是否为同一年
     */
    public static boolean isSameYear(Date targetTime, Date compareTime) {
        if (targetTime == null || compareTime == null) {
            return false;
        }

        Calendar tarCalendar = Calendar.getInstance();
        tarCalendar.setTime(targetTime);
        int tarYear = tarCalendar.get(Calendar.YEAR);

        Calendar compareCalendar = Calendar.getInstance();
        compareCalendar.setTime(compareTime);
        int comYear = compareCalendar.get(Calendar.YEAR);

        return tarYear == comYear;
    }

    public static Date str2date(String str, String format) {
        Date date = null;
        try {
            if (str != null) {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                date = sdf.parse(str);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date str2date(String str) {
        return str2date(str, PATTERN);
    }

    public static String date2str(Date date) {
        return date2str(date, PATTERN);
    }

    public static String date2str(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
        return sdf.format(date);
    }

    public static Calendar str2calendar(String str) {
        Calendar calendar = null;
        Date date = str2date(str);
        if (date != null) {
            calendar = Calendar.getInstance();
            calendar.setTime(date);
        }
        return calendar;
    }


    public static Calendar str2calendar(String str, String format) {
        Calendar calendar = null;
        Date date = str2date(str, format);
        if (date != null) {
            calendar = Calendar.getInstance();
            calendar.setTime(date);
        }
        return calendar;
    }

    public static String calendar2str(Calendar calendar) {
        return date2str(calendar.getTime());
    }

    public static String calendar2str(Calendar calendar, String format) {
        return date2str(calendar.getTime(), format);
    }

}
