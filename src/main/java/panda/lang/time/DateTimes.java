package panda.lang.time;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.TimeZone;

import panda.lang.TimeZones;

/**
 * <p>
 * A suite of utilities surrounding the use of the {@link java.util.Calendar} and
 * {@link java.util.Date} object.
 * </p>
 * <p>
 * DateUtils contains a lot of common methods considering manipulations of Dates or Calendars. Some
 * methods require some extra explanation. The truncate, ceiling and round methods could be
 * considered the Math.floor(), Math.ceil() or Math.round versions for dates This way date-fields
 * will be ignored in bottom-up order. As a complement to these methods we've introduced some
 * fragment-methods. With these methods the Date-fields will be ignored in top-down order. Since a
 * date without a year is not a valid date, you have to decide in what kind of date-field you want
 * your result, for instance milliseconds or days.
 * </p>
 */
public class DateTimes {

	/**
	 * Number of milliseconds in a standard minute.
	 */
	public static final int SEC_MINUTE = 60;
	/**
	 * Number of milliseconds in a standard hour.
	 */
	public static final int SEC_HOUR = 60 * SEC_MINUTE;
	/**
	 * Number of milliseconds in a standard day.
	 */
	public static final int SEC_DAY = 24 * SEC_HOUR;
	/**
	 * Number of milliseconds in a standard week.
	 */
	public static final int SEC_WEEK = SEC_DAY * 7;
	/**
	 * Number of milliseconds in a standard month (30 days).
	 */
	public static final int SEC_MONTH = SEC_DAY * 30;
	/**
	 * Number of milliseconds in a standard year (365 dys).
	 */
	public static final int SEC_YEAR = SEC_DAY * 365;

	/**
	 * Number of milliseconds in a standard second.
	 */
	public static final int MS_SECOND = 1000;
	/**
	 * Number of milliseconds in a standard minute.
	 */
	public static final int MS_MINUTE = 60 * MS_SECOND;
	/**
	 * Number of milliseconds in a standard hour.
	 */
	public static final int MS_HOUR = 60 * MS_MINUTE;
	/**
	 * Number of milliseconds in a standard day.
	 */
	public static final int MS_DAY = 24 * MS_HOUR;
	/**
	 * Number of milliseconds in a standard week.
	 */
	public static final long MS_WEEK = MS_DAY * 7L;
	/**
	 * Number of milliseconds in a standard month (30 days).
	 */
	public static final long MS_MONTH = MS_DAY * 30L;
	/**
	 * Number of milliseconds in a standard year (365 dys).
	 */
	public static final long MS_YEAR = MS_DAY * 365L;

	/**
	 * This is half a month, so this represents whether a date is in the top or bottom half of the
	 * month.
	 */
	public static final int SEMI_MONTH = 1001;

	private static final int[][] fields = { { Calendar.MILLISECOND }, { Calendar.SECOND }, { Calendar.MINUTE },
			{ Calendar.HOUR_OF_DAY, Calendar.HOUR }, { Calendar.DATE, Calendar.DAY_OF_MONTH, Calendar.AM_PM
			/* Calendar.DAY_OF_YEAR, Calendar.DAY_OF_WEEK, Calendar.DAY_OF_WEEK_IN_MONTH */
			}, { Calendar.MONTH, DateTimes.SEMI_MONTH }, { Calendar.YEAR }, { Calendar.ERA } };

	/**
	 * A week range, starting on Sunday.
	 */
	public static final int RANGE_WEEK_SUNDAY = 1;
	/**
	 * A week range, starting on Monday.
	 */
	public static final int RANGE_WEEK_MONDAY = 2;
	/**
	 * A week range, starting on the day focused.
	 */
	public static final int RANGE_WEEK_RELATIVE = 3;
	/**
	 * A week range, centered around the day focused.
	 */
	public static final int RANGE_WEEK_CENTER = 4;
	/**
	 * A month range, the week starting on Sunday.
	 */
	public static final int RANGE_MONTH_SUNDAY = 5;
	/**
	 * A month range, the week starting on Monday.
	 */
	public static final int RANGE_MONTH_MONDAY = 6;

	/**
	 * Constant marker for truncating.
	 */
	private static final int MODIFY_TRUNCATE = 0;
	/**
	 * Constant marker for rounding.
	 */
	private static final int MODIFY_ROUND = 1;
	/**
	 * Constant marker for ceiling.
	 */
	private static final int MODIFY_CEILING = 2;

	/**
	 * The UTC time zone (often referred to as GMT). This is private as it is mutable.
	 */
	private static final TimeZone UTC_TIME_ZONE = TimeZones.GMT;

	// -----------------------------------------------------------------------
	/**
	 * ISO8601 formatter for date-time without time zone. The format used is
	 * <tt>yyyy-MM-dd'T'HH:mm:ss</tt>.
	 */
	public static final FastDateFormat ISO_DATETIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss");

	/**
	 * ISO8601 formatter for date-time without time zone and separator '-'. The format used is
	 * <tt>yyyyMMdd'T'HH:mm:ss</tt>.
	 */
	public static final FastDateFormat ISO_DATETIME_NO_H_FORMAT = FastDateFormat.getInstance("yyyyMMdd'T'HH:mm:ss");

	/**
	 * ISO8601 formatter for date-time without time zone and prefix 'T' char. The format used is
	 * <tt>yyyy-MM-dd HH:mm:ss</tt>.
	 */
	public static final FastDateFormat ISO_DATETIME_NO_T_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

	/**
	 * ISO8601 formatter for date-time with time zone. The format used is
	 * <tt>yyyy-MM-dd'T'HH:mm:ssZZ</tt>.
	 */
	public static final FastDateFormat ISO_DATETIME_TIME_ZONE_FORMAT = FastDateFormat
		.getInstance("yyyy-MM-dd'T'HH:mm:ssZZ");

	/**
	 * ISO8601 formatter for date without time zone. The format used is <tt>yyyy-MM-dd</tt>.
	 */
	public static final FastDateFormat ISO_DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");

	/**
	 * ISO8601-like formatter for date with time zone. The format used is <tt>yyyy-MM-ddZZ</tt>.
	 * This pattern does not comply with the formal ISO8601 specification as the standard does not
	 * allow a time zone without a time.
	 */
	public static final FastDateFormat ISO_DATE_TIME_ZONE_FORMAT = FastDateFormat.getInstance("yyyy-MM-ddZZ");

	/**
	 * ISO8601 formatter for time without time zone. The format used is <tt>'T'HH:mm:ss</tt>.
	 */
	public static final FastDateFormat ISO_TIME_FORMAT = FastDateFormat.getInstance("'T'HH:mm:ss");

	/**
	 * ISO8601 formatter for time with time zone. The format used is <tt>'T'HH:mm:ssZZ</tt>.
	 */
	public static final FastDateFormat ISO_TIME_TIME_ZONE_FORMAT = FastDateFormat.getInstance("'T'HH:mm:ssZZ");

	/**
	 * ISO8601-like formatter for time without time zone. The format used is <tt>HH:mm:ss</tt>. This
	 * pattern does not comply with the formal ISO8601 specification as the standard requires the
	 * 'T' prefix for times.
	 */
	public static final FastDateFormat ISO_TIME_NO_T_FORMAT = FastDateFormat.getInstance("HH:mm:ss");

	/**
	 * ISO8601-like formatter for time with time zone. The format used is <tt>HH:mm:ssZZ</tt>. This
	 * pattern does not comply with the formal ISO8601 specification as the standard requires the
	 * 'T' prefix for times.
	 */
	public static final FastDateFormat ISO_TIME_NO_T_TIME_ZONE_FORMAT = FastDateFormat.getInstance("HH:mm:ssZZ");

	/**
	 * SMTP (and probably other) date headers. The format used is
	 * <tt>EEE, dd MMM yyyy HH:mm:ss Z</tt> in US locale.
	 */
	public static final FastDateFormat SMTP_DATETIME_FORMAT = FastDateFormat.getInstance("EEE, dd MMM yyyy HH:mm:ss Z",
		Locale.US);

	/**
	 * Timestamp format. The format used is <tt>yyyy-MM-dd HH:mm:ss.SSS</tt>. 
	 */
	public static final FastDateFormat TIMESTAMP_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss.SSS");

	/**
	 * Timestamp format for log. The format used is <tt>yyyyMMddTHHmmss.SSS</tt>. 
	 */
	public static final FastDateFormat TIMESTAMP_LOG_FORMAT = FastDateFormat.getInstance("yyyyMMdd'T'HHmmss.SSS");

	/**
	 * DateTime format for log. The format used is <tt>yyyyMMddTHHmmss</tt>. 
	 */
	public static final FastDateFormat DATETIME_LOG_FORMAT = FastDateFormat.getInstance("yyyyMMdd'T'HHmmss");

	/**
	 * Date format for log. The format used is <tt>yyyyMMdd</tt>. 
	 */
	public static final FastDateFormat DATE_LOG_FORMAT = FastDateFormat.getInstance("yyyyMMdd");

	/**
	 * Time format for log. The format used is <tt>HHmmss</tt>. 
	 */
	public static final FastDateFormat TIME_LOG_FORMAT = FastDateFormat.getInstance("HHmmss");

	// -----------------------------------------------------------------------
	/**
	 * @return current Calendar
	 */
	public static Calendar getCalendar() {
		return Calendar.getInstance();
	}

	/**
	 * @return current Date
	 */
	public static Date getDate() {
		return Calendar.getInstance().getTime();
	}

	/**
	 * @return current time in milliseconds
	 */
	public static long getTime() {
		return getDate().getTime();
	}

