package parentPackage.school;

public enum GradeType {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5);

    private final int number;

    GradeType(int number) {
        this.number = number;
    }

    public static GradeType getFromNumber(int number) {
        for (GradeType gradeType : GradeType.values()) {
            if (gradeType.number == number) {
                return gradeType;
            }
        }
        throw new IllegalArgumentException("The grade \"" + number + "\" is not between 1 and 5 inclusive.");
    }

    public int getNumber() {
        return this.number;
    }
}
