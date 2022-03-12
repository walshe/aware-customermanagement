package com.emmett.customermanagement;

import java.time.LocalDate;
import java.time.Month;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class SQLGenerator {
    public static void main(String[] args) {

        LocalDate start = LocalDate.of(1952, Month.JUNE, 11);
        LocalDate end = LocalDate.of(1996, Month.JUNE, 11);

        IntStream.range(0, 1000).boxed().forEach(n -> {
            System.out.printf("INSERT INTO CUSTOMER (name, birth_date, gender, external_customer_id) VALUES ('name_%d', '%s', '%s','external_id_%d');\n",
                    n, between(start, end).toString(), (n%2==0)?"MALE":"FEMALE", n);
        });
    }

    public static LocalDate between(LocalDate startInclusive, LocalDate endExclusive) {
    long startEpochDay = startInclusive.toEpochDay();
    long endEpochDay = endExclusive.toEpochDay();
    long randomDay = ThreadLocalRandom
      .current()
      .nextLong(startEpochDay, endEpochDay);

    return LocalDate.ofEpochDay(randomDay);
}
}
