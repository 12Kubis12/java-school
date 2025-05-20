package parentPackage;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        String inputWay = chooseInputWay();
        List<Clazz> classes = createClasses(inputWay);

        if (!classes.isEmpty()) {
            printClasses(classes);
            sortedStudentsByAverageGrades(classes);
            sortedSubjectsByAverageGrades(classes);
            sortedClassesByAverageGrades(classes);
        }
    }

    public static String chooseInputWay() {
        Scanner scanner = new Scanner(System.in);
        String inputWay;
        while (true) {
            System.out.println("Choose the way of input.\nYou can create every instance by typing ('T') " +
                    "or load them from a file ('F'):");
            inputWay = scanner.nextLine().replaceAll("\\s", "").toUpperCase();
            if (inputWay.equals("F") || inputWay.equals("T")) {
                break;
            } else {
                System.out.println("Invalid input. Try again!!!");
            }
        }
        return inputWay;
    }

    public static List<Clazz> createClasses(String inputType) {
        Map<InstanceType, List<Object>> all_instances = new HashMap<>();
        List<Clazz> classes = new ArrayList<>();
        boolean continueVariable = true;

        if (inputType.equals("F")) {
            File file = new File("src/all_instances.txt");
            Scanner scanner = null;
            try {
                scanner = new Scanner(file);
            } catch (Exception e) {
                System.out.println("File not found!!!");
                continueVariable = false;
            }

            if (continueVariable) {
                while (scanner.hasNextLine() && continueVariable) {
                    continueVariable = scanLine(scanner, all_instances);
                }
            }
        } else if (inputType.equals("T")) {
            Scanner scanner = new Scanner(System.in);

            int count = 1;
            while (count <= 4) {
                switch (count) {
                    case 1 ->
                            System.out.println("Write word \"SUBJECT\", press enter and then write all the subjects " +
                                    "separated by \", \" (as shown in the example below).\n" +
                                    "For example -> \"Math, Biology, Physics\".");
                    case 2 ->
                            System.out.println("Write word \"TEACHER\", press enter and then write all the teachers " +
                                    "separated by \", \" (with subjects they teach - as shown in the example below).\n" +
                                    "For example -> \"Teacher 01-Math; Physics, Teacher 02-Chemistry; Biology; Physical Education\".");
                    case 3 ->
                            System.out.println("Write word \"STUDENT\", press enter and then write all the students " +
                                    "separated by \", \" (with subjects they study and grade for each subject - as shown in the example below).\n" +
                                    "For example -> \"Student 01-Math:4; Geography:3; Civil Education:2, Student 02-Math:1; Physics:1; Physical Education:3\".");
                    case 4 ->
                            System.out.println("Write word \"CLASS\", press enter and then write all the classes " +
                            "separated by \", \" (with primary teacher and students they have - as shown in the example below).\n" +
                            "For example -> \"1.A-Teacher 01:Student 01; Student 02; Student 03, 1.B-Teacher 02:Student 04; Student 05; Student 06\".");
                }
                System.out.println("Use the same entities separators \"-\", \":\", \"; \".\nBe careful what you write " +
                        "otherwise you can create entities with strange names or start from the beginning if you use " +
                        "something that does not exists!");

                boolean resetValue = scanLine(scanner, all_instances);
                if (resetValue) {
                    count++;
                } else {
                    count = 1;
                }
            }
        }

        if (continueVariable) {
            for (Object clazz : all_instances.get(InstanceType.CLASS)) {
                classes.add((Clazz) clazz);
            }
        }

        return classes;
    }

    public static boolean scanLine(Scanner scanner, Map<InstanceType, List<Object>> all_instances) {
        boolean continueVariable = true;
        String line = scanner.nextLine().replaceAll("\\s", "").toUpperCase();
        try {
            InstanceType instanceType = InstanceType.getFromString(line);
            createInstances(all_instances, instanceType, scanner);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Check your .txt file or write correct input!!!");
            continueVariable = false;
        }
        return continueVariable;
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

        List<Object> teachersList = all_instances.get(InstanceType.TEACHER);
        for (int i = 0; i < teachersList.size(); i++) {
            Teacher teacher = (Teacher) teachersList.get(i);
            if (teacher.getName().equals(teacherString)) {
                clazz.setPrimaryTeacher(teacher);
                break;
            } else if (i == teachersList.size() - 1) {
                throw new Exception("Teacher " + teacherString + " does not exist!!!");
            }
        }

        for (String studentString : students) {
            List<Object> studentsList = all_instances.get(InstanceType.STUDENT);
            for (int i = 0; i < studentsList.size(); i++) {
                Student student = (Student) studentsList.get(i);
                if (student.getName().equals(studentString)) {
                    clazz.addStudent(student);
                    break;
                } else if (i == studentsList.size() - 1) {
                    throw new Exception("Student " + studentString + " does not exist!!!");
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
            List<Object> subjectsList = all_instances.get(InstanceType.SUBJECT);
            for (int i = 0; i < subjectsList.size(); i++) {
                Subject subject = (Subject) subjectsList.get(i);
                if (subject.getName().equals(subjectString)) {
                    teacher.addSubject(subject);
                    break;
                } else if (i == subjectsList.size() - 1) {
                    throw new Exception("Subject " + subjectString + " does not exist!!!");
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
            List<Object> subjectsList = all_instances.get(InstanceType.SUBJECT);
            for (int i = 0; i < subjectsList.size(); i++) {
                Subject subject = (Subject) subjectsList.get(i);
                if (subject.getName().equals(subjectString)) {
                    student.addSubjectAndGrade(subject, grade);
                    break;
                } else if (i == subjectsList.size() - 1) {
                    throw new Exception("Subject " + subjectString + " does not exist!!!");
                }
            }
        }

        all_instances.get(InstanceType.STUDENT).add(student);
    }

    public static void createSubject(Map<InstanceType, List<Object>> all_instances, String instance) {
        Subject subject = new Subject(instance);
        if (!all_instances.get(InstanceType.SUBJECT).contains(subject)) {
            all_instances.get(InstanceType.SUBJECT).add(subject);
        }
    }

    public static void printClasses(List<Clazz> classes) {
        System.out.println("-".repeat(50) + " REPORT CLASSES " + "-".repeat(50));
        for (Clazz clazz : classes) {
            System.out.println(clazz);
        }
        System.out.println();
        System.out.println("-".repeat(116));
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
}
