package sos;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello!");
    }

    // Fiind the largest of two numbers
    // Returns -1 if they are equal
    public static int findLargest(int a, int b) {
        if (a == b) return -1;
        return (a > b) ? a : b;
    }
}
