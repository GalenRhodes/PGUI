package com.projectgalen.lib.ui.test;

// ===========================================================================
//     PROJECT: PGUI
//    FILENAME: TestData.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: October 14, 2023
//
// Copyright © 2023 Project Galen. All rights reserved.
//
// Permission to use, copy, modify, and distribute this software for any
// purpose with or without fee is hereby granted, provided that the above
// copyright notice and this permission notice appear in all copies.
//
// THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
// WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
// SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
// WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
// ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
// IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
// ===========================================================================

import com.projectgalen.lib.utils.U;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TestData implements Comparable<TestData> {
    private String     name;
    private int        age;
    private TestEnum   gender;
    private BigDecimal salary;

    public TestData(String name, int age, TestEnum gender, BigDecimal salary) {
        this.name   = name;
        this.age    = age;
        this.salary = salary;
        this.gender = gender;
    }

    public @Override int compareTo(@NotNull TestData o) {
        int cc = U.compare(name, o.name);
        if(cc != 0) return cc;
        if((cc = (age - o.age)) != 0) return cc;
        if((cc = U.compare(gender, o.gender)) != 0) return cc;
        return U.compare(salary, o.salary);
    }

    public @Override boolean equals(Object obj) {
        return ((this == obj) || (obj instanceof TestData other) && _equals(other));
    }

    public int getAge() {
        return age;
    }

    public TestEnum getGender() {
        return gender;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public @Override int hashCode() {
        return Objects.hash(name, age, gender, salary);
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setGender(TestEnum gender) {
        this.gender = gender;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public @Override String toString() {
        return "{\n\t\"name\": \"%s\",\n\t\"age\": %d,\n\t\"gender\": \"%s\",\n\t\"salary\": \"$%,1.2f\"\n}".formatted(name, age, gender, salary.doubleValue());
    }

    private boolean _equals(@NotNull TestData other) {
        return (Objects.equals(name, other.name) && (age == other.age) && (gender == other.gender) && Objects.equals(salary, other.salary));
    }

    public static @NotNull List<TestData> createTestData(int recordCount) {
        List<TestData> testData  = new ArrayList<>();
        List<String>   wordList  = loadWordList();
        int            wordCount = wordList.size();

        for(int i = 0; i < recordCount; i++) {
            String     firstName = wordList.get((int)(Math.random() * wordCount));
            String     lastName  = wordList.get((int)(Math.random() * wordCount));
            TestEnum   gender    = TestEnum.valueOf((int)(Math.random() * 3));
            int        age       = (int)(randomInRange(21, 72)); // Ages ranged between 21 and 71 inclusive.
            BigDecimal salary    = BigDecimal.valueOf(randomInRange(30_000.00, 150_000.00)); // Salaries ranged between $30,000 and $149,999.99 inclusive.

            testData.add(new TestData("%C%s %C%s".formatted(firstName.charAt(0), firstName.substring(1), lastName.charAt(0), lastName.substring(1)), age, gender, salary));
        }

        return testData;
    }

    private static @NotNull List<String> loadWordList() {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(TestData.class.getResourceAsStream("words_alpha.txt")), StandardCharsets.UTF_8))) {
            List<String> wordList = new ArrayList<>();
            String       line     = reader.readLine();
            while(line != null) {
                wordList.add(line.trim());
                line = reader.readLine();
            }
            return wordList;
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static double randomInRange(double low, double high) {
        return (low + (Math.random() * (high - low)));
    }
}
