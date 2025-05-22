package parentPackage.school;

public enum InstanceType {
    CLASS,
    TEACHER,
    STUDENT,
    SUBJECT;

    public static InstanceType getFromString(String string) {
        for (InstanceType instanceType : InstanceType.values()) {
            if (string.toUpperCase().equals(instanceType.toString())) {
                return instanceType;
            }
        }
        throw new IllegalArgumentException("Wrong instance type.");
    }
}
