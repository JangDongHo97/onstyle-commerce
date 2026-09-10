package com.example.aop;

public class PiCalculator {
    public static void main(String[] args) {
        PiCalculator pi = new PiCalculator();
        System.out.println(pi.calculate(100000000));
    }

    double calculate(int points) {
        long start = System.currentTimeMillis();
        int circle = 0;

        for (long i = 0; i < points; i++) {
            double x = Math.random() * 2 - 1;
            double y = Math.random() * 2 - 1;

            if (x * x + y * y <= 1) {
                circle++;
            }
        }

        long executionTime = System.currentTimeMillis() - start;
        System.out.println("executed in " + executionTime + "ms.");
        return 4.0 * circle / points;
    }
}
