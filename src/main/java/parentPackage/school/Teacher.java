package parentPackage.school;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Teacher {
    private final String name;
    private final List<Subject> subjects;
    private Clazz clazz;

    public Teacher(String name) {
        this.name = name;
        this.subjects = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.name);
        if (!this.subjects.isEmpty()) {
            stringBuilder.append(" (Subjects: ");
        }
        for (int i = 0; i < this.subjects.size(); i++) {
            stringBuilder.append(subjects.get(i));
            if (i == this.subjects.size() - 1) {
                stringBuilder.append(")");
            } else {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Teacher teacher = (Teacher) o;
        return Objects.equals(name, teacher.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public void addSubject(Subject subject) {
        this.subjects.add(subject);
    }

    public String getName() {
        return name;
    }

    public List<Subject> getSubjects() {
        return this.subjects;
    }

    public Clazz getClazz() {
        return this.clazz;
    }

    public void setClazz(Clazz clazz) {
        this.clazz = clazz;
    }
}
