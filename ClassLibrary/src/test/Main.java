package test;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello, World!");
        System.out.println("Argument count: " + args.length);
        for (int i = 0; i < args.length; i++) {
            System.out.println(args[i]);
        }
        byte[] test = new byte[8192 - 84];
        for (int j = 0; j < 15; j++) {
            System.out.println("This is a test...");
        }
        //throw new Error("Error'd!");
    }
}
