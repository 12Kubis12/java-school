package parentPackage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Clazz {
    private final String name;
    private final Teacher primaryTeacher;
    private final List<Student> students;

    public Clazz(String name, Teacher primaryTeacher) {
        this.name = name;
        this.students = new ArrayList<>();
        this.primaryTeacher = primaryTeacher;
        if (this.primaryTeacher != null) {
            this.primaryTeacher.setClazz(this);
        }
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Clazz clazz = (Clazz) o;
        return Objects.equals(name, clazz.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public void addStudent(Student student) {
        this.students.add(student);
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
}
