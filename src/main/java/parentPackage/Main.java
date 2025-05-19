package parentPackage;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<Clazz> classes = createClasses();

        if (!classes.isEmpty()) {
            printClasses(classes);
            sortedStudentsByAverageGrades(classes);
            sortedSubjectsByAverageGrades(classes);
            sortedClassesByAverageGrades(classes);
        }
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
        Map<InstanceType, List<Object>> all_instances = new HashMap<>();
        List<Clazz> classes = new ArrayList<>();
        boolean continueValue = true;

        File file = new File("src/all_instances.txt");
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (Exception e) {
            System.out.println("File not found!!!");
            continueValue = false;
        }

        if (continueValue) {
            while (scanner.hasNextLine() && continueValue) {
                continueValue = scanLine(scanner, all_instances);
            }

            if (continueValue) {
                for (Object clazz : all_instances.get(InstanceType.CLASS)) {
                    classes.add((Clazz) clazz);
                }
            }
        }

        return classes;
    }

    public static boolean scanLine(Scanner scanner, Map<InstanceType, List<Object>> all_instances) {
        boolean stopValue = true;
        String line = scanner.nextLine();
        try {
            InstanceType instanceType = InstanceType.getFromString(line);
            createInstances(all_instances, instanceType, scanner);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Check your .txt file!!!");
            stopValue = false;
        }
        return stopValue;
    }

    public static void createInstances(Map<InstanceType, List<Object>> all_instances, InstanceType instanceType, Scanner scanner) throws Exception {
        all_instances.put(instanceType, new ArrayList<>());
        String[] oneTypeInstances = scanner.nextLine().split(", ");

        for (String instance : oneTypeInstances) {
            switch (instanceType) {
                case CLASS -> createClass(all_instances, instance);
                case TEACHER -> createTeacher(all_instances, instance);
                case STUDENT -> createStudent(all_instances, instance);
                case SUBJECT -> createSubject(all_instances, instance);
            }
        }
    }

    public static void createClass(Map<InstanceType, List<Object>> all_instances, String instance) throws Exception {
        String[] instancesArray = instance.split("-");
        String teacherString = instancesArray[1].split(":")[0];
        String[] students = instancesArray[1].split(":")[1].split("; ");
        Clazz clazz = new Clazz(instancesArray[0]);

        for (Object object : all_instances.get(InstanceType.TEACHER)) {
            Teacher teacher = ((Teacher) object);
            if (teacher.getName().equals(teacherString)) {
                clazz.setPrimaryTeacher(teacher);
                break;
            }
        }

        for (String studentString : students) {
            for (Object object : all_instances.get(InstanceType.STUDENT)) {
                Student student = ((Student) object);
                if (student.getName().equals(studentString)) {
                    clazz.addStudent(student);
                    break;
                }
            }
        }

        all_instances.get(InstanceType.CLASS).add(clazz);
    }

    public static void createTeacher(Map<InstanceType, List<Object>> all_instances, String instance) throws Exception {
        String[] instancesArray = instance.split("-");
        String[] subjects = instancesArray[1].split("; ");
        Teacher teacher = new Teacher(instancesArray[0]);

        for (String subjectString : subjects) {
            for (Object object : all_instances.get(InstanceType.SUBJECT)) {
                Subject subject = ((Subject) object);
                if (subject.getName().equals(subjectString)) {
                    teacher.addSubject(subject);
                    break;
                }
            }
        }

        all_instances.get(InstanceType.TEACHER).add(teacher);
    }

    public static void createStudent(Map<InstanceType, List<Object>> all_instances, String instance) throws Exception {
        String[] instancesArray = instance.split("-");
        String[] subjectsAndGrades = instancesArray[1].split("; ");
        Student student = new Student(instancesArray[0]);

        for (String s : subjectsAndGrades) {
            String subjectString = s.split(":")[0];
            int grade = Integer.parseInt(s.split(":")[1]);
            for (Object object : all_instances.get(InstanceType.SUBJECT)) {
                Subject subject = ((Subject) object);
                if (subject.getName().equals(subjectString)) {
                    student.addSubjectAndGrade(subject, grade);
                    break;
                }
            }
        }

        all_instances.get(InstanceType.STUDENT).add(student);
    }

    public static void createSubject(Map<InstanceType, List<Object>> all_instances, String instance) {
        all_instances.get(InstanceType.SUBJECT).add(new Subject(instance));
    }

    public static void printClasses(List<Clazz> classes) {
        System.out.println("-".repeat(50) + " REPORT CLASSES " + "-".repeat(50));
        for (Clazz clazz : classes) {
            System.out.println(clazz);
        }
        System.out.println();
        System.out.println("-".repeat(116));
    }
}
