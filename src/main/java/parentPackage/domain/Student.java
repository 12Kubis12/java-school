package parentPackage.domain;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Student implements ComparableByName {
    private final String name;
    private final Map<Subject, Integer> subjectsAndGrades;
    private final Clazz clazz;

    public Student(String name, Clazz clazz) {
        this.name = name;
        this.clazz = clazz;
        if (this.clazz != null) {
            this.clazz.addStudent(this);
        }
        this.subjectsAndGrades = new HashMap<>();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        stringBuilder.append(this.name).append(": ");
        this.subjectsAndGrades.entrySet().stream()
                .sorted(Comparator.comparing(subjectIntegerEntry -> subjectIntegerEntry.getKey().getName()))
                .forEach(entry ->
                stringBuilder.append(entry.getKey()).append(" -> ").append(entry.getValue()).append(", "));
        stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());

        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(this.name, student.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }

    public void addSubjectAndGrade(Subject subject, Integer grade) {
        this.subjectsAndGrades.put(subject, grade);
    }

    public String getName() {
        return this.name;
    }

    public Map<Subject, Integer> getSubjectsAndGrades() {
        return this.subjectsAndGrades;
    }

    public Clazz getClazz() {
        return this.clazz;
    }
}
