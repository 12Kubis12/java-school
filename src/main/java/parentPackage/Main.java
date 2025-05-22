package parentPackage;

public class Main {
    public static void main(String[] args) {
        School school = new School();

        if (!school.getClasses().isEmpty()) {
            school.printClasses();
            school.sortedStudentsByAverageGrades();
            school.sortedSubjectsByAverageGrades();
            school.sortedClassesByAverageGrades();
        }
    }
}