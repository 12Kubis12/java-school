package parentPackage;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        List<Clazz> classes = createClasses();

        studentsByAverageGrades(classes);


    }

    public static void studentsByAverageGrades(List<Clazz> classes) {
        classes.stream()
                .flatMap(clazz -> clazz.getStudents().stream())
                .forEach(student -> System.out.println(student.getName() + " - "
                        + student.getSubjectsAndGrades().values().stream()
                        .mapToDouble(value -> value)
                        .average()
                        .orElse(0.0)));
    }

    public static List<Clazz> createClasses() {
        Subject math = new Subject("Math");
        Subject biology = new Subject("Biology");
        Subject physics = new Subject("Physics");
        Subject geography = new Subject("Geography");
        Subject chemistry = new Subject("Chemistry");
        Subject physicalEducation = new Subject("Physical Education");
        Subject civicEducation = new Subject("Civic Education");

        Teacher teacher01 = new Teacher("Teacher 01");
        teacher01.addSubject(math);
        teacher01.addSubject(physics);

        Teacher teacher02 = new Teacher("Teacher 02");
        teacher02.addSubject(biology);
        teacher02.addSubject(chemistry);
        teacher02.addSubject(physicalEducation);

        Teacher teacher03 = new Teacher("Teacher 03");
        teacher03.addSubject(geography);
        teacher03.addSubject(civicEducation);

        Student student01 = new Student("Student 01");
        student01.addSubjectsAndGrades(geography, 3);
        student01.addSubjectsAndGrades(civicEducation, 2);
        student01.addSubjectsAndGrades(physicalEducation, 4);


        Student student02 = new Student("Student 02");
        student02.addSubjectsAndGrades(math, 1);
        student02.addSubjectsAndGrades(physics, 1);
        student02.addSubjectsAndGrades(physicalEducation, 3);

        Student student03 = new Student("Student 03");
        student03.addSubjectsAndGrades(math, 1);
        student03.addSubjectsAndGrades(physics, 1);
        student03.addSubjectsAndGrades(chemistry, 2);
        student03.addSubjectsAndGrades(biology, 2);

        Student student04 = new Student("Student 04");
        student04.addSubjectsAndGrades(chemistry, 1);
        student04.addSubjectsAndGrades(biology, 2);
        student04.addSubjectsAndGrades(physicalEducation, 2);
        student04.addSubjectsAndGrades(physics, 5);

        Clazz clazz01 = new Clazz("1.A.");
        clazz01.setPrimaryTeacher(teacher01);
        clazz01.addStudent(student01);
        clazz01.addStudent(student02);

        Clazz clazz02 = new Clazz("1.B.");
        clazz02.setPrimaryTeacher(teacher02);
        clazz02.addStudent(student03);
        clazz02.addStudent(student04);

        return List.of(clazz01, clazz02);
    }
}
