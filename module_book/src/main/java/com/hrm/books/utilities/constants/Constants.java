package com.hrm.books.utilities.constants;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public abstract class Constants {
    public static final String host = "localhost";
    public static final String PATH_IMAGES = "src/main/images";
    public static final ZoneId HO_CHI_MINH_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
//
//    @Value("${secret.key}")
//    public static String SECRET_KEY;

    public static final String SECRET_KEY = "toicrushminhthu123@iloveyouCRUSHEMTRATNHIEU1234567890NGUYENTHIMINHTHU000000000000000000000";
    public static final long TIME_EXPIRATION = 6 * 60 * 60 * 1000;
    public static final DateTimeFormatter DATE_TIME_FORMATTER_SLASH = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    public static final DateTimeFormatter DATE_TIME_FORMATTER_DASH = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public static final String[] actions = {"information, address, bill"};
}
