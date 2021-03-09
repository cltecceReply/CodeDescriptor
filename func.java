public static long gregorianToJulian(long date) throws ParseException {
        return Long.parseLong(new SimpleDateFormat("yyyyDDD")
                .format(new SimpleDateFormat("yyyyMMdd")
                        .parse(String.valueOf(date)))
        );
    }