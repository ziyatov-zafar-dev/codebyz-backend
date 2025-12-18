package uz.codebyz.common;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Helper {
    public static ZonedDateTime currentTimeZoneTimeDate() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Istanbul"));
        return now;
    }
    public static Instant currentTimeInstant() {
        return ZonedDateTime.now(ZoneId.of("Europe/Istanbul")).toInstant();
    }
}
