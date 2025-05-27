package parentPackage.domain;

import parentPackage.constants.Constants;
import parentPackage.service.StatisticalService;
import parentPackage.utility.InputAndOutputUtils;
import parentPackage.utility.ScanUtils;

import java.util.*;
import java.util.stream.Collectors;

public class School {
    private StringBuilder stringToWrite;

    private final Map<InstanceType, List<ComparableByName>> all_instances;
    private final List<Clazz> classes;

    private String inputWay;
    private boolean continueVariable;

    private final int subjectsMinAmount;
    private final int classesMinAmount;
    private final int subjectsPerStudentMinAmount;
    private final int studentsPerClassMinAmount;

    StatisticalService statisticalService;

    public School() {
        this.all_instances = new HashMap<>();
        this.classes = new ArrayList<>();
        this.continueVariable = true;

        this.subjectsMinAmount = Constants.SUBJECTS_MIN_AMOUNT;
        this.classesMinAmount = Constants.CLASSES_MIN_AMOUNT;
        this.subjectsPerStudentMinAmount = Constants.SUBJECTS_PER_STUDENT_MIN_AMOUNT;
        this.studentsPerClassMinAmount = Constants.STUDENTS_PER_CLASS_MIN_AMOUNT;

        this.statisticalService = new StatisticalService();

        this.chooseInputWay();
        this.createEntities();
    }

    private void chooseInputWay() {
        while (true) {
            System.out.println("Choose the way of input.\nYou can create every instance by typing ('T') " +
                    "or load them from a file ('F'):");
            this.inputWay = InputAndOutputUtils.readLine().replaceAll("\\s", "").toUpperCase();
            if (this.inputWay.equals("F") || this.inputWay.equals("T")) {
                break;
            } else {
                System.out.println("Invalid input. Try again!!!");
            }
        }
    }

    private void createEntities() {
        if (this.inputWay.equals("F")) {
            this.continueVariable = InputAndOutputUtils.readFromFile();

            while (this.continueVariable && InputAndOutputUtils.checkFileLine()) {
                String line = InputAndOutputUtils.readLine().replaceAll("\\s", "").toUpperCase();
                this.createEntityType(line);
            }
        } else if (this.inputWay.equals("T")) {
            System.out.println("Be careful what you write otherwise you can create entities with strange names or" +
                    " start from the beginning if you use something wrong way!");
            this.stringToWrite = new StringBuilder();

            int count = 1;
            while (count <= 4) {
                switch (count) {
                    case 1 -> this.createEntityType(InstanceType.TEACHER.toString());
                    case 2 -> this.createEntityType(InstanceType.SUBJECT.toString());
                    case 3 -> this.createEntityType(InstanceType.CLASS.toString());
                    case 4 -> this.createEntityType(InstanceType.STUDENT.toString());
                }

                if (this.continueVariable) {
                    count++;
                } else {
                    count = 1;
                }
            }

            InputAndOutputUtils.writeToFile(this.stringToWrite.toString());
        }

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
            instancesLine = InputAndOutputUtils.readLine().split(", ");
        } else {
            instancesLine = ScanUtils.scanInstances(instanceType, this.all_instances);

            this.stringToWrite.append(instanceType.toString()).append("\n");
            Arrays.stream(instancesLine)
                    .sorted()
                    .forEach(instance -> this.stringToWrite.append(instance).append(", "));
            this.stringToWrite.delete(this.stringToWrite.length() - 2, this.stringToWrite.length());
            if (instanceType != InstanceType.STUDENT) {
                this.stringToWrite.append("\n");
            }
        }

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

        subjectsAndGrades.forEach((key, value) -> {
            if (!availableSubjects.stream()
                    .map(Subject::getName)
                    .toList().contains(key)) {
                subjectsAndGrades.put(key, 0);
            }
        });

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

    public void printStats() {
        statisticalService.pintStats(this.classes);
    }
}

