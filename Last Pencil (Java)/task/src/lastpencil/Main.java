package lastpencil;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            String name1 = "John";
            String name2 = "Jack";
            System.out.println("How many pencils would you like to use:");

            int numberOfPencils = sc.nextInt();
            sc.nextLine();

            System.out.println("Who will be the first (" + name1 + ", " + name2 + "):");
            String firstPlayer = sc.next();

            String pencilStr = new String(new char[numberOfPencils]).replace("\0", "|");

            System.out.println(pencilStr + "\n" + firstPlayer + "is going first!");
        }
    }
}
