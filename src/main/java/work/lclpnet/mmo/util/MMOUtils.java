package work.lclpnet.mmo.util;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class MMOUtils {

    public static boolean isSpecialDate() {
        return isSpecialDate(new Date());
    }

    public static boolean isSpecialDate(Date d) {
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

    public static float randomPitch(Random ran, float min, float max) {
        return min + ran.nextFloat() * (max - min);
    }

    public static class Data {

        public static void encodeGzBase64(InputStream uncompressedIn, OutputStream gzBase64Out) throws IOException {
            try (Base64OutputStream base64Out = new Base64OutputStream(gzBase64Out, true, 0, null);
                 GZIPOutputStream gzipOut = new GZIPOutputStream(base64Out)) {
                byte[] buffer = new byte[1024];
                int read;
                while ((read = uncompressedIn.read(buffer)) != -1)
                    gzipOut.write(buffer, 0, read);
            }
        }

        public static void decodeGzBase64(InputStream gzBase64In, OutputStream uncompressedOut) throws IOException {
            try (Base64InputStream base64In = new Base64InputStream(gzBase64In);
                 GZIPInputStream gzipIn = new GZIPInputStream(base64In)) {
                byte[] buffer = new byte[1024];
                int read;
                while ((read = gzipIn.read(buffer)) != -1)
                    uncompressedOut.write(buffer, 0, read);
            }
        }
    }
}
