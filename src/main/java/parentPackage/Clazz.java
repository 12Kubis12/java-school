package parentPackage;

import java.util.ArrayList;
import java.util.List;

public class Clazz {
    private final String name;
    private Teacher primaryTeacher;
    private final List<Student> students;

    public Clazz(String name) {
        this.name = name;
        this.students = new ArrayList<>();
    }

    public void addStudent(Student student) {
        this.students.add(student);
    }

    public String getName() {
        return name;
    }

    public Teacher getPrimaryTeacher() {
        return this.primaryTeacher;
    }

    public void setPrimaryTeacher(Teacher primaryTeacher) {
        this.primaryTeacher = primaryTeacher;
    }

    public List<Student> getStudents() {
        return this.students;
    }
}
