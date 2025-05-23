package parentPackage.school;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class School {
    private Scanner scanner;
    private final Map<InstanceType, List<ComparableByName>> all_instances;
    private String inputWay;
    private final List<Clazz> classes;
    private boolean continueVariable;
    private final int subjectsMinAmount;
    private final int classesMinAmount;
    private final int subjectsPerStudentMinAmount;
    private final int studentsPerClassMinAmount;

    public School() {
        this.all_instances = new HashMap<>();
        this.classes = new ArrayList<>();
        this.continueVariable = true;
        this.subjectsMinAmount = 3;
        this.classesMinAmount = 2;
        this.subjectsPerStudentMinAmount = 3;
        this.studentsPerClassMinAmount = 3;
        this.chooseInputWay();
        this.createEntities();
    }

    private void chooseInputWay() {
        this.scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Choose the way of input.\nYou can create every instance by typing ('T') " +
                    "or load them from a file ('F'):");
            this.inputWay = this.scanner.nextLine().replaceAll("\\s", "").toUpperCase();
            if (this.inputWay.equals("F") || this.inputWay.equals("T")) {
                break;
            } else {
                System.out.println("Invalid input. Try again!!!");
            }
        }
    }

    private void createEntities() {
        if (this.inputWay.equals("F")) {
            File file = new File("src/all_instances.txt");
            try {
                this.scanner = new Scanner(file);
            } catch (Exception e) {
                System.out.println("File not found!!!");
                this.continueVariable = false;
            }
// continueVariable is false only when an exception is thrown
            while (this.continueVariable && this.scanner.hasNextLine()) {
                String line = this.scanner.nextLine().replaceAll("\\s", "").toUpperCase();
                this.createEntityType(line);
            }
        } else if (this.inputWay.equals("T")) {
            System.out.println("Be careful what you write otherwise you can create entities with strange names or" +
                    " start from the beginning if you use something wrong way!");
            int count = 1;
            while (count <= 4) {
                switch (count) {
                    case 1 -> this.createEntityType(InstanceType.TEACHER.toString());
                    case 2 -> this.createEntityType(InstanceType.SUBJECT.toString());
                    case 3 -> this.createEntityType(InstanceType.CLASS.toString());
                    case 4 -> this.createEntityType(InstanceType.STUDENT.toString());
                }
// if there is thrown an exception (in case of creating entities from console), the process start from the beginning.
                if (this.continueVariable) {
                    count++;
                } else {
                    count = 1;
                }
            }
        }
// delete all teachers who do not teach any subject and are not primary teacher for any class
        if (this.continueVariable) {
            this.all_instances.get(InstanceType.TEACHER).removeAll(this.all_instances.get(InstanceType.TEACHER).stream()
                    .map(teacher -> (Teacher) teacher)
                    .filter(teacher -> teacher.getClazz() == null && teacher.getSubjects().isEmpty())
                    .toList());

            this.classes.addAll(this.all_instances.get(InstanceType.CLASS).stream()
                    .map(clazz -> (Clazz) clazz)
                    .toList());
        }
    }

// here are caught most of the exceptions
    private void createEntityType(String type) {
        try {
            InstanceType instanceType = InstanceType.getFromString(type);
            this.createInstances(instanceType);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Check your .txt file or write correct input!!!");
            this.continueVariable = false;
        }
    }

    private void createInstances(InstanceType instanceType) throws Exception {
        this.all_instances.put(instanceType, new ArrayList<>());
        String[] instancesLine;
        if (this.inputWay.equals("F")) {
            instancesLine = this.scanner.nextLine().split(", ");
        } else {
            instancesLine = this.scanInstances(instanceType);
        }
// if everything except students is created, check the conditions for amount of entities
        if (instanceType == InstanceType.STUDENT) {
            this.checkCreatedEntities();
        }

        for (String instance : instancesLine) {
            switch (instanceType) {
                case TEACHER -> this.createTeacher(instance);
                case SUBJECT -> this.createSubject(instance);
                case CLASS -> this.createClass(instance);
                case STUDENT -> this.createStudent(instance);
            }
        }
// if everything including students is created, check the condition for amount of students in one class
        if (instanceType == InstanceType.STUDENT) {
            List<Clazz> classesWithNotEnoughStudents = this.all_instances.get(InstanceType.CLASS).stream()
                    .map(clazz -> (Clazz) clazz)
                    .filter(clazz -> clazz.getStudents().size() < this.studentsPerClassMinAmount)
                    .sorted(Comparator.comparing(Clazz::getName))
                    .toList();
            if (!classesWithNotEnoughStudents.isEmpty()) {
                throw new Exception("These classes must have at least " + this.studentsPerClassMinAmount
                        + " students! -> " + classesWithNotEnoughStudents);
            }
        }
    }

