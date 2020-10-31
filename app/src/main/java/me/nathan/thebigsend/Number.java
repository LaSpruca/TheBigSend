package me.nathan.thebigsend;

import java.util.Map;

public class Number {
    public String phoneNumber;
    public Map<String, String> mergeData;

    public Number(String phoneNumber, Map<String, String> mergeData) {
        this.phoneNumber = phoneNumber;
        this.mergeData = mergeData;
        validateNumber();
    }

    /**
     * Function to check that the number is valid, this only supports nz country code
     */
    private void validateNumber() {
        // Create a new string builder
        StringBuilder sb = new StringBuilder();
        sb.append(this.phoneNumber);

        // Remove the country code
        if (sb.toString().startsWith("64")) {
            sb.substring(0, 2);
        } else if (sb.toString().startsWith("+64")) {
            sb.substring(0, 3);
        }

        // Add 0 to start of number if not present
        if (!sb.toString().startsWith("0")) {
            sb.insert(0, "0");
        }

        // Return the validated string
        this.phoneNumber = sb.toString();
    }

    @Override
    public String toString() {
        return "Number{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", mergeData=" + mergeData +
                '}';
    }
}
