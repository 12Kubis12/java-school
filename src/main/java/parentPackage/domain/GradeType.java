package parentPackage.domain;

import java.util.Arrays;

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
        return Arrays.stream(GradeType.values())
                .filter(gradeType -> gradeType.number == number)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("The grade \"" + number + "\" is not between 1 and 5 inclusive."));
    }

    public int getNumber() {
        return this.number;
    }
}
