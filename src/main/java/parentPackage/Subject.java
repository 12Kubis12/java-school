package parentPackage;

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
