package parentPackage;

import java.util.HashMap;
import java.util.Map;

public class Student {
    private final String name;
    private final Map<Subject, Integer> subjectsAndGrades;
    private Clazz clazz;

    public Student(String name) {
        this.name = name;
        this.subjectsAndGrades = new HashMap<>();
        this.clazz = null;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        stringBuilder.append(this.name).append(": ");
        for (Map.Entry<Subject, Integer> entry : subjectsAndGrades.entrySet()) {
            stringBuilder.append(entry.getKey()).append(" -> ").append(entry.getValue()).append(", ");
        }
        return stringBuilder.toString();
    }

    public void addSubjectAndGrade(Subject subject, Integer grade) throws Exception {
        if (this.subjectsAndGrades.containsKey(subject)) {
            throw new Exception("Student " + this.name + " already has grade in subject " + subject.getName() + " -> "
                    +this.subjectsAndGrades.get(subject) + ".");
        } else if (grade < 1 || grade > 5) {
            throw new Exception("Given grade is not between 1 and 5 (inclusive).");
        } else {
            this.subjectsAndGrades.put(subject, grade);
        }
    }

    public String getName() {
        return name;
    }

    public Map<Subject, Integer> getSubjectsAndGrades() {
        return this.subjectsAndGrades;
    }

    public Clazz getClazz() {
        return this.clazz;
    }

    public void setClazz(Clazz clazz) {
        this.clazz = clazz;
    }
}
