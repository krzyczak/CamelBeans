package io.github.krzyczak;

//import org.apache.camel.Header;

public class MyBean {
//    public static boolean isGoldCustomer(@Header("level") String level) {
    public static boolean isGoldCustomer(String level) {
        String[] data = level.split(",");
        System.out.println("Level: " + level + " Gold: " + data[2] + " (" + data[2].equals("gold") + ").");
        return data[2].equals("gold");
    }
}
