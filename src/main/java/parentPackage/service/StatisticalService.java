package parentPackage.service;

import parentPackage.domain.Clazz;
import parentPackage.domain.Student;

import java.util.*;
import java.util.stream.Collectors;

public class StatisticalService {

    private void printClasses(List<Clazz> classes) {
        System.out.println("-".repeat(50) + " REPORT CLASSES " + "-".repeat(50));
        classes
                .stream()
                .sorted(Comparator.comparing(Clazz::getName))
                .forEach(System.out::println);
        System.out.println();
        System.out.println("-".repeat(116));
    }

    private void sortedStudentsByAverageGrades(List<Clazz> classes) {
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

    private void sortedSubjectsByAverageGrades(List<Clazz> classes) {
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

    private void sortedClassesByAverageGrades(List<Clazz> classes) {
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

    public void pintStats(List<Clazz> classes) {
        if (!classes.isEmpty()) {
            this.printClasses(classes);

            this.sortedStudentsByAverageGrades(classes);
            this.sortedSubjectsByAverageGrades(classes);
            this.sortedClassesByAverageGrades(classes);
        }
    }
}
