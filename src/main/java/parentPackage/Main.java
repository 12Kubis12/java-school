package parentPackage;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<Clazz> classes = createClasses();

        sortedStudentsByAverageGrades(classes);
        sortedSubjectsByAverageGrades(classes);
        sortedClassesByAverageGrades(classes);
    }

    public static void sortedStudentsByAverageGrades(List<Clazz> classes) {
        System.out.println("Sorted students by their average grades: ");

        classes.stream()
                .flatMap(clazz -> clazz.getStudents().stream())
                .collect(Collectors.toMap(Student::getName, student -> student.getSubjectsAndGrades().values().stream()
                        .mapToDouble(value -> value)
                        .average()
                        .orElse(0.0)))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(pair -> System.out.printf("%s -> %.2f%n", pair.getKey(), pair.getValue()));

        System.out.println();
    }

    public static void sortedSubjectsByAverageGrades(List<Clazz> classes) {
        System.out.println("Sorted subjects by average of grades given to students: ");

        classes.stream()
                .flatMap(clazz -> clazz.getStudents().stream()
                        .flatMap(student -> student.getSubjectsAndGrades().entrySet().stream()))
                .collect(Collectors.toMap(entry -> entry.getKey().getName(),
                        entry -> new ArrayList<>(Collections.singletonList(entry.getValue())),
                        (first, second) -> {
                            first.addAll(second);
                            return first;
                        })).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream()
                        .mapToDouble(value -> value)
                        .average()
                        .orElse(0.0)))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(pair -> System.out.printf("%s -> %.2f%n", pair.getKey(), pair.getValue()));

        System.out.println();
    }

    public static void sortedClassesByAverageGrades(List<Clazz> classes) {
        System.out.println("Sorted classes with the best students: ");

        classes.stream()
                .collect(Collectors.toMap(Clazz::getName, clazz -> clazz.getStudents().stream()
                        .mapToDouble(student -> student.getSubjectsAndGrades().values().stream()
                                .mapToDouble(value -> value)
                                .average()
                                .orElse(0.0))
                        .average()
                        .orElse(0.0)))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(pair -> System.out.printf("%s -> %.2f%n", pair.getKey(), pair.getValue()));

        System.out.println();
    }

    public static List<Clazz> createClasses() {
        Subject math = new Subject("Math");
        Subject biology = new Subject("Biology");
        Subject physics = new Subject("Physics");
        Subject geography = new Subject("Geography");
        Subject chemistry = new Subject("Chemistry");
        Subject physicalEducation = new Subject("Physical Education");
        Subject civicEducation = new Subject("Civic Education");
        Subject english = new Subject("English");
        Subject art = new Subject("Art");
        Subject music = new Subject("Music");

        Teacher teacher01 = new Teacher("Teacher 01");
        teacher01.addSubject(math);
        teacher01.addSubject(physics);

        Teacher teacher02 = new Teacher("Teacher 02");
        teacher02.addSubject(chemistry);
        teacher02.addSubject(biology);
        teacher02.addSubject(physicalEducation);

        Teacher teacher03 = new Teacher("Teacher 03");
        teacher03.addSubject(geography);
        teacher03.addSubject(civicEducation);

        Teacher teacher04 = new Teacher("Teacher 04");
        teacher04.addSubject(english);
        teacher04.addSubject(art);
        teacher04.addSubject(music);

        Student student01 = new Student("Student 01");
        student01.addSubjectsAndGrades(math, 4);
        student01.addSubjectsAndGrades(geography, 3);
        student01.addSubjectsAndGrades(civicEducation, 2);

        Student student02 = new Student("Student 02");
        student02.addSubjectsAndGrades(math, 1);
        student02.addSubjectsAndGrades(physics, 1);
        student02.addSubjectsAndGrades(physicalEducation, 3);

        Student student03 = new Student("Student 03");
        student03.addSubjectsAndGrades(chemistry, 2);
        student03.addSubjectsAndGrades(math, 1);
        student03.addSubjectsAndGrades(physics, 1);
        student03.addSubjectsAndGrades(biology, 2);

        Student student04 = new Student("Student 04");
        student04.addSubjectsAndGrades(chemistry, 1);
        student04.addSubjectsAndGrades(biology, 2);
        student04.addSubjectsAndGrades(physicalEducation, 2);
        student04.addSubjectsAndGrades(physics, 5);

        Student student05 = new Student("Student 05");
        student05.addSubjectsAndGrades(geography, 1);
        student05.addSubjectsAndGrades(math, 2);
        student05.addSubjectsAndGrades(civicEducation, 4);

        Student student06 = new Student("Student 06");
        student06.addSubjectsAndGrades(geography, 2);
        student06.addSubjectsAndGrades(art, 5);
        student06.addSubjectsAndGrades(music, 1);

        Student student07 = new Student("Student 07");
        student07.addSubjectsAndGrades(geography, 2);
        student07.addSubjectsAndGrades(physicalEducation, 1);
        student07.addSubjectsAndGrades(biology, 1);

        Student student08 = new Student("Student 08");
        student08.addSubjectsAndGrades(english, 1);
        student08.addSubjectsAndGrades(math, 4);
        student08.addSubjectsAndGrades(music, 1);
        student08.addSubjectsAndGrades(art, 1);

        Student student09 = new Student("Student 09");
        student09.addSubjectsAndGrades(english, 4);
        student09.addSubjectsAndGrades(art, 3);
        student09.addSubjectsAndGrades(chemistry, 1);
        student09.addSubjectsAndGrades(physics, 1);

        Student student10 = new Student("Student 10");
        student10.addSubjectsAndGrades(english, 1);
        student10.addSubjectsAndGrades(physicalEducation, 1);
        student10.addSubjectsAndGrades(geography, 2);
        student10.addSubjectsAndGrades(music, 2);

        Clazz clazz01 = new Clazz("1.A.");
        clazz01.setPrimaryTeacher(teacher01);
        clazz01.addStudent(student01);
        clazz01.addStudent(student02);

        Clazz clazz02 = new Clazz("1.B.");
        clazz02.setPrimaryTeacher(teacher02);
        clazz02.addStudent(student03);
        clazz02.addStudent(student04);

        Clazz clazz03 = new Clazz("2.A.");
        clazz03.setPrimaryTeacher(teacher03);
        clazz03.addStudent(student05);
        clazz03.addStudent(student06);
        clazz03.addStudent(student07);

        Clazz clazz04 = new Clazz("2.B.");
        clazz04.setPrimaryTeacher(teacher04);
        clazz04.addStudent(student08);
        clazz04.addStudent(student09);
        clazz04.addStudent(student10);


        return List.of(clazz01, clazz02, clazz03, clazz04);
    }
}
