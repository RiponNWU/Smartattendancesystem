package com.example.smartattendancesystem;

import java.util.ArrayList;

import javax.security.auth.Subject;

public class Session {
    private int ID;
    private ArrayList<Student>students;
    private String Subject;
    private String Date;
    public Session(){
        students = new ArrayList<>();
    }
    public int getID(){
        return ID;
    }
    public void setID(int ID){
        this .ID=ID;
    }
    public ArrayList<Student>getStudents(){
        return students;
    }
    public void setStudents(ArrayList<Student>students){
        this.students=students;
    }
    public String getSubject(){
        return Subject;
    }
    public void setSubject(String Subject){
        this.Subject=Subject;
    }public String getDate(){
        return Date;
    }public void setDate(String Date){
        this.Date=Date;

    }

}
