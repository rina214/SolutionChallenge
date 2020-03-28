package com.example.solutionchallenge;

import java.util.ArrayList;

public class Course {
    private String building;
    private String classroom;
    private ArrayList<Long> day;
    private String name;
    private String prof;
    private String start;
    private Long time;

    public Course(String building, String classroom, ArrayList<Long> day, String name, String prof, String start, Long time) {
        this.building = building;
        this.classroom = classroom;
        this.day = day;
        this.name = name;
        this.prof = prof;
        this.start = start;
        this.time = time;
    }

    String getBuilding() { return this.building; }
    String getClassroom() { return this.classroom; }
    ArrayList<Long> getDay() { return this.day; }
    String getName() {
        return this.name;
    }
    String getProf() { return this.prof; }
    String getStart() { return this.start; }
    Long getTime() { return  this.time; }
}
