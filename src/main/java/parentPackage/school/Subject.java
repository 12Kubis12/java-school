package parentPackage.school;

import java.util.Objects;

public class Subject implements ComparableByName {
    private final String name;
    private final Teacher teacher;

    public Subject(String name, Teacher teacher) {
        this.name = name;
        this.teacher = teacher;
        if (this.teacher != null) {
            this.teacher.addSubject(this);
        }
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Subject subject = (Subject) o;
        return Objects.equals(this.name, subject.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }

    public String getName() {
        return this.name;
    }

    public Teacher getTeacher() {
        return this.teacher;
    }
}
