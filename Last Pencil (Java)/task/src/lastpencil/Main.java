package lastpencil;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            String name1 = "John";
            String name2 = "Jack";
            System.out.println("How many pencils would you like to use:");

            int nPencils = sc.nextInt();
            sc.nextLine();

            System.out.println("Who will be the first (" + name1 + ", " + name2 + "):");

            String currentPlayer = "Jack";
            if (sc.next().equals("John")) {
                currentPlayer = "John";
            }

            System.out.println(pencilString(nPencils));

            while (true) {
                System.out.println(currentPlayer + "'s turn:");
                nPencils = nPencils - sc.nextInt();

                if (nPencils <= 0) {
                    break;
                }

                System.out.println(pencilString(nPencils));
                currentPlayer = currentPlayer.equals("John") ? "Jack" : "John";
            }
        }
    }

    static String pencilString(int n) {
        return new String(new char[n]).replace("\0", "|");
    }
}