// this function is used in case one creating every instance from console
    private String[] scanInstances(InstanceType instanceType) {
        Set<String> instances = new HashSet<>();
        Set<String> usedNames = new HashSet<>();
        Set<String> usedTeachers = new HashSet<>();
        String instancesSting = "";
        Map<String, Integer> unfilledCLasses = new HashMap<>();
        int minAmount = 0;
        long maxAmount = 0;
// set the conditions for amount of entities
        if (instanceType == InstanceType.CLASS || instanceType == InstanceType.TEACHER) {
            minAmount = this.classesMinAmount;
            if (instanceType == InstanceType.CLASS) {
                maxAmount = this.all_instances.get(InstanceType.TEACHER).size();
            }
        } else if (instanceType == InstanceType.STUDENT) {
            unfilledCLasses = this.all_instances.get(InstanceType.CLASS).stream()
                    .map(clazz -> (Clazz) clazz)
                    .collect(Collectors.toMap(Clazz::getName, clazz -> 0));
        } else if (instanceType == InstanceType.SUBJECT) {
            minAmount = this.subjectsMinAmount;
        }

        while (!instancesSting.equalsIgnoreCase("s") || instances.size() < minAmount
                || !unfilledCLasses.isEmpty()) {
// check the condition for amount of students in one class
            if (instanceType == InstanceType.STUDENT) {
                unfilledCLasses = unfilledCLasses.entrySet().stream()
                        .filter(entry -> entry.getValue() < this.studentsPerClassMinAmount)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                if (!unfilledCLasses.isEmpty()) {
                    System.out.println("Fill these classes with at least " + this.studentsPerClassMinAmount + " students: ");
                    unfilledCLasses.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .forEach(entry -> System.out.println(entry.getKey()
                            + " -> " + (this.studentsPerClassMinAmount - entry.getValue()) + " more"));
                }
            }

            switch (instanceType) {
                case TEACHER -> instancesSting = this.scanTeacher(instanceType, usedNames);
                case SUBJECT -> instancesSting = this.scanSubject(instanceType, usedNames);
                case CLASS -> instancesSting = this.scanClass(instanceType, usedNames, usedTeachers);
                case STUDENT -> instancesSting = this.scanStudent(instanceType, usedNames, unfilledCLasses);
            }

            if (!instancesSting.equalsIgnoreCase("s") && !instancesSting.isEmpty()) {
                instances.add(instancesSting);
                if (instances.size() == maxAmount) {
                    break;
                }
            } else if (instancesSting.equalsIgnoreCase("s") && instances.size() < minAmount) {
                System.out.println("Write more entities of this type, you must create "
                        + (minAmount - instances.size()) + " more!");
            }
        }

        return instances.toArray(String[]::new);
    }

    private String scanTeacher(InstanceType instanceType, Set<String> usedNames) {
        return this.initialStep(instanceType, usedNames);
    }

    private String scanSubject(InstanceType instanceType, Set<String> usedNames) {
        StringBuilder stringBuilder = new StringBuilder();
        String subject = this.initialStep(instanceType, usedNames);
        stringBuilder.append(subject);

        if (!subject.equalsIgnoreCase("s") && !subject.isEmpty()) {
            System.out.println("Write the teacher who teach the subject:");
            List<String> options = new ArrayList<>(this.all_instances.get(InstanceType.TEACHER).stream()
                    .map(teacher -> (Teacher) teacher)
                    .map(Teacher::getName)
                    .sorted()
                    .toList());
            stringBuilder.append("-").append(this.chooseFromOptions(options, false));
        }

        return stringBuilder.toString();
    }

    private String scanClass(InstanceType instanceType, Set<String> usedNames, Set<String> usedTeachers) {
        StringBuilder stringBuilder = new StringBuilder();
        String clazz = this.initialStep(instanceType, usedNames);
        stringBuilder.append(clazz);

        if (!clazz.equalsIgnoreCase("s") && !clazz.isEmpty()) {
            System.out.println("Write the teacher who is the primary teacher:");
            List<String> options = new ArrayList<>(this.all_instances.get(InstanceType.TEACHER).stream()
                    .map(teacher -> (Teacher) teacher)
                    .map(Teacher::getName)
                    .filter(name -> !usedTeachers.contains(name))
                    .sorted()
                    .toList());
            String teacher = this.chooseFromOptions(options, false);
            usedTeachers.add(teacher);
            stringBuilder.append("-").append(teacher);
        }

        return stringBuilder.toString();
    }

    private String scanStudent(InstanceType instanceType, Set<String> usedNames, Map<String, Integer> unfilledCLasses) {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> subjectOptions = new ArrayList<>(this.all_instances.get(InstanceType.SUBJECT).stream()
                .map(subject -> (Subject) subject)
                .map(Subject::getName)
                .sorted()
                .toList());
        int minSubjectsAmount = this.subjectsPerStudentMinAmount;
        String student = this.initialStep(instanceType, usedNames);
        stringBuilder.append(student);

        if (!student.equalsIgnoreCase("s") && !student.isEmpty()) {
            System.out.println("Write a class the student is a part of:");
            List<String> options = new ArrayList<>(this.all_instances.get(InstanceType.CLASS).stream()
                    .map(clazz -> (Clazz) clazz)
                    .map(Clazz::getName)
                    .sorted()
                    .toList());
            String clazz = this.chooseFromOptions(options, false);

            if (unfilledCLasses.containsKey(clazz)) {
                unfilledCLasses.put(clazz, unfilledCLasses.get(clazz) + 1);
            }

            stringBuilder.append("-").append(clazz).append("-");
            System.out.println("Now write subjects the student study and for each subject write grade from 1 to 5 inclusive.\n" +
                    "If you want to stop write \"s\".");

            while (!subjectOptions.isEmpty()) {
                System.out.println("Write the subject:");
                String subject = this.chooseFromOptions(subjectOptions, true);

                if (!subject.equalsIgnoreCase("s")) {
                    stringBuilder.append(subject).append(":");
                    minSubjectsAmount--;
                    System.out.println("Write the grade:");
                    List<String> gradeOptions = new ArrayList<>(Arrays.stream(GradeType.values())
                            .map(gradeType -> Integer.toString(gradeType.getNumber()))
                            .sorted()
                            .toList());
                    stringBuilder.append(this.chooseFromOptions(gradeOptions, false)).append("; ");
                } else if (minSubjectsAmount > 0) {
                    System.out.println("Give the student " + minSubjectsAmount + " more subjects!");
                } else {
                    break;
                }
            }

            stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
        }

        return stringBuilder.toString();
    }

    private String initialStep(InstanceType instanceType, Set<String> usedNames) {
        System.out.println("Write name of the " + instanceType + ":");
        System.out.println("(If you want to stop write \"s\").");
        return this.checkUsedNames(usedNames, this.scanner.nextLine());
    }

    private String checkUsedNames(Set<String> usedNames, String name) {
        if (usedNames.contains(name)) {
            System.out.println("The name is already used!");
            return "";
        } else if (!name.equalsIgnoreCase("s")) {
            usedNames.add(name);
        }
        return name;
    }