	// -----------------------------------------------------------------------
	/**
	 * <p>
	 * Checks if two date objects are on the same day ignoring time.
	 * </p>
	 * <p>
	 * 28 Mar 2002 13:45 and 28 Mar 2002 06:01 would return true. 28 Mar 2002 13:45 and 12 Mar 2002
	 * 13:45 would return false.
	 * </p>
	 * 
	 * @param date1 the first date, not altered, not null
	 * @param date2 the second date, not altered, not null
	 * @return true if they represent the same day
	 * @throws IllegalArgumentException if either date is <code>null</code>
	 */
	public static boolean isSameDay(final Date date1, final Date date2) {
		if (date1 == null || date2 == null) {
			throw new IllegalArgumentException("The date must not be null");
		}
		final Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);
		final Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		return isSameDay(cal1, cal2);
	}

	/**
	 * <p>
	 * Checks if two calendar objects are on the same day ignoring time.
	 * </p>
	 * <p>
	 * 28 Mar 2002 13:45 and 28 Mar 2002 06:01 would return true. 28 Mar 2002 13:45 and 12 Mar 2002
	 * 13:45 would return false.
	 * </p>
	 * 
	 * @param cal1 the first calendar, not altered, not null
	 * @param cal2 the second calendar, not altered, not null
	 * @return true if they represent the same day
	 * @throws IllegalArgumentException if either calendar is <code>null</code>
	 */
	public static boolean isSameDay(final Calendar cal1, final Calendar cal2) {
		if (cal1 == null || cal2 == null) {
			throw new IllegalArgumentException("The date must not be null");
		}
		return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1
			.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
	}

	// -----------------------------------------------------------------------
	/**
	 * <p>
	 * Checks if two date objects represent the same instant in time.
	 * </p>
	 * <p>
	 * This method compares the long millisecond time of the two objects.
	 * </p>
	 * 
	 * @param date1 the first date, not altered, not null
	 * @param date2 the second date, not altered, not null
	 * @return true if they represent the same millisecond instant
	 * @throws IllegalArgumentException if either date is <code>null</code>
	 */
	public static boolean isSameInstant(final Date date1, final Date date2) {
		if (date1 == null || date2 == null) {
			throw new IllegalArgumentException("The date must not be null");
		}
		return date1.getTime() == date2.getTime();
	}

	/**
	 * <p>
	 * Checks if two calendar objects represent the same instant in time.
	 * </p>
	 * <p>
	 * This method compares the long millisecond time of the two objects.
	 * </p>
	 * 
	 * @param cal1 the first calendar, not altered, not null
	 * @param cal2 the second calendar, not altered, not null
	 * @return true if they represent the same millisecond instant
	 * @throws IllegalArgumentException if either date is <code>null</code>
	 */
	public static boolean isSameInstant(final Calendar cal1, final Calendar cal2) {
		if (cal1 == null || cal2 == null) {
			throw new IllegalArgumentException("The date must not be null");
		}
		return cal1.getTime().getTime() == cal2.getTime().getTime();
	}

	// -----------------------------------------------------------------------
	/**
	 * <p>
	 * Checks if two calendar objects represent the same local time.
	 * </p>
	 * <p>
	 * This method compares the values of the fields of the two objects. In addition, both calendars
	 * must be the same of the same type.
	 * </p>
	 * 
	 * @param cal1 the first calendar, not altered, not null
	 * @param cal2 the second calendar, not altered, not null
	 * @return true if they represent the same millisecond instant
	 * @throws IllegalArgumentException if either date is <code>null</code>
	 */
	public static boolean isSameLocalTime(final Calendar cal1, final Calendar cal2) {
		if (cal1 == null || cal2 == null) {
			throw new IllegalArgumentException("The date must not be null");
		}
		return (cal1.get(Calendar.MILLISECOND) == cal2.get(Calendar.MILLISECOND)
				&& cal1.get(Calendar.SECOND) == cal2.get(Calendar.SECOND)
				&& cal1.get(Calendar.MINUTE) == cal2.get(Calendar.MINUTE)
				&& cal1.get(Calendar.HOUR_OF_DAY) == cal2.get(Calendar.HOUR_OF_DAY)
				&& cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
				&& cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
				&& cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) && cal1.getClass() == cal2.getClass());
	}

	// -----------------------------------------------------------------------
	/**
	 * Adds a number of years to a date returning a new object. The original {@code Date} is
	 * unchanged.
	 * 
	 * @param date the date, not null
	 * @param amount the amount to add, may be negative
	 * @return the new {@code Date} with the amount added
	 * @throws IllegalArgumentException if the date is null
	 */
	public static Date addYears(final Date date, final int amount) {
		return add(date, Calendar.YEAR, amount);
	}

	// -----------------------------------------------------------------------
	/**
	 * Adds a number of months to a date returning a new object. The original {@code Date} is
	 * unchanged.
	 * 
	 * @param date the date, not null
	 * @param amount the amount to add, may be negative
	 * @return the new {@code Date} with the amount added
	 * @throws IllegalArgumentException if the date is null
	 */
	public static Date addMonths(final Date date, final int amount) {
		return add(date, Calendar.MONTH, amount);
	}

	// -----------------------------------------------------------------------
	/**
	 * Adds a number of weeks to a date returning a new object. The original {@code Date} is
	 * unchanged.
	 * 
	 * @param date the date, not null
	 * @param amount the amount to add, may be negative
	 * @return the new {@code Date} with the amount added
	 * @throws IllegalArgumentException if the date is null
	 */
	public static Date addWeeks(final Date date, final int amount) {
		return add(date, Calendar.WEEK_OF_YEAR, amount);
	}

	// -----------------------------------------------------------------------
	/**
	 * Adds a number of days to a date returning a new object. The original {@code Date} is
	 * unchanged.
	 * 
	 * @param date the date, not null
	 * @param amount the amount to add, may be negative
	 * @return the new {@code Date} with the amount added
	 * @throws IllegalArgumentException if the date is null
	 */
	public static Date addDays(final Date date, final int amount) {
		return add(date, Calendar.DAY_OF_MONTH, amount);
	}

	// -----------------------------------------------------------------------
	/**
	 * Adds a number of hours to a date returning a new object. The original {@code Date} is
	 * unchanged.
	 * 
	 * @param date the date, not null
	 * @param amount the amount to add, may be negative
	 * @return the new {@code Date} with the amount added
	 * @throws IllegalArgumentException if the date is null
	 */
	public static Date addHours(final Date date, final int amount) {
		return add(date, Calendar.HOUR_OF_DAY, amount);
	}

	// -----------------------------------------------------------------------
	/**
	 * Adds a number of minutes to a date returning a new object. The original {@code Date} is
	 * unchanged.
	 * 
	 * @param date the date, not null
	 * @param amount the amount to add, may be negative
	 * @return the new {@code Date} with the amount added
	 * @throws IllegalArgumentException if the date is null
	 */
	public static Date addMinutes(final Date date, final int amount) {
		return add(date, Calendar.MINUTE, amount);
	}

	// -----------------------------------------------------------------------
	/**
	 * Adds a number of seconds to a date returning a new object. The original {@code Date} is
	 * unchanged.
	 * 
	 * @param date the date, not null
	 * @param amount the amount to add, may be negative
	 * @return the new {@code Date} with the amount added
	 * @throws IllegalArgumentException if the date is null
	 */
	public static Date addSeconds(final Date date, final int amount) {
		return add(date, Calendar.SECOND, amount);
	}

	// -----------------------------------------------------------------------
	/**
	 * Adds a number of milliseconds to a date returning a new object. The original {@code Date} is
	 * unchanged.
	 * 
	 * @param date the date, not null
	 * @param amount the amount to add, may be negative
	 * @return the new {@code Date} with the amount added
	 * @throws IllegalArgumentException if the date is null
	 */
	public static Date addMilliseconds(final Date date, final int amount) {
		return add(date, Calendar.MILLISECOND, amount);
	}

	// -----------------------------------------------------------------------
	/**
	 * Adds to a date returning a new object. The original {@code Date} is unchanged.
	 * 
	 * @param date the date, not null
	 * @param calendarField the calendar field to add to
	 * @param amount the amount to add, may be negative
	 * @return the new {@code Date} with the amount added
	 * @throws IllegalArgumentException if the date is null
	 */
	private static Date add(final Date date, final int calendarField, final int amount) {
		if (date == null) {
			throw new IllegalArgumentException("The date must not be null");
		}
		final Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(calendarField, amount);
		return c.getTime();
	}

	// -----------------------------------------------------------------------
	/**
	 * Sets the years field to a date returning a new object. The original {@code Date} is
	 * unchanged.
	 * 
	 * @param date the date, not null
	 * @param amount the amount to set
	 * @return a new {@code Date} set with the specified value
	 * @throws IllegalArgumentException if the date is null
	 */
	public static Date setYears(final Date date, final int amount) {
		return set(date, Calendar.YEAR, amount);
	}

	// -----------------------------------------------------------------------
	/**
	 * Sets the months field to a date returning a new object. The original {@code Date} is
	 * unchanged.
	 * 
	 * @param date the date, not null
	 * @param amount the amount to set
	 * @return a new {@code Date} set with the specified value
	 * @throws IllegalArgumentException if the date is null
	 */
	public static Date setMonths(final Date date, final int amount) {
		return set(date, Calendar.MONTH, amount);
	}

	// -----------------------------------------------------------------------
	/**
	 * Sets the day of month field to a date returning a new object. The original {@code Date} is
	 * unchanged.
	 * 
	 * @param date the date, not null
	 * @param amount the amount to set
	 * @return a new {@code Date} set with the specified value
	 * @throws IllegalArgumentException if the date is null
	 */
	public static Date setDays(final Date date, final int amount) {
		return set(date, Calendar.DAY_OF_MONTH, amount);
	}

	// -----------------------------------------------------------------------
	/**
	 * Sets the hours field to a date returning a new object. Hours range from 0-23. The original
	 * {@code Date} is unchanged.
	 * 
	 * @param date the date, not null
	 * @param amount the amount to set
	 * @return a new {@code Date} set with the specified value
	 * @throws IllegalArgumentException if the date is null
	 */
	public static Date setHours(final Date date, final int amount) {
		return set(date, Calendar.HOUR_OF_DAY, amount);
	}

	// -----------------------------------------------------------------------
	/**
	 * Sets the minute field to a date returning a new object. The original {@code Date} is
	 * unchanged.
	 * 
	 * @param date the date, not null
	 * @param amount the amount to set
	 * @return a new {@code Date} set with the specified value
	 * @throws IllegalArgumentException if the date is null
	 */
	public static Date setMinutes(final Date date, final int amount) {
		return set(date, Calendar.MINUTE, amount);
	}

	// -----------------------------------------------------------------------
	/**
	 * Sets the seconds field to a date returning a new object. The original {@code Date} is
	 * unchanged.
	 * 
	 * @param date the date, not null
	 * @param amount the amount to set
	 * @return a new {@code Date} set with the specified value
	 * @throws IllegalArgumentException if the date is null
	 */
	public static Date setSeconds(final Date date, final int amount) {
		return set(date, Calendar.SECOND, amount);
	}

	// -----------------------------------------------------------------------
	/**
	 * Sets the miliseconds field to a date returning a new object. The original {@code Date} is
	 * unchanged.
	 * 
	 * @param date the date, not null
	 * @param amount the amount to set
	 * @return a new {@code Date} set with the specified value
	 * @throws IllegalArgumentException if the date is null
	 */
	public static Date setMilliseconds(final Date date, final int amount) {
		return set(date, Calendar.MILLISECOND, amount);
	}

	// -----------------------------------------------------------------------
	/**
	 * Sets the specified field to a date returning a new object. This does not use a lenient
	 * calendar. The original {@code Date} is unchanged.
	 * 
	 * @param date the date, not null
	 * @param calendarField the {@code Calendar} field to set the amount to
	 * @param amount the amount to set
	 * @return a new {@code Date} set with the specified value
	 * @throws IllegalArgumentException if the date is null
	 */
	private static Date set(final Date date, final int calendarField, final int amount) {
		if (date == null) {
			throw new IllegalArgumentException("The date must not be null");
		}
		// getInstance() returns a new object, so this method is thread safe.
		final Calendar c = Calendar.getInstance();
		c.setLenient(false);
		c.setTime(date);
		c.set(calendarField, amount);
		return c.getTime();
	}

	// -----------------------------------------------------------------------
	/**
	 * Convert a {@code Date} into a {@code Calendar}.
	 * 
	 * @param date the date to convert to a Calendar
	 * @return the created Calendar
	 * @throws NullPointerException if null is passed in
	 */
	public static Calendar toCalendar(final Date date) {
		final Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c;
	}

	// -----------------------------------------------------------------------
	/**
	 * <p>
	 * Round this date, leaving the field specified as the most significant field.
	 * </p>
	 * <p>
	 * For example, if you had the datetime of 28 Mar 2002 13:45:01.231, if this was passed with
	 * HOUR, it would return 28 Mar 2002 14:00:00.000. If this was passed with MONTH, it would
	 * return 1 April 2002 0:00:00.000.
	 * </p>
	 * <p>
	 * For a date in a timezone that handles the change to daylight saving time, rounding to
	 * Calendar.HOUR_OF_DAY will behave as follows. Suppose daylight saving time begins at 02:00 on
	 * March 30. Rounding a date that crosses this time would produce the following values:
	 * <ul>
	 * <li>March 30, 2003 01:10 rounds to March 30, 2003 01:00</li>
	 * <li>March 30, 2003 01:40 rounds to March 30, 2003 03:00</li>
	 * <li>March 30, 2003 02:10 rounds to March 30, 2003 03:00</li>
	 * <li>March 30, 2003 02:40 rounds to March 30, 2003 04:00</li>
	 * </ul>
	 * </p>
	 * 
	 * @param date the date to work with, not null
	 * @param field the field from {@code Calendar} or {@code SEMI_MONTH}
	 * @return the different rounded date, not null
	 * @throws ArithmeticException if the year is over 280 million
	 */
	public static Date round(final Date date, final int field) {
		if (date == null) {
			throw new IllegalArgumentException("The date must not be null");
		}
		final Calendar gval = Calendar.getInstance();
		gval.setTime(date);
		modify(gval, field, MODIFY_ROUND);
		return gval.getTime();
	}

	/**
	 * <p>
	 * Round this date, leaving the field specified as the most significant field.
	 * </p>
	 * <p>
	 * For example, if you had the datetime of 28 Mar 2002 13:45:01.231, if this was passed with
	 * HOUR, it would return 28 Mar 2002 14:00:00.000. If this was passed with MONTH, it would
	 * return 1 April 2002 0:00:00.000.
	 * </p>
	 * <p>
	 * For a date in a timezone that handles the change to daylight saving time, rounding to
	 * Calendar.HOUR_OF_DAY will behave as follows. Suppose daylight saving time begins at 02:00 on
	 * March 30. Rounding a date that crosses this time would produce the following values:
	 * <ul>
	 * <li>March 30, 2003 01:10 rounds to March 30, 2003 01:00</li>
	 * <li>March 30, 2003 01:40 rounds to March 30, 2003 03:00</li>
	 * <li>March 30, 2003 02:10 rounds to March 30, 2003 03:00</li>
	 * <li>March 30, 2003 02:40 rounds to March 30, 2003 04:00</li>
	 * </ul>
	 * </p>
	 * 
	 * @param date the date to work with, not null
	 * @param field the field from {@code Calendar} or <code>SEMI_MONTH</code>
	 * @return the different rounded date, not null
	 * @throws IllegalArgumentException if the date is <code>null</code>
	 * @throws ArithmeticException if the year is over 280 million
	 */
	public static Calendar round(final Calendar date, final int field) {
		if (date == null) {
			throw new IllegalArgumentException("The date must not be null");
		}
		final Calendar rounded = (Calendar)date.clone();
		modify(rounded, field, MODIFY_ROUND);
		return rounded;
	}

	/**
	 * <p>
	 * Round this date, leaving the field specified as the most significant field.
	 * </p>
	 * <p>
	 * For example, if you had the datetime of 28 Mar 2002 13:45:01.231, if this was passed with
	 * HOUR, it would return 28 Mar 2002 14:00:00.000. If this was passed with MONTH, it would
	 * return 1 April 2002 0:00:00.000.
	 * </p>
	 * <p>
	 * For a date in a timezone that handles the change to daylight saving time, rounding to
	 * Calendar.HOUR_OF_DAY will behave as follows. Suppose daylight saving time begins at 02:00 on
	 * March 30. Rounding a date that crosses this time would produce the following values:
	 * <ul>
	 * <li>March 30, 2003 01:10 rounds to March 30, 2003 01:00</li>
	 * <li>March 30, 2003 01:40 rounds to March 30, 2003 03:00</li>
	 * <li>March 30, 2003 02:10 rounds to March 30, 2003 03:00</li>
	 * <li>March 30, 2003 02:40 rounds to March 30, 2003 04:00</li>
	 * </ul>
	 * </p>
	 * 
	 * @param date the date to work with, either {@code Date} or {@code Calendar}, not null
	 * @param field the field from {@code Calendar} or <code>SEMI_MONTH</code>
	 * @return the different rounded date, not null
	 * @throws IllegalArgumentException if the date is <code>null</code>
	 * @throws ClassCastException if the object type is not a {@code Date} or {@code Calendar}
	 * @throws ArithmeticException if the year is over 280 million
	 */
	public static Date round(final Object date, final int field) {
		if (date == null) {
			throw new IllegalArgumentException("The date must not be null");
		}
		if (date instanceof Date) {
			return round((Date)date, field);
		}
		else if (date instanceof Calendar) {
			return round((Calendar)date, field).getTime();
		}
		else {
			throw new ClassCastException("Could not round " + date);
		}
	}

	// -----------------------------------------------------------------------
	/**
	 * <p>
	 * Truncate this date, leaving the field specified as the most significant field.
	 * </p>
	 * <p>
	 * For example, if you had the datetime of 28 Mar 2002 13:45:01.231, if you passed with HOUR, it
	 * would return 28 Mar 2002 13:00:00.000. If this was passed with MONTH, it would return 1 Mar
	 * 2002 0:00:00.000.
	 * </p>
	 * 
	 * @param date the date to work with, not null
	 * @param field the field from {@code Calendar} or <code>SEMI_MONTH</code>
	 * @return the different truncated date, not null
	 * @throws IllegalArgumentException if the date is <code>null</code>
	 * @throws ArithmeticException if the year is over 280 million
	 */
	public static Date truncate(final Date date, final int field) {
		if (date == null) {
			throw new IllegalArgumentException("The date must not be null");
		}
		final Calendar gval = Calendar.getInstance();
		gval.setTime(date);
		modify(gval, field, MODIFY_TRUNCATE);
		return gval.getTime();
	}

	/**
	 * <p>
	 * Truncate this date, leaving the field specified as the most significant field.
	 * </p>
	 * <p>
	 * For example, if you had the datetime of 28 Mar 2002 13:45:01.231, if you passed with HOUR, it
	 * would return 28 Mar 2002 13:00:00.000. If this was passed with MONTH, it would return 1 Mar
	 * 2002 0:00:00.000.
	 * </p>
	 * 
	 * @param date the date to work with, not null
	 * @param field the field from {@code Calendar} or <code>SEMI_MONTH</code>
	 * @return the different truncated date, not null
	 * @throws IllegalArgumentException if the date is <code>null</code>
	 * @throws ArithmeticException if the year is over 280 million
	 */
	public static Calendar truncate(final Calendar date, final int field) {
		if (date == null) {
			throw new IllegalArgumentException("The date must not be null");
		}
		final Calendar truncated = (Calendar)date.clone();
		modify(truncated, field, MODIFY_TRUNCATE);
		return truncated;
	}

	/**
	 * <p>
	 * Truncate this date, leaving the field specified as the most significant field.
	 * </p>
	 * <p>
	 * For example, if you had the datetime of 28 Mar 2002 13:45:01.231, if you passed with HOUR, it
	 * would return 28 Mar 2002 13:00:00.000. If this was passed with MONTH, it would return 1 Mar
	 * 2002 0:00:00.000.
	 * </p>
	 * 
	 * @param date the date to work with, either {@code Date} or {@code Calendar}, not null
	 * @param field the field from {@code Calendar} or <code>SEMI_MONTH</code>
	 * @return the different truncated date, not null
	 * @throws IllegalArgumentException if the date is <code>null</code>
	 * @throws ClassCastException if the object type is not a {@code Date} or {@code Calendar}
	 * @throws ArithmeticException if the year is over 280 million
	 */
	public static Date truncate(final Object date, final int field) {
		if (date == null) {
			throw new IllegalArgumentException("The date must not be null");
		}
		if (date instanceof Date) {
			return truncate((Date)date, field);
		}
		else if (date instanceof Calendar) {
			return truncate((Calendar)date, field).getTime();
		}
		else {
			throw new ClassCastException("Could not truncate " + date);
		}
	}

	// -----------------------------------------------------------------------
	/**
	 * <p>
	 * Ceil this date, leaving the field specified as the most significant field.
	 * </p>
	 * <p>
	 * For example, if you had the datetime of 28 Mar 2002 13:45:01.231, if you passed with HOUR, it
	 * would return 28 Mar 2002 14:00:00.000. If this was passed with MONTH, it would return 1 Apr
	 * 2002 0:00:00.000.
	 * </p>
	 * 
	 * @param date the date to work with, not null
	 * @param field the field from {@code Calendar} or <code>SEMI_MONTH</code>
	 * @return the different ceil date, not null
	 * @throws IllegalArgumentException if the date is <code>null</code>
	 * @throws ArithmeticException if the year is over 280 million
	 */
	public static Date ceiling(final Date date, final int field) {
		if (date == null) {
			throw new IllegalArgumentException("The date must not be null");
		}
		final Calendar gval = Calendar.getInstance();
		gval.setTime(date);
		modify(gval, field, MODIFY_CEILING);
		return gval.getTime();
	}

	/**
	 * <p>
	 * Ceil this date, leaving the field specified as the most significant field.
	 * </p>
	 * <p>
	 * For example, if you had the datetime of 28 Mar 2002 13:45:01.231, if you passed with HOUR, it
	 * would return 28 Mar 2002 14:00:00.000. If this was passed with MONTH, it would return 1 Mar
	 * 2002 0:00:00.000.
	 * </p>
	 * 
	 * @param date the date to work with, not null
	 * @param field the field from {@code Calendar} or <code>SEMI_MONTH</code>
	 * @return the different ceil date, not null
	 * @throws IllegalArgumentException if the date is <code>null</code>
	 * @throws ArithmeticException if the year is over 280 million
	 */
	public static Calendar ceiling(final Calendar date, final int field) {
		if (date == null) {
			throw new IllegalArgumentException("The date must not be null");
		}
		final Calendar ceiled = (Calendar)date.clone();
		modify(ceiled, field, MODIFY_CEILING);
		return ceiled;
	}

	/**
	 * <p>
	 * Ceil this date, leaving the field specified as the most significant field.
	 * </p>
	 * <p>
	 * For example, if you had the datetime of 28 Mar 2002 13:45:01.231, if you passed with HOUR, it
	 * would return 28 Mar 2002 14:00:00.000. If this was passed with MONTH, it would return 1 Mar
	 * 2002 0:00:00.000.
	 * </p>
	 * 
	 * @param date the date to work with, either {@code Date} or {@code Calendar}, not null
	 * @param field the field from {@code Calendar} or <code>SEMI_MONTH</code>
	 * @return the different ceil date, not null
	 * @throws IllegalArgumentException if the date is <code>null</code>
	 * @throws ClassCastException if the object type is not a {@code Date} or {@code Calendar}
	 * @throws ArithmeticException if the year is over 280 million
	 */
	public static Date ceiling(final Object date, final int field) {
		if (date == null) {
			throw new IllegalArgumentException("The date must not be null");
		}
		
		if (date instanceof Date) {
			return ceiling((Date)date, field);
		}
		
		if (date instanceof Calendar) {
			return ceiling((Calendar)date, field).getTime();
		}

		throw new ClassCastException("Could not find ceiling of for type: " + date.getClass());
	}

	// -----------------------------------------------------------------------
	public static Date zeroCeiling(final Object date) {
		if (date == null) {
			throw new IllegalArgumentException("The date must not be null");
		}

		if (date instanceof Date) {
			int field = zeroField((Date)date);
			return ceiling((Date)date, field);
		}
			
		if (date instanceof Calendar) {
			int field = zeroField((Calendar)date);
			return ceiling((Calendar)date, field).getTime();
		}

		throw new ClassCastException("Could not find ceiling of for type: " + date.getClass());
	}

	public static int zeroField(final Calendar c) {
		if (c.get(Calendar.MILLISECOND) != 0) {
			return Calendar.MILLISECOND;
		}
		if (c.get(Calendar.SECOND) != 0) {
			return Calendar.SECOND;
		}
		if (c.get(Calendar.MINUTE) != 0) {
			return Calendar.MINUTE;
		}
		if (c.get(Calendar.HOUR_OF_DAY) != 0) {
			return Calendar.HOUR_OF_DAY;
		}
		if (c.get(Calendar.DATE) != 0) {
			return Calendar.DATE;
		}
		if (c.get(Calendar.MONTH) != 0) {
			return Calendar.MONTH;
		}
		return Calendar.YEAR;
	}
	
	public static int zeroField(final Date date) {
		return zeroField(toCalendar(date));
	}
	
	// -----------------------------------------------------------------------
	/**
	 * <p>
	 * Internal calculation method.
	 * </p>
	 * 
	 * @param val the calendar, not null
	 * @param field the field constant
	 * @param modType type to truncate, round or ceiling
	 * @throws ArithmeticException if the year is over 280 million
	 */
	private static void modify(final Calendar val, final int field, final int modType) {
		if (val.get(Calendar.YEAR) > 280000000) {
			throw new ArithmeticException("Calendar value too large for accurate calculations");
		}

		if (field == Calendar.MILLISECOND) {
			if (modType == MODIFY_CEILING) {
				val.add(Calendar.MILLISECOND, 1);
			}
			return;
		}

		// ----------------- Fix for LANG-59 ---------------------- START ---------------
		// see http://issues.apache.org/jira/browse/LANG-59
		//
		// Manually truncate milliseconds, seconds and minutes, rather than using
		// Calendar methods.

		final Date date = val.getTime();
		long time = date.getTime();
		boolean done = false;

		// truncate milliseconds
		final int millisecs = val.get(Calendar.MILLISECOND);
		if (MODIFY_TRUNCATE == modType || millisecs < 500) {
			time = time - millisecs;
		}
		if (field == Calendar.SECOND) {
			done = true;
		}

		// truncate seconds
		final int seconds = val.get(Calendar.SECOND);
		if (!done && (MODIFY_TRUNCATE == modType || seconds < 30)) {
			time = time - (seconds * 1000L);
		}
		if (field == Calendar.MINUTE) {
			done = true;
		}

		// truncate minutes
		final int minutes = val.get(Calendar.MINUTE);
		if (!done && (MODIFY_TRUNCATE == modType || minutes < 30)) {
			time = time - (minutes * 60000L);
		}

		// reset time
		if (date.getTime() != time) {
			date.setTime(time);
			val.setTime(date);
		}
		// ----------------- Fix for LANG-59 ----------------------- END ----------------

		boolean roundUp = false;
		for (final int[] aField : fields) {
			for (final int element : aField) {
				if (element == field) {
					// This is our field... we stop looping
					if (modType == MODIFY_CEILING || (modType == MODIFY_ROUND && roundUp)) {
						if (field == DateTimes.SEMI_MONTH) {
							// This is a special case that's hard to generalize
							// If the date is 1, we round up to 16, otherwise
							// we subtract 15 days and add 1 month
							if (val.get(Calendar.DATE) == 1) {
								val.add(Calendar.DATE, 15);
							}
							else {
								val.add(Calendar.DATE, -15);
								val.add(Calendar.MONTH, 1);
							}
							// ----------------- Fix for LANG-440 ---------------------- START
							// ---------------
						}
						else if (field == Calendar.AM_PM) {
							// This is a special case
							// If the time is 0, we round up to 12, otherwise
							// we subtract 12 hours and add 1 day
							if (val.get(Calendar.HOUR_OF_DAY) == 0) {
								val.add(Calendar.HOUR_OF_DAY, 12);
							}
							else {
								val.add(Calendar.HOUR_OF_DAY, -12);
								val.add(Calendar.DATE, 1);
							}
							// ----------------- Fix for LANG-440 ---------------------- END
							// ---------------
						}
						else {
							// We need at add one to this field since the
							// last number causes us to round up
							val.add(aField[0], 1);
						}
					}
					return;
				}
			}
			// We have various fields that are not easy roundings
			int offset = 0;
			boolean offsetSet = false;
			// These are special types of fields that require different rounding rules
			switch (field) {
			case DateTimes.SEMI_MONTH:
				if (aField[0] == Calendar.DATE) {
					// If we're going to drop the DATE field's value,
					// we want to do this our own way.
					// We need to subtrace 1 since the date has a minimum of 1
					offset = val.get(Calendar.DATE) - 1;
					// If we're above 15 days adjustment, that means we're in the
					// bottom half of the month and should stay accordingly.
					if (offset >= 15) {
						offset -= 15;
					}
					// Record whether we're in the top or bottom half of that range
					roundUp = offset > 7;
					offsetSet = true;
				}
				break;
			case Calendar.AM_PM:
				if (aField[0] == Calendar.HOUR_OF_DAY) {
					// If we're going to drop the HOUR field's value,
					// we want to do this our own way.
					offset = val.get(Calendar.HOUR_OF_DAY);
					if (offset >= 12) {
						offset -= 12;
					}
					roundUp = offset >= 6;
					offsetSet = true;
				}
				break;
			}
			if (!offsetSet) {
				final int min = val.getActualMinimum(aField[0]);
				final int max = val.getActualMaximum(aField[0]);
				// Calculate the offset from the minimum allowed value
				offset = val.get(aField[0]) - min;
				// Set roundUp if this is more than half way between the minimum and maximum
				roundUp = offset > ((max - min) / 2);
			}
			// We need to remove this field
			if (offset != 0) {
				val.set(aField[0], val.get(aField[0]) - offset);
			}
		}
		throw new IllegalArgumentException("The field " + field + " is not supported");

	}

	// -----------------------------------------------------------------------
	/**
	 * <p>
	 * This constructs an <code>Iterator</code> over each day in a date range defined by a focus
	 * date and range style.
	 * </p>
	 * <p>
	 * For instance, passing Thursday, July 4, 2002 and a <code>RANGE_MONTH_SUNDAY</code> will
	 * return an <code>Iterator</code> that starts with Sunday, June 30, 2002 and ends with
	 * Saturday, August 3, 2002, returning a Calendar instance for each intermediate day.
	 * </p>
	 * <p>
	 * This method provides an iterator that returns Calendar objects. The days are progressed using
	 * {@link Calendar#add(int, int)}.
	 * </p>
	 * 
	 * @param focus the date to work with, not null
	 * @param rangeStyle the style constant to use. Must be one of
	 *            {@link DateTimes#RANGE_MONTH_SUNDAY}, {@link DateTimes#RANGE_MONTH_MONDAY},
	 *            {@link DateTimes#RANGE_WEEK_SUNDAY}, {@link DateTimes#RANGE_WEEK_MONDAY},
	 *            {@link DateTimes#RANGE_WEEK_RELATIVE}, {@link DateTimes#RANGE_WEEK_CENTER}
	 * @return the date iterator, not null, not null
	 * @throws IllegalArgumentException if the date is <code>null</code>
	 * @throws IllegalArgumentException if the rangeStyle is invalid
	 */
	public static Iterator<Calendar> iterator(final Date focus, final int rangeStyle) {
		if (focus == null) {
			throw new IllegalArgumentException("The date must not be null");
		}
		final Calendar gval = Calendar.getInstance();
		gval.setTime(focus);
		return iterator(gval, rangeStyle);
	}

	/**
	 * <p>
	 * This constructs an <code>Iterator</code> over each day in a date range defined by a focus
	 * date and range style.
	 * </p>
	 * <p>
	 * For instance, passing Thursday, July 4, 2002 and a <code>RANGE_MONTH_SUNDAY</code> will
	 * return an <code>Iterator</code> that starts with Sunday, June 30, 2002 and ends with
	 * Saturday, August 3, 2002, returning a Calendar instance for each intermediate day.
	 * </p>
	 * <p>
	 * This method provides an iterator that returns Calendar objects. The days are progressed using
	 * {@link Calendar#add(int, int)}.
	 * </p>
	 * 
	 * @param focus the date to work with, not null
	 * @param rangeStyle the style constant to use. Must be one of
	 *            {@link DateTimes#RANGE_MONTH_SUNDAY}, {@link DateTimes#RANGE_MONTH_MONDAY},
	 *            {@link DateTimes#RANGE_WEEK_SUNDAY}, {@link DateTimes#RANGE_WEEK_MONDAY},
	 *            {@link DateTimes#RANGE_WEEK_RELATIVE}, {@link DateTimes#RANGE_WEEK_CENTER}
	 * @return the date iterator, not null
	 * @throws IllegalArgumentException if the date is <code>null</code>
	 * @throws IllegalArgumentException if the rangeStyle is invalid
	 */
	public static Iterator<Calendar> iterator(final Calendar focus, final int rangeStyle) {
		if (focus == null) {
			throw new IllegalArgumentException("The date must not be null");
		}
		Calendar start = null;
		Calendar end = null;
		int startCutoff = Calendar.SUNDAY;
		int endCutoff = Calendar.SATURDAY;
		switch (rangeStyle) {
		case RANGE_MONTH_SUNDAY:
		case RANGE_MONTH_MONDAY:
			// Set start to the first of the month
			start = truncate(focus, Calendar.MONTH);
			// Set end to the last of the month
			end = (Calendar)start.clone();
			end.add(Calendar.MONTH, 1);
			end.add(Calendar.DATE, -1);
			// Loop start back to the previous sunday or monday
			if (rangeStyle == RANGE_MONTH_MONDAY) {
				startCutoff = Calendar.MONDAY;
				endCutoff = Calendar.SUNDAY;
			}
			break;
		case RANGE_WEEK_SUNDAY:
		case RANGE_WEEK_MONDAY:
		case RANGE_WEEK_RELATIVE:
		case RANGE_WEEK_CENTER:
			// Set start and end to the current date
			start = truncate(focus, Calendar.DATE);
			end = truncate(focus, Calendar.DATE);
			switch (rangeStyle) {
			case RANGE_WEEK_SUNDAY:
				// already set by default
				break;
			case RANGE_WEEK_MONDAY:
				startCutoff = Calendar.MONDAY;
				endCutoff = Calendar.SUNDAY;
				break;
			case RANGE_WEEK_RELATIVE:
				startCutoff = focus.get(Calendar.DAY_OF_WEEK);
				endCutoff = startCutoff - 1;
				break;
			case RANGE_WEEK_CENTER:
				startCutoff = focus.get(Calendar.DAY_OF_WEEK) - 3;
				endCutoff = focus.get(Calendar.DAY_OF_WEEK) + 3;
				break;
			}
			break;
		default:
			throw new IllegalArgumentException("The range style " + rangeStyle + " is not valid.");
		}
		if (startCutoff < Calendar.SUNDAY) {
			startCutoff += 7;
		}
		if (startCutoff > Calendar.SATURDAY) {
			startCutoff -= 7;
		}
		if (endCutoff < Calendar.SUNDAY) {
			endCutoff += 7;
		}
		if (endCutoff > Calendar.SATURDAY) {
			endCutoff -= 7;
		}
		while (start.get(Calendar.DAY_OF_WEEK) != startCutoff) {
			start.add(Calendar.DATE, -1);
		}
		while (end.get(Calendar.DAY_OF_WEEK) != endCutoff) {
			end.add(Calendar.DATE, 1);
		}
		return new DateIterator(start, end);
	}

	/**
	 * <p>
	 * This constructs an <code>Iterator</code> over each day in a date range defined by a focus
	 * date and range style.
	 * </p>
	 * <p>
	 * For instance, passing Thursday, July 4, 2002 and a <code>RANGE_MONTH_SUNDAY</code> will
	 * return an <code>Iterator</code> that starts with Sunday, June 30, 2002 and ends with
	 * Saturday, August 3, 2002, returning a Calendar instance for each intermediate day.
	 * </p>
	 * 
	 * @param focus the date to work with, either {@code Date} or {@code Calendar}, not null
	 * @param rangeStyle the style constant to use. Must be one of the range styles listed for the
	 *            {@link #iterator(Calendar, int)} method.
	 * @return the date iterator, not null
	 * @throws IllegalArgumentException if the date is <code>null</code>
	 * @throws ClassCastException if the object type is not a {@code Date} or {@code Calendar}
	 */
	public static Iterator<?> iterator(final Object focus, final int rangeStyle) {
		if (focus == null) {
			throw new IllegalArgumentException("The date must not be null");
		}
		if (focus instanceof Date) {
			return iterator((Date)focus, rangeStyle);
		}
		else if (focus instanceof Calendar) {
			return iterator((Calendar)focus, rangeStyle);
		}
		else {
			throw new ClassCastException("Could not iterate based on " + focus);
		}
	}

	/**
	 * <p>
	 * Returns the number of milliseconds within the fragment. All datefields greater than the
	 * fragment will be ignored.
	 * </p>
	 * <p>
	 * Asking the milliseconds of any date will only return the number of milliseconds of the
	 * current second (resulting in a number between 0 and 999). This method will retrieve the
	 * number of milliseconds for any fragment. For example, if you want to calculate the number of
	 * milliseconds past today, your fragment is Calendar.DATE or Calendar.DAY_OF_YEAR. The result
	 * will be all milliseconds of the past hour(s), minutes(s) and second(s).
	 * </p>
	 * <p>
	 * Valid fragments are: Calendar.YEAR, Calendar.MONTH, both Calendar.DAY_OF_YEAR and
	 * Calendar.DATE, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND and
	 * Calendar.MILLISECOND A fragment less than or equal to a SECOND field will return 0.
	 * </p>
	 * <p>
	 * <ul>
	 * <li>January 1, 2008 7:15:10.538 with Calendar.SECOND as fragment will return 538</li>
	 * <li>January 6, 2008 7:15:10.538 with Calendar.SECOND as fragment will return 538</li>
	 * <li>January 6, 2008 7:15:10.538 with Calendar.MINUTE as fragment will return 10538 (10*1000 +
	 * 538)</li>
	 * <li>January 16, 2008 7:15:10.538 with Calendar.MILLISECOND as fragment will return 0 (a
	 * millisecond cannot be split in milliseconds)</li>
	 * </ul>
	 * </p>
	 * 
	 * @param date the date to work with, not null
	 * @param fragment the {@code Calendar} field part of date to calculate
	 * @return number of milliseconds within the fragment of date
	 * @throws IllegalArgumentException if the date is <code>null</code> or fragment is not
	 *             supported
	 */
	public static long getFragmentInMilliseconds(final Date date, final int fragment) {
		return getFragment(date, fragment, Calendar.MILLISECOND);
	}

	/**
	 * <p>
	 * Returns the number of seconds within the fragment. All datefields greater than the fragment
	 * will be ignored.
	 * </p>
	 * <p>
	 * Asking the seconds of any date will only return the number of seconds of the current minute
	 * (resulting in a number between 0 and 59). This method will retrieve the number of seconds for
	 * any fragment. For example, if you want to calculate the number of seconds past today, your
	 * fragment is Calendar.DATE or Calendar.DAY_OF_YEAR. The result will be all seconds of the past
	 * hour(s) and minutes(s).
	 * </p>
	 * <p>
	 * Valid fragments are: Calendar.YEAR, Calendar.MONTH, both Calendar.DAY_OF_YEAR and
	 * Calendar.DATE, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND and
	 * Calendar.MILLISECOND A fragment less than or equal to a SECOND field will return 0.
	 * </p>
	 * <p>
	 * <ul>
	 * <li>January 1, 2008 7:15:10.538 with Calendar.MINUTE as fragment will return 10 (equivalent
	 * to deprecated date.getSeconds())</li>
	 * <li>January 6, 2008 7:15:10.538 with Calendar.MINUTE as fragment will return 10 (equivalent
	 * to deprecated date.getSeconds())</li>
	 * <li>January 6, 2008 7:15:10.538 with Calendar.DAY_OF_YEAR as fragment will return 26110
	 * (7*3600 + 15*60 + 10)</li>
	 * <li>January 16, 2008 7:15:10.538 with Calendar.MILLISECOND as fragment will return 0 (a
	 * millisecond cannot be split in seconds)</li>
	 * </ul>
	 * </p>
	 * 
	 * @param date the date to work with, not null
	 * @param fragment the {@code Calendar} field part of date to calculate
	 * @return number of seconds within the fragment of date
	 * @throws IllegalArgumentException if the date is <code>null</code> or fragment is not
	 *             supported
	 */
	public static long getFragmentInSeconds(final Date date, final int fragment) {
		return getFragment(date, fragment, Calendar.SECOND);
	}

	/**
	 * <p>
	 * Returns the number of minutes within the fragment. All datefields greater than the fragment
	 * will be ignored.
	 * </p>
	 * <p>
	 * Asking the minutes of any date will only return the number of minutes of the current hour
	 * (resulting in a number between 0 and 59). This method will retrieve the number of minutes for
	 * any fragment. For example, if you want to calculate the number of minutes past this month,
	 * your fragment is Calendar.MONTH. The result will be all minutes of the past day(s) and
	 * hour(s).
	 * </p>
	 * <p>
	 * Valid fragments are: Calendar.YEAR, Calendar.MONTH, both Calendar.DAY_OF_YEAR and
	 * Calendar.DATE, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND and
	 * Calendar.MILLISECOND A fragment less than or equal to a MINUTE field will return 0.
	 * </p>
	 * <p>
	 * <ul>
	 * <li>January 1, 2008 7:15:10.538 with Calendar.HOUR_OF_DAY as fragment will return 15
	 * (equivalent to deprecated date.getMinutes())</li>
	 * <li>January 6, 2008 7:15:10.538 with Calendar.HOUR_OF_DAY as fragment will return 15
	 * (equivalent to deprecated date.getMinutes())</li>
	 * <li>January 1, 2008 7:15:10.538 with Calendar.MONTH as fragment will return 15</li>
	 * <li>January 6, 2008 7:15:10.538 with Calendar.MONTH as fragment will return 435 (7*60 + 15)</li>
	 * <li>January 16, 2008 7:15:10.538 with Calendar.MILLISECOND as fragment will return 0 (a
	 * millisecond cannot be split in minutes)</li>
	 * </ul>
	 * </p>
	 * 
	 * @param date the date to work with, not null
	 * @param fragment the {@code Calendar} field part of date to calculate
	 * @return number of minutes within the fragment of date
	 * @throws IllegalArgumentException if the date is <code>null</code> or fragment is not
	 *             supported
	 */
	public static long getFragmentInMinutes(final Date date, final int fragment) {
		return getFragment(date, fragment, Calendar.MINUTE);
	}

	/**
	 * <p>
	 * Returns the number of hours within the fragment. All datefields greater than the fragment
	 * will be ignored.
	 * </p>
	 * <p>
	 * Asking the hours of any date will only return the number of hours of the current day
	 * (resulting in a number between 0 and 23). This method will retrieve the number of hours for
	 * any fragment. For example, if you want to calculate the number of hours past this month, your
	 * fragment is Calendar.MONTH. The result will be all hours of the past day(s).
	 * </p>
	 * <p>
	 * Valid fragments are: Calendar.YEAR, Calendar.MONTH, both Calendar.DAY_OF_YEAR and
	 * Calendar.DATE, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND and
	 * Calendar.MILLISECOND A fragment less than or equal to a HOUR field will return 0.
	 * </p>
	 * <p>
	 * <ul>
	 * <li>January 1, 2008 7:15:10.538 with Calendar.DAY_OF_YEAR as fragment will return 7
	 * (equivalent to deprecated date.getHours())</li>
	 * <li>January 6, 2008 7:15:10.538 with Calendar.DAY_OF_YEAR as fragment will return 7
	 * (equivalent to deprecated date.getHours())</li>
	 * <li>January 1, 2008 7:15:10.538 with Calendar.MONTH as fragment will return 7</li>
	 * <li>January 6, 2008 7:15:10.538 with Calendar.MONTH as fragment will return 127 (5*24 + 7)</li>
	 * <li>January 16, 2008 7:15:10.538 with Calendar.MILLISECOND as fragment will return 0 (a
	 * millisecond cannot be split in hours)</li>
	 * </ul>
	 * </p>
	 * 
	 * @param date the date to work with, not null
	 * @param fragment the {@code Calendar} field part of date to calculate
	 * @return number of hours within the fragment of date
	 * @throws IllegalArgumentException if the date is <code>null</code> or fragment is not
	 *             supported
	 */
	public static long getFragmentInHours(final Date date, final int fragment) {
		return getFragment(date, fragment, Calendar.HOUR_OF_DAY);
	}

	/**
	 * <p>
	 * Returns the number of days within the fragment. All datefields greater than the fragment will
	 * be ignored.
	 * </p>
	 * <p>
	 * Asking the days of any date will only return the number of days of the current month
	 * (resulting in a number between 1 and 31). This method will retrieve the number of days for
	 * any fragment. For example, if you want to calculate the number of days past this year, your
	 * fragment is Calendar.YEAR. The result will be all days of the past month(s).
	 * </p>
	 * <p>
	 * Valid fragments are: Calendar.YEAR, Calendar.MONTH, both Calendar.DAY_OF_YEAR and
	 * Calendar.DATE, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND and
	 * Calendar.MILLISECOND A fragment less than or equal to a DAY field will return 0.
	 * </p>
	 * <p>
	 * <ul>
	 * <li>January 28, 2008 with Calendar.MONTH as fragment will return 28 (equivalent to deprecated
	 * date.getDay())</li>
	 * <li>February 28, 2008 with Calendar.MONTH as fragment will return 28 (equivalent to
	 * deprecated date.getDay())</li>
	 * <li>January 28, 2008 with Calendar.YEAR as fragment will return 28</li>
	 * <li>February 28, 2008 with Calendar.YEAR as fragment will return 59</li>
	 * <li>January 28, 2008 with Calendar.MILLISECOND as fragment will return 0 (a millisecond
	 * cannot be split in days)</li>
	 * </ul>
	 * </p>
	 * 
	 * @param date the date to work with, not null
	 * @param fragment the {@code Calendar} field part of date to calculate
	 * @return number of days within the fragment of date
	 * @throws IllegalArgumentException if the date is <code>null</code> or fragment is not
	 *             supported
	 */
	public static long getFragmentInDays(final Date date, final int fragment) {
		return getFragment(date, fragment, Calendar.DAY_OF_YEAR);
	}

	/**
	 * <p>
	 * Returns the number of milliseconds within the fragment. All datefields greater than the
	 * fragment will be ignored.
	 * </p>
	 * <p>
	 * Asking the milliseconds of any date will only return the number of milliseconds of the
	 * current second (resulting in a number between 0 and 999). This method will retrieve the
	 * number of milliseconds for any fragment. For example, if you want to calculate the number of
	 * seconds past today, your fragment is Calendar.DATE or Calendar.DAY_OF_YEAR. The result will
	 * be all seconds of the past hour(s), minutes(s) and second(s).
	 * </p>
	 * <p>
	 * Valid fragments are: Calendar.YEAR, Calendar.MONTH, both Calendar.DAY_OF_YEAR and
	 * Calendar.DATE, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND and
	 * Calendar.MILLISECOND A fragment less than or equal to a MILLISECOND field will return 0.
	 * </p>
	 * <p>
	 * <ul>
	 * <li>January 1, 2008 7:15:10.538 with Calendar.SECOND as fragment will return 538 (equivalent
	 * to calendar.get(Calendar.MILLISECOND))</li>
	 * <li>January 6, 2008 7:15:10.538 with Calendar.SECOND as fragment will return 538 (equivalent
	 * to calendar.get(Calendar.MILLISECOND))</li>
	 * <li>January 6, 2008 7:15:10.538 with Calendar.MINUTE as fragment will return 10538 (10*1000 +
	 * 538)</li>
	 * <li>January 16, 2008 7:15:10.538 with Calendar.MILLISECOND as fragment will return 0 (a
	 * millisecond cannot be split in milliseconds)</li>
	 * </ul>
	 * </p>
	 * 
	 * @param calendar the calendar to work with, not null
	 * @param fragment the {@code Calendar} field part of calendar to calculate
	 * @return number of milliseconds within the fragment of date
	 * @throws IllegalArgumentException if the date is <code>null</code> or fragment is not
	 *             supported
	 */
	public static long getFragmentInMilliseconds(final Calendar calendar, final int fragment) {
		return getFragment(calendar, fragment, Calendar.MILLISECOND);
	}

	/**
	 * <p>
	 * Returns the number of seconds within the fragment. All datefields greater than the fragment
	 * will be ignored.
	 * </p>
	 * <p>
	 * Asking the seconds of any date will only return the number of seconds of the current minute
	 * (resulting in a number between 0 and 59). This method will retrieve the number of seconds for
	 * any fragment. For example, if you want to calculate the number of seconds past today, your
	 * fragment is Calendar.DATE or Calendar.DAY_OF_YEAR. The result will be all seconds of the past
	 * hour(s) and minutes(s).
	 * </p>
	 * <p>
	 * Valid fragments are: Calendar.YEAR, Calendar.MONTH, both Calendar.DAY_OF_YEAR and
	 * Calendar.DATE, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND and
	 * Calendar.MILLISECOND A fragment less than or equal to a SECOND field will return 0.
	 * </p>
	 * <p>
	 * <ul>
	 * <li>January 1, 2008 7:15:10.538 with Calendar.MINUTE as fragment will return 10 (equivalent
	 * to calendar.get(Calendar.SECOND))</li>
	 * <li>January 6, 2008 7:15:10.538 with Calendar.MINUTE as fragment will return 10 (equivalent
	 * to calendar.get(Calendar.SECOND))</li>
	 * <li>January 6, 2008 7:15:10.538 with Calendar.DAY_OF_YEAR as fragment will return 26110
	 * (7*3600 + 15*60 + 10)</li>
	 * <li>January 16, 2008 7:15:10.538 with Calendar.MILLISECOND as fragment will return 0 (a
	 * millisecond cannot be split in seconds)</li>
	 * </ul>
	 * </p>
	 * 
	 * @param calendar the calendar to work with, not null
	 * @param fragment the {@code Calendar} field part of calendar to calculate
	 * @return number of seconds within the fragment of date
	 * @throws IllegalArgumentException if the date is <code>null</code> or fragment is not
	 *             supported
	 */
	public static long getFragmentInSeconds(final Calendar calendar, final int fragment) {
		return getFragment(calendar, fragment, Calendar.SECOND);
	}

	/**
	 * <p>
	 * Returns the number of minutes within the fragment. All datefields greater than the fragment
	 * will be ignored.
	 * </p>
	 * <p>
	 * Asking the minutes of any date will only return the number of minutes of the current hour
	 * (resulting in a number between 0 and 59). This method will retrieve the number of minutes for
	 * any fragment. For example, if you want to calculate the number of minutes past this month,
	 * your fragment is Calendar.MONTH. The result will be all minutes of the past day(s) and
	 * hour(s).
	 * </p>
	 * <p>
	 * Valid fragments are: Calendar.YEAR, Calendar.MONTH, both Calendar.DAY_OF_YEAR and
	 * Calendar.DATE, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND and
	 * Calendar.MILLISECOND A fragment less than or equal to a MINUTE field will return 0.
	 * </p>
	 * <p>
	 * <ul>
	 * <li>January 1, 2008 7:15:10.538 with Calendar.HOUR_OF_DAY as fragment will return 15
	 * (equivalent to calendar.get(Calendar.MINUTES))</li>
	 * <li>January 6, 2008 7:15:10.538 with Calendar.HOUR_OF_DAY as fragment will return 15
	 * (equivalent to calendar.get(Calendar.MINUTES))</li>
	 * <li>January 1, 2008 7:15:10.538 with Calendar.MONTH as fragment will return 15</li>
	 * <li>January 6, 2008 7:15:10.538 with Calendar.MONTH as fragment will return 435 (7*60 + 15)</li>
	 * <li>January 16, 2008 7:15:10.538 with Calendar.MILLISECOND as fragment will return 0 (a
	 * millisecond cannot be split in minutes)</li>
	 * </ul>
	 * </p>
	 * 
	 * @param calendar the calendar to work with, not null
	 * @param fragment the {@code Calendar} field part of calendar to calculate
	 * @return number of minutes within the fragment of date
	 * @throws IllegalArgumentException if the date is <code>null</code> or fragment is not
	 *             supported
	 */
	public static long getFragmentInMinutes(final Calendar calendar, final int fragment) {
		return getFragment(calendar, fragment, Calendar.MINUTE);
	}

	/**
	 * <p>
	 * Returns the number of hours within the fragment. All datefields greater than the fragment
	 * will be ignored.
	 * </p>
	 * <p>
	 * Asking the hours of any date will only return the number of hours of the current day
	 * (resulting in a number between 0 and 23). This method will retrieve the number of hours for
	 * any fragment. For example, if you want to calculate the number of hours past this month, your
	 * fragment is Calendar.MONTH. The result will be all hours of the past day(s).
	 * </p>
	 * <p>
	 * Valid fragments are: Calendar.YEAR, Calendar.MONTH, both Calendar.DAY_OF_YEAR and
	 * Calendar.DATE, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND and
	 * Calendar.MILLISECOND A fragment less than or equal to a HOUR field will return 0.
	 * </p>
	 * <p>
	 * <ul>
	 * <li>January 1, 2008 7:15:10.538 with Calendar.DAY_OF_YEAR as fragment will return 7
	 * (equivalent to calendar.get(Calendar.HOUR_OF_DAY))</li>
	 * <li>January 6, 2008 7:15:10.538 with Calendar.DAY_OF_YEAR as fragment will return 7
	 * (equivalent to calendar.get(Calendar.HOUR_OF_DAY))</li>
	 * <li>January 1, 2008 7:15:10.538 with Calendar.MONTH as fragment will return 7</li>
	 * <li>January 6, 2008 7:15:10.538 with Calendar.MONTH as fragment will return 127 (5*24 + 7)</li>
	 * <li>January 16, 2008 7:15:10.538 with Calendar.MILLISECOND as fragment will return 0 (a
	 * millisecond cannot be split in hours)</li>
	 * </ul>
	 * </p>
	 * 
	 * @param calendar the calendar to work with, not null
	 * @param fragment the {@code Calendar} field part of calendar to calculate
	 * @return number of hours within the fragment of date
	 * @throws IllegalArgumentException if the date is <code>null</code> or fragment is not
	 *             supported
	 */
	public static long getFragmentInHours(final Calendar calendar, final int fragment) {
		return getFragment(calendar, fragment, Calendar.HOUR_OF_DAY);
	}

	/**
	 * <p>
	 * Returns the number of days within the fragment. All datefields greater than the fragment will
	 * be ignored.
	 * </p>
	 * <p>
	 * Asking the days of any date will only return the number of days of the current month
	 * (resulting in a number between 1 and 31). This method will retrieve the number of days for
	 * any fragment. For example, if you want to calculate the number of days past this year, your
	 * fragment is Calendar.YEAR. The result will be all days of the past month(s).
	 * </p>
	 * <p>
	 * Valid fragments are: Calendar.YEAR, Calendar.MONTH, both Calendar.DAY_OF_YEAR and
	 * Calendar.DATE, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND and
	 * Calendar.MILLISECOND A fragment less than or equal to a DAY field will return 0.
	 * </p>
	 * <p>
	 * <ul>
	 * <li>January 28, 2008 with Calendar.MONTH as fragment will return 28 (equivalent to
	 * calendar.get(Calendar.DAY_OF_MONTH))</li>
	 * <li>February 28, 2008 with Calendar.MONTH as fragment will return 28 (equivalent to
	 * calendar.get(Calendar.DAY_OF_MONTH))</li>
	 * <li>January 28, 2008 with Calendar.YEAR as fragment will return 28 (equivalent to
	 * calendar.get(Calendar.DAY_OF_YEAR))</li>
	 * <li>February 28, 2008 with Calendar.YEAR as fragment will return 59 (equivalent to
	 * calendar.get(Calendar.DAY_OF_YEAR))</li>
	 * <li>January 28, 2008 with Calendar.MILLISECOND as fragment will return 0 (a millisecond
	 * cannot be split in days)</li>
	 * </ul>
	 * </p>
	 * 
	 * @param calendar the calendar to work with, not null
	 * @param fragment the {@code Calendar} field part of calendar to calculate
	 * @return number of days within the fragment of date
	 * @throws IllegalArgumentException if the date is <code>null</code> or fragment is not
	 *             supported
	 */
	public static long getFragmentInDays(final Calendar calendar, final int fragment) {
		return getFragment(calendar, fragment, Calendar.DAY_OF_YEAR);
	}

	/**
	 * Date-version for fragment-calculation in any unit
	 * 
	 * @param date the date to work with, not null
	 * @param fragment the Calendar field part of date to calculate
	 * @param unit the {@code Calendar} field defining the unit
	 * @return number of units within the fragment of the date
	 * @throws IllegalArgumentException if the date is <code>null</code> or fragment is not
	 *             supported
	 */
	private static long getFragment(final Date date, final int fragment, final int unit) {
		if (date == null) {
			throw new IllegalArgumentException("The date must not be null");
		}
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return getFragment(calendar, fragment, unit);
	}

	/**
	 * Calendar-version for fragment-calculation in any unit
	 * 
	 * @param calendar the calendar to work with, not null
	 * @param fragment the Calendar field part of calendar to calculate
	 * @param unit the {@code Calendar} field defining the unit
	 * @return number of units within the fragment of the calendar
	 * @throws IllegalArgumentException if the date is <code>null</code> or fragment is not
	 *             supported
	 */
	private static long getFragment(final Calendar calendar, final int fragment, final int unit) {
		if (calendar == null) {
			throw new IllegalArgumentException("The date must not be null");
		}
		final long millisPerUnit = getMillisPerUnit(unit);
		long result = 0;

		// Fragments bigger than a day require a breakdown to days
		switch (fragment) {
		case Calendar.YEAR:
			result += (calendar.get(Calendar.DAY_OF_YEAR) * MS_DAY) / millisPerUnit;
			break;
		case Calendar.MONTH:
			result += (calendar.get(Calendar.DAY_OF_MONTH) * MS_DAY) / millisPerUnit;
			break;
		}

		switch (fragment) {
		// Number of days already calculated for these cases
		case Calendar.YEAR:
		case Calendar.MONTH:

			// The rest of the valid cases
		case Calendar.DAY_OF_YEAR:
		case Calendar.DATE:
			result += (calendar.get(Calendar.HOUR_OF_DAY) * MS_HOUR) / millisPerUnit;
			//$FALL-THROUGH$
		case Calendar.HOUR_OF_DAY:
			result += (calendar.get(Calendar.MINUTE) * MS_MINUTE) / millisPerUnit;
			//$FALL-THROUGH$
		case Calendar.MINUTE:
			result += (calendar.get(Calendar.SECOND) * MS_SECOND) / millisPerUnit;
			//$FALL-THROUGH$
		case Calendar.SECOND:
			result += (calendar.get(Calendar.MILLISECOND) * 1) / millisPerUnit;
			break;
		case Calendar.MILLISECOND:
			break;// never useful
		default:
			throw new IllegalArgumentException("The fragment " + fragment + " is not supported");
		}
		return result;
	}

	/**
	 * Determines if two calendars are equal up to no more than the specified most significant
	 * field.
	 * 
	 * @param cal1 the first calendar, not <code>null</code>
	 * @param cal2 the second calendar, not <code>null</code>
	 * @param field the field from {@code Calendar}
	 * @return <code>true</code> if equal; otherwise <code>false</code>
	 * @throws IllegalArgumentException if any argument is <code>null</code>
	 * @see #truncate(Calendar, int)
	 * @see #truncatedEquals(Date, Date, int)
	 */
	public static boolean truncatedEquals(final Calendar cal1, final Calendar cal2, final int field) {
		return truncatedCompareTo(cal1, cal2, field) == 0;
	}

	/**
	 * Determines if two dates are equal up to no more than the specified most significant field.
	 * 
	 * @param date1 the first date, not <code>null</code>
	 * @param date2 the second date, not <code>null</code>
	 * @param field the field from {@code Calendar}
	 * @return <code>true</code> if equal; otherwise <code>false</code>
	 * @throws IllegalArgumentException if any argument is <code>null</code>
	 * @see #truncate(Date, int)
	 * @see #truncatedEquals(Calendar, Calendar, int)
	 */
	public static boolean truncatedEquals(final Date date1, final Date date2, final int field) {
		return truncatedCompareTo(date1, date2, field) == 0;
	}

	/**
	 * Determines how two calendars compare up to no more than the specified most significant field.
	 * 
	 * @param cal1 the first calendar, not <code>null</code>
	 * @param cal2 the second calendar, not <code>null</code>
	 * @param field the field from {@code Calendar}
	 * @return a negative integer, zero, or a positive integer as the first calendar is less than,
	 *         equal to, or greater than the second.
	 * @throws IllegalArgumentException if any argument is <code>null</code>
	 * @see #truncate(Calendar, int)
	 * @see #truncatedCompareTo(Date, Date, int)
	 */
	public static int truncatedCompareTo(final Calendar cal1, final Calendar cal2, final int field) {
		final Calendar truncatedCal1 = truncate(cal1, field);
		final Calendar truncatedCal2 = truncate(cal2, field);
		return truncatedCal1.compareTo(truncatedCal2);
	}

	/**
	 * Determines how two dates compare up to no more than the specified most significant field.
	 * 
	 * @param date1 the first date, not <code>null</code>
	 * @param date2 the second date, not <code>null</code>
	 * @param field the field from <code>Calendar</code>
	 * @return a negative integer, zero, or a positive integer as the first date is less than, equal
	 *         to, or greater than the second.
	 * @throws IllegalArgumentException if any argument is <code>null</code>
	 * @see #truncate(Calendar, int)
	 * @see #truncatedCompareTo(Date, Date, int)
	 */
	public static int truncatedCompareTo(final Date date1, final Date date2, final int field) {
		final Date truncatedDate1 = truncate(date1, field);
		final Date truncatedDate2 = truncate(date2, field);
		return truncatedDate1.compareTo(truncatedDate2);
	}

	/**
	 * Returns the number of milliseconds of a {@code Calendar} field, if this is a constant value.
	 * This handles millisecond, second, minute, hour and day (even though days can very in length).
	 * 
	 * @param unit a {@code Calendar} field constant which is a valid unit for a fragment
	 * @return the number of milliseconds in the field
	 * @throws IllegalArgumentException if date can't be represented in milliseconds
	 */
	private static long getMillisPerUnit(final int unit) {
		long result = Long.MAX_VALUE;
		switch (unit) {
		case Calendar.DAY_OF_YEAR:
		case Calendar.DATE:
			result = MS_DAY;
			break;
		case Calendar.HOUR_OF_DAY:
			result = MS_HOUR;
			break;
		case Calendar.MINUTE:
			result = MS_MINUTE;
			break;
		case Calendar.SECOND:
			result = MS_SECOND;
			break;
		case Calendar.MILLISECOND:
			result = 1;
			break;
		default:
			throw new IllegalArgumentException("The unit " + unit + " cannot be represented is milleseconds");
		}
		return result;
	}

	// -----------------------------------------------------------------------
	/**
	 * <p>
	 * Date iterator.
	 * </p>
	 */
	static class DateIterator implements Iterator<Calendar> {
		private final Calendar endFinal;
		private final Calendar spot;

		/**
		 * Constructs a DateIterator that ranges from one date to another.
		 * 
		 * @param startFinal start date (inclusive)
		 * @param endFinal end date (not inclusive)
		 */
		DateIterator(final Calendar startFinal, final Calendar endFinal) {
			super();
			this.endFinal = endFinal;
			spot = startFinal;
			spot.add(Calendar.DATE, -1);
		}

		/**
		 * Has the iterator not reached the end date yet?
		 * 
		 * @return <code>true</code> if the iterator has yet to reach the end date
		 */
		@Override
		public boolean hasNext() {
			return spot.before(endFinal);
		}

		/**
		 * Return the next calendar in the iteration
		 * 
		 * @return Object calendar for the next date
		 */
		@Override
		public Calendar next() {
			if (spot.equals(endFinal)) {
				throw new NoSuchElementException();
			}
			spot.add(Calendar.DATE, 1);
			return (Calendar)spot.clone();
		}

		/**
		 * Always throws UnsupportedOperationException.
		 * 
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	// -----------------------------------------------------------------------
	/**
	 * ISO8601 formatter for date-time without time zone. The format used is
	 * <tt>yyyy-MM-dd'T'HH:mm:ss</tt>.
	 */
	public static FastDateFormat isoDatetimeFormat() {
		return ISO_DATETIME_FORMAT;
	}
	
	/**
	 * ISO8601 formatter for date-time without time zone and prefix 'T' char. The format used is
	 * <tt>yyyy-MM-dd HH:mm:ss</tt>.
	 */
	public static FastDateFormat datetimeFormat() {
		return ISO_DATETIME_NO_T_FORMAT;
	}
	
	/**
	 * ISO8601 formatter for date-time without time zone and separator '-' char. The format used is
	 * <tt>yyyyMMdd'T'HH:mm:ss</tt>.
	 */
	public static FastDateFormat isoDatetimeNohFormat() {
		return ISO_DATETIME_NO_H_FORMAT;
	}
	
	/**
	 * ISO8601 formatter for date without time zone. The format used is <tt>yyyy-MM-dd</tt>.
	 */
	public static FastDateFormat dateFormat() {
		return ISO_DATE_FORMAT;
	}
	
	/**
	 * ISO8601-like formatter for time without time zone. The format used is <tt>HH:mm:ss</tt>. This
	 * pattern does not comply with the formal ISO8601 specification as the standard requires the
	 * 'T' prefix for times.
	 */
	public static FastDateFormat timeFormat() {
		return ISO_TIME_NO_T_FORMAT;
	}
	
	/**
	 * Timestamp format. The format used is <tt>yyyy-MM-dd HH:mm:ss.SSS</tt>. 
	 */
	public static FastDateFormat timestampFormat() {
		return TIMESTAMP_FORMAT;
	}
	
	/**
	 * Timestamp format for log. The format used is <tt>yyyyMMdd'T'HHmmss.SSS</tt>. 
	 */
	public static FastDateFormat timestampLogFormat() {
		return TIMESTAMP_LOG_FORMAT;
	}

	/**
	 * DateTime format for log. The format used is <tt>yyyyMMddTHHmmss</tt>. 
	 */
	public static FastDateFormat datetimeLogFormat() {
		return DATETIME_LOG_FORMAT;
	}

	/**
	 * Date format for log. The format used is <tt>yyyyMMdd</tt>. 
	 */
	public static FastDateFormat dateLogFormat() {
		return DATE_LOG_FORMAT;
	}

	/**
	 * Time format for log. The format used is <tt>HHmmss</tt>. 
	 */
	public static final FastDateFormat timeLogFormat() {
		return TIME_LOG_FORMAT;
	}
	
	/**
	 * <p>
	 * Formats a date/time into a specific pattern using the UTC time zone.
	 * </p>
	 * 
	 * @param millis the date to format expressed in milliseconds
	 * @param pattern the pattern to use to format the date, not null
	 * @return the formatted date
	 */
	public static String formatUTC(final long millis, final String pattern) {
		return format(new Date(millis), pattern, UTC_TIME_ZONE, null);
	}

	/**
	 * <p>
	 * Formats a date/time into a specific pattern using the UTC time zone.
	 * </p>
	 * 
	 * @param date the date to format, not null
	 * @param pattern the pattern to use to format the date, not null
	 * @return the formatted date
	 */
	public static String formatUTC(final Date date, final String pattern) {
		return format(date, pattern, UTC_TIME_ZONE, null);
	}

	/**
	 * <p>
	 * Formats a date/time into a specific pattern using the UTC time zone.
	 * </p>
	 * 
	 * @param millis the date to format expressed in milliseconds
	 * @param pattern the pattern to use to format the date, not null
	 * @param locale the locale to use, may be <code>null</code>
	 * @return the formatted date
	 */
	public static String formatUTC(final long millis, final String pattern, final Locale locale) {
		return format(new Date(millis), pattern, UTC_TIME_ZONE, locale);
	}

	/**
	 * <p>
	 * Formats a date/time into a specific pattern using the UTC time zone.
	 * </p>
	 * 
	 * @param date the date to format, not null
	 * @param pattern the pattern to use to format the date, not null
	 * @param locale the locale to use, may be <code>null</code>
	 * @return the formatted date
	 */
	public static String formatUTC(final Date date, final String pattern, final Locale locale) {
		return format(date, pattern, UTC_TIME_ZONE, locale);
	}

	/**
	 * <p>
	 * Formats a date/time into a specific pattern.
	 * </p>
	 * 
	 * @param millis the date to format expressed in milliseconds
	 * @param pattern the pattern to use to format the date, not null
	 * @return the formatted date
	 */
	public static String format(final long millis, final String pattern) {
		return format(new Date(millis), pattern, null, null);
	}

	/**
	 * <p>
	 * Formats a date/time into a specific pattern.
	 * </p>
	 * 
	 * @param date the date to format, not null
	 * @param pattern the pattern to use to format the date, not null
	 * @return the formatted date
	 */
	public static String format(final Date date, final String pattern) {
		return format(date, pattern, null, null);
	}

	/**
	 * <p>
	 * Formats a calendar into a specific pattern.
	 * </p>
	 * 
	 * @param calendar the calendar to format, not null
	 * @param pattern the pattern to use to format the calendar, not null
	 * @return the formatted calendar
	 * @see FastDateFormat#format(Calendar)
	 */
	public static String format(final Calendar calendar, final String pattern) {
		return format(calendar, pattern, null, null);
	}

	/**
	 * <p>
	 * Formats a date/time into a specific pattern in a time zone.
	 * </p>
	 * 
	 * @param millis the time expressed in milliseconds
	 * @param pattern the pattern to use to format the date, not null
	 * @param timeZone the time zone to use, may be <code>null</code>
	 * @return the formatted date
	 */
	public static String format(final long millis, final String pattern, final TimeZone timeZone) {
		return format(new Date(millis), pattern, timeZone, null);
	}

	/**
	 * <p>
	 * Formats a date/time into a specific pattern in a time zone.
	 * </p>
	 * 
	 * @param date the date to format, not null
	 * @param pattern the pattern to use to format the date, not null
	 * @param timeZone the time zone to use, may be <code>null</code>
	 * @return the formatted date
	 */
	public static String format(final Date date, final String pattern, final TimeZone timeZone) {
		return format(date, pattern, timeZone, null);
	}

	/**
	 * <p>
	 * Formats a calendar into a specific pattern in a time zone.
	 * </p>
	 * 
	 * @param calendar the calendar to format, not null
	 * @param pattern the pattern to use to format the calendar, not null
	 * @param timeZone the time zone to use, may be <code>null</code>
	 * @return the formatted calendar
	 * @see FastDateFormat#format(Calendar)
	 */
	public static String format(final Calendar calendar, final String pattern, final TimeZone timeZone) {
		return format(calendar, pattern, timeZone, null);
	}

	/**
	 * <p>
	 * Formats a date/time into a specific pattern in a locale.
	 * </p>
	 * 
	 * @param millis the date to format expressed in milliseconds
	 * @param pattern the pattern to use to format the date, not null
	 * @param locale the locale to use, may be <code>null</code>
	 * @return the formatted date
	 */
	public static String format(final long millis, final String pattern, final Locale locale) {
		return format(new Date(millis), pattern, null, locale);
	}

	/**
	 * <p>
	 * Formats a date/time into a specific pattern in a locale.
	 * </p>
	 * 
	 * @param date the date to format, not null
	 * @param pattern the pattern to use to format the date, not null
	 * @param locale the locale to use, may be <code>null</code>
	 * @return the formatted date
	 */
	public static String format(final Date date, final String pattern, final Locale locale) {
		return format(date, pattern, null, locale);
	}

	/**
	 * <p>
	 * Formats a calendar into a specific pattern in a locale.
	 * </p>
	 * 
	 * @param calendar the calendar to format, not null
	 * @param pattern the pattern to use to format the calendar, not null
	 * @param locale the locale to use, may be <code>null</code>
	 * @return the formatted calendar
	 * @see FastDateFormat#format(Calendar)
	 */
	public static String format(final Calendar calendar, final String pattern, final Locale locale) {
		return format(calendar, pattern, null, locale);
	}

	/**
	 * <p>
	 * Formats a date/time into a specific pattern in a time zone and locale.
	 * </p>
	 * 
	 * @param millis the date to format expressed in milliseconds
	 * @param pattern the pattern to use to format the date, not null
	 * @param timeZone the time zone to use, may be <code>null</code>
	 * @param locale the locale to use, may be <code>null</code>
	 * @return the formatted date
	 */
	public static String format(final long millis, final String pattern, final TimeZone timeZone, final Locale locale) {
		return format(new Date(millis), pattern, timeZone, locale);
	}

	/**
	 * <p>
	 * Formats a date/time into a specific pattern in a time zone and locale.
	 * </p>
	 * 
	 * @param date the date to format, not null
	 * @param pattern the pattern to use to format the date, not null, not null
	 * @param timeZone the time zone to use, may be <code>null</code>
	 * @param locale the locale to use, may be <code>null</code>
	 * @return the formatted date
	 */
	public static String format(final Date date, final String pattern, final TimeZone timeZone, final Locale locale) {
		final FastDateFormat df = FastDateFormat.getInstance(pattern, timeZone, locale);
		return df.format(date);
	}

	/**
	 * <p>
	 * Formats a calendar into a specific pattern in a time zone and locale.
	 * </p>
	 * 
	 * @param calendar the calendar to format, not null
	 * @param pattern the pattern to use to format the calendar, not null
	 * @param timeZone the time zone to use, may be <code>null</code>
	 * @param locale the locale to use, may be <code>null</code>
	 * @return the formatted calendar
	 * @see FastDateFormat#format(Calendar)
	 */
	public static String format(final Calendar calendar, final String pattern, final TimeZone timeZone,
			final Locale locale) {
		final FastDateFormat df = FastDateFormat.getInstance(pattern, timeZone, locale);
		return df.format(calendar);
	}

	// -----------------------------------------------------------------------
	public static Date parse(final String source, final String ... patterns) throws ParseException {
		return parse(source, null, patterns);
	}
	
	public static Date parse(final String source, Locale locale, final String ... patterns) throws ParseException {
		if (source == null || patterns == null) {
			throw new IllegalArgumentException("Date and Patterns must not be null");
		}
		
		for (String pattern : patterns) {
			try {
				return parse(source, pattern, null, locale);
			}
			catch (ParseException e) {
				//skip
			}
		}
		throw new ParseException("Unable to parse the date: " + source, -1);
	}
	
	public static Date parse(final String source, final String pattern) throws ParseException {
		return parse(source, pattern, null, null);
	}
	
	public static Date parse(final String source, final String pattern, final TimeZone timeZone) throws ParseException {
		return parse(source, pattern, timeZone, null);
	}
	
	public static Date parse(final String source, final String pattern, final Locale locale) throws ParseException {
		return parse(source, pattern, null, locale);
	}
	
	public static Date parse(final String source, final String pattern, final TimeZone timeZone, final Locale locale) throws ParseException {
		final FastDateFormat df = FastDateFormat.getInstance(pattern, timeZone, locale);
		return df.parse(source);
	}

	// -----------------------------------------------------------------------
	public static Date safeParse(final String source, final String ... patterns) {
		return safeParse(source, null, patterns);
	}
	
	public static Date safeParse(final String source, Locale locale, final String ... patterns) {
		if (source == null || patterns == null) {
			return null;
		}
		
		for (String pattern : patterns) {
			try {
				return parse(source, pattern, null, locale);
			}
			catch (ParseException e) {
				//skip
			}
		}
		return null;
	}
	
	public static Date safeParse(final String source, final String pattern) {
		return safeParse(source, pattern, null, null);
	}
	
	public static Date safeParse(final String source, final String pattern, final TimeZone timeZone) {
		return safeParse(source, pattern, timeZone, null);
	}
	
	public static Date safeParse(final String source, final String pattern, final Locale locale) {
		return safeParse(source, pattern, null, locale);
	}
	
	public static Date safeParse(final String source, final String pattern, final TimeZone timeZone, final Locale locale) {
		final FastDateFormat df = FastDateFormat.getInstance(pattern, timeZone, locale);
		return safeParse(df, source);
	}
	
	public static Date safeParse(final FastDateFormat format, final String source) {
		try {
			return format.parse(source);
		}
		catch (ParseException e) {
			return null;
		}
	}
	
	//----------------------------------------------------------
	public static long diffSeconds(Date to, Date from) {
		return (to.getTime() - from.getTime()) / MS_SECOND;
	}

	public static long diffMinutes(Date to, Date from) {
		return (to.getTime() - from.getTime()) / MS_MINUTE;
	}

	public static long diffHours(Date to, Date from) {
		return (to.getTime() - from.getTime()) / MS_HOUR;
	}

	public static long diffDays(Date to, Date from) {
		return (to.getTime() - from.getTime()) / MS_DAY;
	}

	public static long diffWeeks(Date to, Date from) {
		return (to.getTime() - from.getTime()) / MS_WEEK;
	}

	public static long diffMonths(Date to, Date from) {
		return (to.getTime() - from.getTime()) / MS_MONTH;
	}

	public static long diffYears(Date to, Date from) {
		return (to.getTime() - from.getTime()) / MS_YEAR;
	}
}
