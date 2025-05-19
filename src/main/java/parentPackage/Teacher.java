package parentPackage;

import java.util.ArrayList;
import java.util.List;

public class Teacher {
    private final String name;
    private final List<Subject> subjects;
    private Clazz clazz;

    public Teacher(String name) {
        this.name = name;
        this.subjects = new ArrayList<>();
        this.clazz = null;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.name).append(" (Subjects: ");
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

    public void addSubject(Subject subject) throws Exception {
        if (subject.getTeacher() == null) {
            subject.setTeacher(this);
            this.subjects.add(subject);
        } else {
            throw new Exception("Subject " + subject.getName() + " already has a teacher: "
                    + subject.getTeacher().getName() + ".");
        }
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
