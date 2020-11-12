package work.lclpnet.mmo.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public static boolean isSpecialDay(Date d) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		
		int month = cal.get(Calendar.MONTH), day = cal.get(Calendar.DATE);
		boolean aprilFirst = month == Calendar.APRIL && day == 1,
				newYear = month == Calendar.JANUARY && day == 1 || month == Calendar.DECEMBER && day == 31,
				christmas = month == Calendar.DECEMBER && (day >= 24 && day <= 26),
				leapYearDay = month == Calendar.FEBRUARY && day == 29,
				stPatrick = month == Calendar.MARCH && day == 17,
				devBirthday = month == Calendar.MARCH && day == 1,
				halloween = month == Calendar.OCTOBER && day == 31,
				stNicholas = month == Calendar.DECEMBER && day == 6;
		
		return aprilFirst || newYear || christmas || leapYearDay || stPatrick || devBirthday || halloween || stNicholas;
	}
	
}
