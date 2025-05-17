package parentPackage;

public class Subject {
    private final String name;
    private Teacher teacher;

    public Subject(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Teacher getTeacher() {
        return this.teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }
}
