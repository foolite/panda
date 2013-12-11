package panda.castor.castors;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import panda.castor.CastContext;
import panda.castor.Castor;
import panda.lang.time.DateTimes;
import panda.lang.time.FastDateFormat;

/**
 * 
 * @author yf.frank.wang@gmail.com
 *
 */
public class DateTypeCastor {
	public static class DateCastor extends Castor<Object, Date> {
		public static FastDateFormat[] DATE_FORMATS = {
			DateTimes.timestampFormat(),
			DateTimes.datetimeFormat(),
			DateTimes.dateFormat(),
			DateTimes.timeFormat(),
		};
	
		public DateCastor() {
			super(Object.class, Date.class);
		}
		
		@Override
		protected Date castValue(Object value, CastContext context) {
			if (value instanceof Number) {
				Number num = (Number)value;
				return new Date(num.longValue());
			}
			if (value instanceof Calendar) {
				return ((Calendar)value).getTime();
			}
			if (value instanceof java.sql.Date) {
				return new Date(((java.sql.Date)value).getTime());
			}
			if (value instanceof java.sql.Time) {
				return new Date(((java.sql.Time)value).getTime());
			}
			if (value instanceof java.sql.Timestamp) {
				return new Date(((java.sql.Timestamp)value).getTime());
			}
			if (value instanceof CharSequence) {
				String sv = value.toString();
	
				ParseException ex = null;
				for (FastDateFormat df : DATE_FORMATS) {
					try {
						return df.parse(sv);
					}
					catch (ParseException e) {
						ex = e;
					}
				}
				throw castError(value, context, ex);
			}
			throw castError(value, context);
		}
	
		@Override
		public Date castValueTo(Object value, Date target, CastContext context) {
			Date v = castValue(value, context);
			target.setTime(v.getTime());
			return target;
		}
		
		/**
		 * @return default date format
		 */
		public FastDateFormat getDefaultDateFormat() {
			return DateTimes.timestampFormat();
		}
	
		/**
		 * getDateFormat
		 *
		 * @param pattern pattern
		 * @param locale locale
		 * @param timezone time zone
		 * @return DateFormat object
		 */
		public FastDateFormat getDateFormat(String pattern, Locale locale, TimeZone timezone) {
			return FastDateFormat.getInstance(pattern, timezone, locale);
		}
	}

	//------------------------------------------------------------------------------------
	public static class CalendarCastor extends Castor<Object, Calendar> {
		private DateCastor dateCastor;
		
		public CalendarCastor(DateCastor dateCastor) {
			super(Object.class, Calendar.class);
			this.dateCastor = dateCastor;
		}
		
		@Override
		protected Calendar castValue(Object value, CastContext context) {
			Date d = dateCastor.cast(value, context);
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			return c;
		}

		@Override
		protected Calendar castValueTo(Object value, Calendar target, CastContext context) {
			Date d = dateCastor.cast(value, context);
			target.setTime(d);
			return target;
		}
	}

	//------------------------------------------------------------------------------------
	public static class GregorianCalendarCastor extends Castor<Object, GregorianCalendar> {
		private DateCastor dateCastor;
		
		public GregorianCalendarCastor(DateCastor dateCastor) {
			super(Object.class, GregorianCalendar.class);
			this.dateCastor = dateCastor;
		}
		
		@Override
		protected GregorianCalendar castValue(Object value, CastContext context) {
			Date d = dateCastor.cast(value, context);
			GregorianCalendar c = new GregorianCalendar();
			c.setTime(d);
			return c;
		}

		@Override
		protected GregorianCalendar castValueTo(Object value, GregorianCalendar target, CastContext context) {
			Date d = dateCastor.cast(value, context);
			target.setTime(d);
			return target;
		}
	}
	
	//------------------------------------------------------------------------------------
	public static class SqlDateCastor extends Castor<Object, java.sql.Date> {
		private DateCastor dateCastor;
		
		public SqlDateCastor(DateCastor dateCastor) {
			super(Object.class, java.sql.Date.class);
			this.dateCastor = dateCastor;
		}
		
		@Override
		protected java.sql.Date castValue(Object value, CastContext context) {
			Date d = dateCastor.cast(value, context);
			java.sql.Date sd = new java.sql.Date(d.getTime());
			return sd;
		}

		@Override
		protected java.sql.Date castValueTo(Object value, java.sql.Date target, CastContext context) {
			Date d = dateCastor.cast(value, context);
			target.setTime(d.getTime());
			return target;
		}
	}
	
	//------------------------------------------------------------------------------------
	public static class SqlTimeCastor extends Castor<Object, java.sql.Time> {
		private DateCastor dateCastor;
		
		public SqlTimeCastor(DateCastor dateCastor) {
			super(Object.class, java.sql.Time.class);
			this.dateCastor = dateCastor;
		}
		
		@Override
		protected java.sql.Time castValue(Object value, CastContext context) {
			Date d = dateCastor.cast(value, context);
			java.sql.Time st = new java.sql.Time(d.getTime());
			return st;
		}

		@Override
		protected java.sql.Time castValueTo(Object value, java.sql.Time target, CastContext context) {
			Date d = dateCastor.cast(value, context);
			target.setTime(d.getTime());
			return target;
		}
	}
	
	//------------------------------------------------------------------------------------
	public static class SqlTimestampCastor extends Castor<Object, java.sql.Timestamp> {
		private DateCastor dateCastor;
		
		public SqlTimestampCastor(DateCastor dateCastor) {
			super(Object.class, java.sql.Timestamp.class);
			this.dateCastor = dateCastor;
		}
		
		@Override
		protected java.sql.Timestamp castValue(Object value, CastContext context) {
			Date d = dateCastor.cast(value, context);
			java.sql.Timestamp st = new java.sql.Timestamp(d.getTime());
			return st;
		}

		@Override
		protected java.sql.Timestamp castValueTo(Object value, java.sql.Timestamp target, CastContext context) {
			Date d = dateCastor.cast(value, context);
			target.setTime(d.getTime());
			return target;
		}
	}
}