// every time an instance, which needs different existing instance, is being created, this function is called to provide the options
// this process can be stopped only when creating subjects and grades
    private String chooseFromOptions(List<String> options, boolean possibleStop) {
        String index;
        String chosenOption;
        System.out.print("Choose from (white corresponding number): ");
        options.forEach(s -> System.out.print("\"" + s + "\" -> " + (options.indexOf(s) + 1) + ", "));
        System.out.println();

        while (true) {
            try {
                index = this.scanner.nextLine();
                if (index.equalsIgnoreCase("s") && possibleStop) {
                    chosenOption = index;
                    break;
                }
                chosenOption = options.remove(Integer.parseInt(index) - 1);
                break;
            } catch (Exception e) {
                System.out.println("Invalid input try again!!!");
            }
        }

        return chosenOption;
    }

    private void createTeacher(String instanceLine) throws Exception {
        Teacher teacher = new Teacher(instanceLine);
        this.checkExistingEntities(InstanceType.TEACHER, teacher);
    }

    private void createSubject(String instanceLine) throws Exception {
        String[] instancesArray = instanceLine.split("-");

        Subject subject = new Subject(instancesArray[0], this.all_instances.get(InstanceType.TEACHER).stream()
                .map(teacher -> (Teacher) teacher)
                .filter(teacher -> teacher.getName().equals(instancesArray[1]))
                .findAny()
                .orElseThrow(() -> new Exception("Subject " + instancesArray[0] +
                        " has no teacher or the given teacher (" + instancesArray[1] + ") does not exist.")));

        this.checkExistingEntities(InstanceType.SUBJECT, subject);
    }

    private void createClass(String instanceLine) throws Exception {
        String[] instancesArray = instanceLine.split("-");

        Clazz clazz = new Clazz(instancesArray[0], this.all_instances.get(InstanceType.TEACHER).stream()
                .map(teacher -> (Teacher) teacher)
                .filter(teacher -> teacher.getClazz() == null)
                .filter(teacher -> teacher.getName().equals(instancesArray[1]))
                .findAny()
                .orElseThrow(() -> new Exception("Class " + instancesArray[0] +
                        " has no primary teacher or the given teacher (" + instancesArray[1] + ") does not exist" +
                        " or is primary teacher for different class.")));

        this.checkExistingEntities(InstanceType.CLASS, clazz);
    }

    private void createStudent(String instanceLine) throws Exception {
        String[] instancesArray = instanceLine.split("-");
        List<Subject> availableSubjects = this.all_instances.get(InstanceType.SUBJECT).stream()
                .map(subject -> (Subject) subject)
                .toList();

        Student student = new Student(instancesArray[0], this.all_instances.get(InstanceType.CLASS).stream()
                .map(clazz -> (Clazz) clazz)
                .filter(clazz -> clazz.getName().equals(instancesArray[1]))
                .findAny()
                .orElseThrow(() -> new Exception("Student " + instancesArray[0] +
                        " has no class or the given class (" + instancesArray[1] + ") does not exist.")));

// create map of subjects and grades - used further (if there is some subject twice, grade is set to 0 and further is thrown an exception)
        Map<String, Integer> subjectsAndGrades;
        try {
            subjectsAndGrades = Arrays.stream(instancesArray[2].split("; "))
                    .map(string -> string.split(":"))
                    .collect(Collectors.toMap(string -> string[0],
                            string -> GradeType.getFromNumber(Integer.parseInt(string[1])).getNumber(),
                            (first, second) -> 0));
        } catch (Exception e) {
            System.out.print(e.getMessage());
            throw new Exception(" -> " + student.getName() + ".");
        }
// check if the given names of the subjects exist (if some subject does not exist, grade is set to 0 and further is thrown an exception)
        subjectsAndGrades.forEach((key, value) -> {
            if (!availableSubjects.stream()
                    .map(Subject::getName)
                    .toList().contains(key)) {
                subjectsAndGrades.put(key, 0);
            }
        });
// throw an exception for conditions mentioned above (also for amount of subjects for one student)
        if (subjectsAndGrades.containsValue(0)) {
            List<String> subjects = subjectsAndGrades.entrySet().stream()
                    .filter((entry -> entry.getValue() == 0))
                    .map(Map.Entry::getKey)
                    .toList();
            throw new Exception("Student " + student.getName() + " already has grade in subjects - "
                    + subjects + ", or the subjects do not exist.");
        } else if (subjectsAndGrades.size() < this.subjectsPerStudentMinAmount) {
            throw new Exception("Student " + student.getName() + " has to study a least "
                    + this.subjectsPerStudentMinAmount + " subjects.");
        }

        subjectsAndGrades.forEach((key, value) ->
                student.addSubjectAndGrade(availableSubjects.stream()
                        .filter(subject -> subject.getName().equals(key))
                        .findFirst()
                        .orElse(null), value));

        this.checkExistingEntities(InstanceType.STUDENT, student);
    }

    private void checkExistingEntities(InstanceType instanceType, ComparableByName instance) throws Exception {
        if (!this.all_instances.get(instanceType).contains(instance)) {
            this.all_instances.get(instanceType).add(instance);
        } else {
            throw new Exception(instanceType.toString() + " " + instance.getName() + " already exists.");
        }
    }

    private void checkCreatedEntities() throws Exception {
        int classesMaxAmount = this.all_instances.get(InstanceType.TEACHER).size();

        if (this.all_instances.get(InstanceType.CLASS).size() < this.classesMinAmount) {
            throw new Exception("You must have at least " + this.classesMinAmount + " classes!");
        } else if (this.all_instances.get(InstanceType.CLASS).size() > classesMaxAmount) {
            throw new Exception("You cannot have more classes than teachers -> " + classesMaxAmount + "!");
        } else if (this.all_instances.get(InstanceType.TEACHER).size() < this.classesMinAmount) {
            throw new Exception("You must have at least " + this.classesMinAmount + " teachers!");
        } else if (this.all_instances.get(InstanceType.SUBJECT).size() < this.subjectsMinAmount) {
            throw new Exception("You must have at least " + this.subjectsMinAmount + " subjects!");
        }
    }

    public void printClasses() {
        System.out.println("-".repeat(50) + " REPORT CLASSES " + "-".repeat(50));
        this.classes
                .stream()
                .sorted(Comparator.comparing(Clazz::getName))
                .forEach(System.out::println);
        System.out.println();
        System.out.println("-".repeat(116));
    }

    public void sortedStudentsByAverageGrades() {
        System.out.println("Sorted students by their average grades: ");

        this.classes.stream()
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

    public void sortedSubjectsByAverageGrades() {
        System.out.println("Sorted subjects by average of grades given to students: ");

        this.classes.stream()
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

    public void sortedClassesByAverageGrades() {
        System.out.println("Sorted classes with the best students: ");

        this.classes.stream()
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

    public List<Clazz> getClasses() {
        return this.classes;
    }
}

