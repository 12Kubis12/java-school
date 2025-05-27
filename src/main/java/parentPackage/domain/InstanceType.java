package parentPackage.domain;

import java.util.Arrays;

public enum InstanceType {
    CLASS,
    TEACHER,
    STUDENT,
    SUBJECT;

    public static InstanceType getFromString(String string) {
        return Arrays.stream(InstanceType.values())
                .filter(instanceType -> instanceType.toString().equals(string))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Wrong instance type -> " + string));
    }
}
