package com.example.smartattendancesystem;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class Database {
    private String url = "jdbc:mysql://localhost/smartattendancesystem";
    private String user = "user";
    private String pass = "#1#2#3%1%2%3";
    private Statement statement;

    public Database() {
        try {
            Connection connection = DriverManager.getConnection(url, user, pass);
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (Exception e) {
            Log.d("Database", "Failed to connect to the database.");
            e.printStackTrace();
        }
    }

    public ArrayList<Class> getAllClasses() {
        ArrayList<Class> classes = new ArrayList<>();
        try {
            String select = "SELECT * FROM classes;";
            ResultSet rs = statement.executeQuery(select);
            while (rs.next()) {
                Class c = new Class(rs.getInt("ID"), rs.getString("ClassName"));
                classes.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    public int getNextClassID() {
        int id = 0;
        ArrayList<Class> classes = getAllClasses();
        int size = classes.size();
        if (size != 0) {
            Class lastClass = classes.get(size - 1);
            id = lastClass.getID() + 1;
        }
        return id;
    }

    public void addClass(Class c) {
        try {
            String insert = "INSERT INTO classes (ID, ClassName) VALUES (" + c.getID() + ", '" + c.getClassName() + "');";
            statement.execute(insert);

            String createSessionTable = "CREATE TABLE IF NOT EXISTS `" + c.getID() + "-Session` (ID INT, Subject TEXT, Date TEXT);";
            statement.execute(createSessionTable);

            String createStudentTable = "CREATE TABLE IF NOT EXISTS `" + c.getID() + "-Students` (ID INT, FirstName TEXT, LastName TEXT, Email TEXT, Tel TEXT);";
            statement.execute(createStudentTable);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteClass(int id) {
        try {
            String delete = "DELETE FROM classes WHERE ID = " + id + ";";
            statement.execute(delete);

            for (Session s : getSessions(id)) {
                deleteSession(id, s.getID());
            }

            String dropSessionTable = "DROP TABLE IF EXISTS `" + id + "-Session`;";
            String dropStudentTable = "DROP TABLE IF EXISTS `" + id + "-Students`;";
            statement.execute(dropSessionTable);
            statement.execute(dropStudentTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Student> getStudents(int id) {
        ArrayList<Student> students = new ArrayList<>();
        try {
            String select = "SELECT * FROM `" + id + "-Students`;";
            ResultSet rs = statement.executeQuery(select);
            while (rs.next()) {
                Student s = new Student();
                s.setID(rs.getInt("ID"));
                s.setFirstName(rs.getString("FirstName"));
                s.setLastName(rs.getString("LastName"));
                s.setEmail(rs.getString("Email"));
                s.setTel(rs.getString("Tel"));
                students.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return students;
    }

    public int getNextStudentID(int classID) {
        int id = 0;
        ArrayList<Student> students = getStudents(classID);
        int size = students.size();
        if (size != 0) {
            Student lastStudent = students.get(size - 1);
            id = lastStudent.getID() + 1;
        }
        return id;
    }

    public Student getStudent(int classID, int studentID) {
        Student student = new Student();
        try {
            String select = "SELECT ID, FirstName, LastName, Email, Tel FROM `" + classID + "-Students` WHERE ID = " + studentID + ";";
            ResultSet rs = statement.executeQuery(select);
            if (rs.next()) {
                student.setID(rs.getInt("ID"));
                student.setFirstName(rs.getString("FirstName"));
                student.setLastName(rs.getString("LastName"));
                student.setEmail(rs.getString("Email"));
                student.setTel(rs.getString("Tel"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return student;
    }

    public void addStudent(int classID, Student s) {
        try {
            String insert = "INSERT INTO `" + classID + "-Students` (ID, FirstName, LastName, Email, Tel) VALUES (" +
                    s.getID() + ", '" + s.getFirstName() + "', '" + s.getLastName() + "', '" + s.getEmail() + "', '" + s.getTel() + "');";
            statement.execute(insert);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateStudent(int classID, Student s) {
        try {
            String update = "UPDATE `" + classID + "-Students` SET FirstName = '" + s.getFirstName() + "', LastName = '" +
                    s.getLastName() + "', Email = '" + s.getEmail() + "', Tel = '" + s.getTel() + "' WHERE ID = " + s.getID() + ";";
            statement.execute(update);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteStudent(int classID, int studentID) {
        try {
            String delete = "DELETE FROM `" + classID + "-Students` WHERE ID = " + studentID + ";";
            statement.execute(delete);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Session> getSessions(int classID) {
        ArrayList<Session> sessions = new ArrayList<>();
        try {
            String select = "SELECT * FROM `" + classID + "-Session`;";
            ResultSet rs = statement.executeQuery(select);
            while (rs.next()) {
                Session s = new Session();
                s.setID(rs.getInt("ID"));
                s.setSubject(rs.getString("Subject"));
                s.setDate(rs.getString("Date"));
                sessions.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sessions;
    }

    public int getNextSessionID(int classID) {
        int id = 0;
        ArrayList<Session> sessions = getSessions(classID);
        int size = sessions.size();
        if (size != 0) {
            Session lastSession = sessions.get(size - 1);
            id = lastSession.getID() + 1;
        }
        return id;
    }

    public void addSession(int classID, Session s) {
        try {
            String insert = "INSERT INTO `" + classID + "-Session` (ID, Subject, Date) VALUES (" + s.getID() + ", '" + s.getSubject() + "', '" + s.getDate() + "');";
            statement.execute(insert);

            String createAttendanceTable = "CREATE TABLE IF NOT EXISTS `" + classID + "-" + s.getID() + "` (ID INT);";
            statement.execute(createAttendanceTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Session getSession(int classID, int sessionID) {
        Session s = new Session();
        try {
            String select = "SELECT ID, Subject, Date FROM `" + classID + "-Session` WHERE ID = " + sessionID + ";";
            ResultSet rs = statement.executeQuery(select);
            if (rs.next()) {
                s.setID(rs.getInt("ID"));
                s.setSubject(rs.getString("Subject"));
                s.setDate(rs.getString("Date"));

                String selectAttendance = "SELECT * FROM `" + classID + "-" + sessionID + "`;";
                ResultSet rsAttendance = statement.executeQuery(selectAttendance);
                ArrayList<Integer> ids = new ArrayList<>();
                while (rsAttendance.next()) {
                    ids.add(rsAttendance.getInt("ID"));
                }
                ArrayList<Student> students = new ArrayList<>();
                for (int id : ids) {
                    students.add(getStudent(classID, id));
                }
                s.setStudents(students);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public void updateSessionDate(int classID, Session s) {
        try {
            String update = "UPDATE `" + classID + "-Session` SET Subject = '" + s.getSubject() + "', Date = '" + s.getDate() + "' WHERE ID = " + s.getID() + ";";
            statement.execute(update);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteSession(int classID, int sessionID) {
        try {
            String delete = "DELETE FROM `" + classID + "-Session` WHERE ID = " + sessionID + ";";
            statement.execute(delete);

            String drop = "DROP TABLE IF EXISTS `" + classID + "-" + sessionID + "`;";
            statement.execute(drop);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addStudentsToSession(int classID, int sessionID, ArrayList<Integer> students) {
        try {
            for (Integer id : students) {
                String insert = "INSERT INTO `" + classID + "-" + sessionID + "` (ID) VALUES (" + id + ");";
                statement.execute(insert);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeStudentsFromSession(int classID, int sessionID, ArrayList<Integer> students) {
        try {
            for (Integer id : students) {
                String delete = "DELETE FROM `" + classID + "-" + sessionID + "` WHERE ID = " + id + ";";
                statement.execute(delete);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

