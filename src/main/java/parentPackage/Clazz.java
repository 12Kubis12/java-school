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
        this.primaryTeacher = null;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\nClass ").append(this.name).append(": \n").append("Primary teacher: ")
                .append(this.primaryTeacher);
        for (Student student : this.students) {
            stringBuilder.append(student);
        }
        return stringBuilder.toString();
    }

    public void addStudent(Student student) throws Exception {
        if (student.getClazz() == null) {
            student.setClazz(this);
            this.students.add(student);
        } else {
            throw new Exception("Student " + student.getName() + " is already member of a class: "
                    + student.getClazz().getName() + ".");
        }
    }

    public String getName() {
        return name;
    }

    public List<Student> getStudents() {
        return this.students;
    }

    public Teacher getPrimaryTeacher() {
        return this.primaryTeacher;
    }

    public void setPrimaryTeacher(Teacher primaryTeacher) throws Exception {
        if (primaryTeacher.getClazz() == null) {
            primaryTeacher.setClazz(this);
            this.primaryTeacher = primaryTeacher;
        } else {
            throw new Exception("Teacher " + primaryTeacher.getName() + " is already primary teacher for a class: "
                    + primaryTeacher.getClazz().getName() + ".");
        }
    }
}
