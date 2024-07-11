package com.example.Student_Record_Data;

public class Student {
    private String name;
    private String nim;
    private String course;
    private String ipk;

    public Student(String name, String nim, String course, String ipk) {
        this.name = name;
        this.nim = nim;
        this.course = course;
        this.ipk = ipk;
    }

    public String getName() {
        return name;
    }

    public String getNim() {
        return nim;
    }

    public String getCourse() {
        return course;
    }

    public String getIpk() {
        return ipk;
    }
}

