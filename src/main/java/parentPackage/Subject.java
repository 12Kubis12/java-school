package parentPackage;

import java.util.Objects;

public class Subject {
    private final String name;
    private Teacher teacher;

    public Subject(String name) {
        this.name = name;
        this.teacher = null;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Subject subject = (Subject) o;
        return Objects.equals(name, subject.name) && Objects.equals(teacher, subject.teacher);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, teacher);
    }

    public String getName() {
        return name;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }
}
