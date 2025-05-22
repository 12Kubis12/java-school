package parentPackage.school;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class School {
    private Scanner scanner;
    private final Map<InstanceType, List<Object>> all_instances;
    private String inputWay;
    private final List<Clazz> classes;
    private boolean continueVariable;

    public School() {
        this.all_instances = new HashMap<>();
        this.classes = new ArrayList<>();
        this.continueVariable = true;
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
                createEntityType(line);
            }
        } else if (this.inputWay.equals("T")) {
            System.out.println("Be careful what you write otherwise you can create entities with strange names or" +
                    " start from the beginning if you use something wrong way!");
            int count = 1;
            while (count <= 4) {
                switch (count) {
                    case 1 -> createEntityType(InstanceType.TEACHER.toString());
                    case 2 -> createEntityType(InstanceType.SUBJECT.toString());
                    case 3 -> createEntityType(InstanceType.CLASS.toString());
                    case 4 -> createEntityType(InstanceType.STUDENT.toString());
                }
// if there is thrown an exception (in case of creating every option from console), the process start from the beginning.
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

    // here are caught most exceptions
    private void createEntityType(String type) {
        try {
            InstanceType instanceType = InstanceType.getFromString(type);
            createInstances(instanceType);
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
            instancesLine = scanInstances(instanceType);
        }
// if everything is created (except students), check the conditions for amount of entities
        if (instanceType == InstanceType.STUDENT) {
            this.checkCreatedEntities();
        }

        for (String instance : instancesLine) {
            switch (instanceType) {
                case TEACHER -> createTeacher(instance);
                case SUBJECT -> createSubject(instance);
                case CLASS -> createClass(instance);
                case STUDENT -> createStudent(instance);
            }
        }
// if everything is created (including students), check the condition for amount students in one class
        if (instanceType == InstanceType.STUDENT) {
            int studentsPerClassMinAmount = 3;
            List<Clazz> classesWithNotEnoughStudents = all_instances.get(InstanceType.CLASS).stream()
                    .map(clazz -> (Clazz) clazz)
                    .filter(clazz -> clazz.getStudents().size() < studentsPerClassMinAmount)
                    .toList();
            if (!classesWithNotEnoughStudents.isEmpty()) {
                throw new Exception("These classes must have at least " + studentsPerClassMinAmount
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
        int minStudentsPerClass = 3;
        Map<String, Integer> unfilledCLasses = new HashMap<>();
        int minAmount = 0;
        long maxAmount = 0;
// set conditions for amount of entities
        if (instanceType == InstanceType.CLASS || instanceType == InstanceType.TEACHER) {
            minAmount = 2;
            if (instanceType == InstanceType.CLASS) {
                maxAmount = this.all_instances.get(InstanceType.TEACHER).size();
            }
        } else if (instanceType == InstanceType.STUDENT) {
            unfilledCLasses = this.all_instances.get(InstanceType.CLASS).stream()
                    .map(clazz -> (Clazz) clazz)
                    .collect(Collectors.toMap(Clazz::getName, clazz -> 0));
        } else {
            minAmount = 3;
        }

        while (!instancesSting.equalsIgnoreCase("s") || instances.size() < minAmount
                || !unfilledCLasses.isEmpty()) {
// check the condition for amount students in one class
            if (instanceType == InstanceType.STUDENT) {
                unfilledCLasses = unfilledCLasses.entrySet().stream()
                        .filter(entry -> entry.getValue() < minStudentsPerClass)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                if (!unfilledCLasses.isEmpty()) {
                    System.out.println("Fill these classes with at least " + minStudentsPerClass + " students: ");
                    unfilledCLasses.forEach((key, value) -> System.out.println(key
                            + " -> " + (minStudentsPerClass - value) + " more"));
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
                    .toList());
            stringBuilder.append("-").append(chooseFromOptions(options, false));
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
                    .toList());
            String teacher = chooseFromOptions(options, false);
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
                .toList());
        int minSubjectsAmount = 3;
        String student = this.initialStep(instanceType, usedNames);
        stringBuilder.append(student);

        if (!student.equalsIgnoreCase("s") && !student.isEmpty()) {
            System.out.println("Write a class the student is a part of:");
            List<String> options = new ArrayList<>(this.all_instances.get(InstanceType.CLASS).stream()
                    .map(clazz -> (Clazz) clazz)
                    .map(Clazz::getName)
                    .toList());
            String clazz = chooseFromOptions(options, false);

            if (unfilledCLasses.containsKey(clazz)) {
                unfilledCLasses.put(clazz, unfilledCLasses.get(clazz) + 1);
            }

            stringBuilder.append("-").append(clazz).append("-");
            System.out.println("Now write subjects the student study and for each subject write grade from 1 to 5 inclusive.\n" +
                    "If you want to stop write \"s\".");

            while (!subjectOptions.isEmpty()) {
                System.out.println("Write the subject:");
                String subject = chooseFromOptions(subjectOptions, true);

                if (!subject.equalsIgnoreCase("s")) {
                    stringBuilder.append(subject).append(":");
                    minSubjectsAmount--;
                    System.out.println("Write the grade:");
                    List<String> gradeOptions = new ArrayList<>(Arrays.stream(GradeType.values())
                            .map(gradeType -> Integer.toString(gradeType.getNumber()))
                            .toList());
                    stringBuilder.append(chooseFromOptions(gradeOptions, false)).append("; ");
                } else if (minSubjectsAmount > 0) {
                    System.out.println("Give the student " + minSubjectsAmount + " more subjects!");
                } else {
                    break;
                }
            }

            int length = stringBuilder.length();
            stringBuilder.delete(length - 2, length);
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

    // every time an instance is being created which needs different existing instance, this function is called to give options
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
        if (!this.all_instances.get(InstanceType.TEACHER).contains(teacher)) {
            this.all_instances.get(InstanceType.TEACHER).add(teacher);
        } else {
            throw new Exception("Teacher " + teacher.getName() + " already exists");
        }
    }

    private void createSubject(String instanceLine) throws Exception {
        String[] instancesArray = instanceLine.split("-");
        Subject subject = new Subject(instancesArray[0], this.all_instances.get(InstanceType.TEACHER).stream()
                .map(teacher -> (Teacher) teacher)
                .filter(teacher -> teacher.getName().equals(instancesArray[1]))
                .findFirst()
                .orElse(null));
// check if the given name of the teacher exists
        if (subject.getTeacher() == null) {
            throw new Exception("Subject " + subject.getName() + " has no teacher or the given teacher does not exist.");
        }

        if (!this.all_instances.get(InstanceType.SUBJECT).contains(subject)) {
            this.all_instances.get(InstanceType.SUBJECT).add(subject);
        } else {
            throw new Exception("Subject " + subject.getName() + " already exists");
        }
    }

    private void createClass(String instanceLine) throws Exception {
        String[] instancesArray = instanceLine.split("-");

        Clazz clazz = new Clazz(instancesArray[0], this.all_instances.get(InstanceType.TEACHER).stream()
                .map(teacher -> (Teacher) teacher)
                .filter(teacher -> teacher.getClazz() == null)
                .filter(teacher -> teacher.getName().equals(instancesArray[1]))
                .findFirst()
                .orElse(null));
// check if the given name of the teacher exists
        if (clazz.getPrimaryTeacher() == null) {
            throw new Exception("Class " + clazz.getName() + " has no primary teacher or the given teacher does not exist.");
        }

        if (!this.all_instances.get(InstanceType.CLASS).contains(clazz)) {
            this.all_instances.get(InstanceType.CLASS).add(clazz);
        } else {
            throw new Exception("Class " + clazz.getName() + " already exists");
        }
    }

    private void createStudent(String instanceLine) throws Exception {
        String[] instancesArray = instanceLine.split("-");
        int minSubjectsAmount = 3;
        List<Subject> availableSubjects = this.all_instances.get(InstanceType.SUBJECT).stream()
                .map(subject -> (Subject) subject)
                .toList();

        Student student = new Student(instancesArray[0], this.all_instances.get(InstanceType.CLASS).stream()
                .map(clazz -> (Clazz) clazz)
                .filter(clazz -> clazz.getName().equals(instancesArray[1]))
                .findFirst()
                .orElse(null));
// check if the given name of the class exists
        if (student.getClazz() == null) {
            throw new Exception("Student " + student.getName() + " has no class or the given class does not exist.");
        }
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
        } else if (subjectsAndGrades.size() < minSubjectsAmount) {
            throw new Exception("Student " + student.getName() + " has to study a least " + minSubjectsAmount + " subjects.");
        }

        subjectsAndGrades.forEach((key, value) ->
                student.addSubjectAndGrade(availableSubjects.stream()
                        .filter(subject -> subject.getName().equals(key))
                        .findFirst()
                        .orElse(null), value));

        if (!this.all_instances.get(InstanceType.STUDENT).contains(student)) {
            this.all_instances.get(InstanceType.STUDENT).add(student);
        } else {
            throw new Exception("Student " + student.getName() + " already exists");
        }
    }

    private void checkCreatedEntities() throws Exception {
        int classesMinAmount = 2;
        int classesMaxAmount = all_instances.get(InstanceType.TEACHER).size();

        if (all_instances.get(InstanceType.CLASS).size() < classesMinAmount) {
            throw new Exception("You must have at least " + classesMinAmount + " classes!");
        } else if (all_instances.get(InstanceType.CLASS).size() > classesMaxAmount) {
            throw new Exception("You cannot have more classes than teachers -> " + classesMaxAmount + "!");
        } else if (all_instances.get(InstanceType.TEACHER).size() < classesMinAmount) {
            throw new Exception("You must have at least " + classesMinAmount + " teachers!");
        }
    }

    public void printClasses() {
        System.out.println("-".repeat(50) + " REPORT CLASSES " + "-".repeat(50));
        this.classes.forEach(System.out::println);
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

