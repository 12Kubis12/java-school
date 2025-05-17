package parentPackage;

import java.util.HashMap;
import java.util.Map;

public class Student {
    private final String name;
    private final Map<Subject, Integer> subjectsAndGrades;

    public Student(String name) {
        this.name = name;
        this.subjectsAndGrades = new HashMap<>();
    }

    public void addSubjectsAndGrades(Subject subject, Integer grade) {
        this.subjectsAndGrades.put(subject,grade);
    }

    public String getName() {
        return name;
    }

    public Map<Subject, Integer> getSubjectsAndGrades() {
        return this.subjectsAndGrades;
    }
}
