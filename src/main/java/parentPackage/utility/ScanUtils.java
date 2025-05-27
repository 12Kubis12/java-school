package parentPackage.utility;

import parentPackage.constants.Constants;
import parentPackage.domain.*;

import java.util.*;
import java.util.stream.Collectors;

public class ScanUtils {

    public static String[] scanInstances(InstanceType instanceType, Map<InstanceType, List<ComparableByName>> all_instances) {
        Set<String> instances = new HashSet<>();
        Set<String> usedNames = new HashSet<>();
        Set<String> usedTeachers = new HashSet<>();
        String instancesSting = "";
        Map<String, Integer> unfilledCLasses = new HashMap<>();
        int minAmount = Constants.INITIAL_LIMIT_FOR_SCAN;
        long maxAmount = Constants.INITIAL_LIMIT_FOR_SCAN;

        if (instanceType == InstanceType.CLASS || instanceType == InstanceType.TEACHER) {
            minAmount = Constants.CLASSES_MIN_AMOUNT;
            if (instanceType == InstanceType.CLASS) {
                maxAmount = all_instances.get(InstanceType.TEACHER).size();
            }
        } else if (instanceType == InstanceType.STUDENT) {
            unfilledCLasses = all_instances.get(InstanceType.CLASS).stream()
                    .map(clazz -> (Clazz) clazz)
                    .collect(Collectors.toMap(Clazz::getName, clazz -> 0));
        } else if (instanceType == InstanceType.SUBJECT) {
            minAmount = Constants.SUBJECTS_MIN_AMOUNT;
        }

        while (!instancesSting.equalsIgnoreCase("s") || instances.size() < minAmount
                || !unfilledCLasses.isEmpty()) {

            if (instanceType == InstanceType.STUDENT) {
                unfilledCLasses = unfilledCLasses.entrySet().stream()
                        .filter(entry -> entry.getValue() < Constants.STUDENTS_PER_CLASS_MIN_AMOUNT)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                if (!unfilledCLasses.isEmpty()) {
                    System.out.println("Fill these classes with at least " + Constants.STUDENTS_PER_CLASS_MIN_AMOUNT
                            + " students: ");
                    unfilledCLasses.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .forEach(entry -> System.out.println(entry.getKey()
                                    + " -> " + (Constants.STUDENTS_PER_CLASS_MIN_AMOUNT - entry.getValue()) + " more"));
                }
            }

            switch (instanceType) {
                case TEACHER -> instancesSting = scanTeacher(instanceType, usedNames);
                case SUBJECT -> instancesSting = scanSubject(instanceType, usedNames, all_instances);
                case CLASS -> instancesSting = scanClass(instanceType, usedNames, usedTeachers, all_instances);
                case STUDENT -> instancesSting = scanStudent(instanceType, usedNames, unfilledCLasses, all_instances);
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

    private static String scanTeacher(InstanceType instanceType, Set<String> usedNames) {
        return initialStep(instanceType, usedNames);
    }

    private static String scanSubject(InstanceType instanceType, Set<String> usedNames,
                                      Map<InstanceType, List<ComparableByName>> all_instances) {
        StringBuilder stringBuilder = new StringBuilder();
        String subject = initialStep(instanceType, usedNames);
        stringBuilder.append(subject);

        if (!subject.equalsIgnoreCase("s") && !subject.isEmpty()) {
            System.out.println("Write the teacher who teach the subject:");
            List<String> options = new ArrayList<>(all_instances.get(InstanceType.TEACHER).stream()
                    .map(teacher -> (Teacher) teacher)
                    .map(Teacher::getName)
                    .sorted()
                    .toList());
            stringBuilder.append("-").append(chooseFromOptions(options, false));
        }

        return stringBuilder.toString();
    }

    private static String scanClass(InstanceType instanceType, Set<String> usedNames, Set<String> usedTeachers,
                                    Map<InstanceType, List<ComparableByName>> all_instances) {
        StringBuilder stringBuilder = new StringBuilder();
        String clazz = initialStep(instanceType, usedNames);
        stringBuilder.append(clazz);

        if (!clazz.equalsIgnoreCase("s") && !clazz.isEmpty()) {
            System.out.println("Write the teacher who is the primary teacher:");
            List<String> options = new ArrayList<>(all_instances.get(InstanceType.TEACHER).stream()
                    .map(teacher -> (Teacher) teacher)
                    .map(Teacher::getName)
                    .filter(name -> !usedTeachers.contains(name))
                    .sorted()
                    .toList());
            String teacher = chooseFromOptions(options, false);
            usedTeachers.add(teacher);
            stringBuilder.append("-").append(teacher);
        }

        return stringBuilder.toString();
    }

    private static String scanStudent(InstanceType instanceType, Set<String> usedNames, Map<String, Integer> unfilledCLasses,
                                      Map<InstanceType, List<ComparableByName>> all_instances) {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> subjectOptions = new ArrayList<>(all_instances.get(InstanceType.SUBJECT).stream()
                .map(subject -> (Subject) subject)
                .map(Subject::getName)
                .sorted()
                .toList());
        int minSubjectsAmount = Constants.SUBJECTS_PER_STUDENT_MIN_AMOUNT;
        String student = initialStep(instanceType, usedNames);
        stringBuilder.append(student);

        if (!student.equalsIgnoreCase("s") && !student.isEmpty()) {
            System.out.println("Write a class the student is a part of:");
            List<String> options = new ArrayList<>(all_instances.get(InstanceType.CLASS).stream()
                    .map(clazz -> (Clazz) clazz)
                    .map(Clazz::getName)
                    .sorted()
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
                            .sorted()
                            .toList());
                    stringBuilder.append(chooseFromOptions(gradeOptions, false)).append("; ");
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

    private static String initialStep(InstanceType instanceType, Set<String> usedNames) {
        System.out.println("Write name of the " + instanceType + ":");
        System.out.println("(If you want to stop write \"s\").");
        return checkUsedNames(usedNames, InputAndOutputUtils.readLine());
    }

    private static String checkUsedNames(Set<String> usedNames, String name) {
        if (usedNames.contains(name)) {
            System.out.println("The name is already used!");
            return "";
        } else if (!name.equalsIgnoreCase("s")) {
            usedNames.add(name);
        }
        return name;
    }

    private static String chooseFromOptions(List<String> options, boolean possibleStop) {
        System.out.print("Choose from (white corresponding number): ");
        options.forEach(s -> System.out.print("\"" + s + "\" -> " + (options.indexOf(s) + 1) + ", "));
        System.out.println();

        return InputAndOutputUtils.readIntOption(options, possibleStop);
    }
}
