import org.hyperskill.hstest.dynamic.DynamicTest;
import org.hyperskill.hstest.exception.outcomes.WrongAnswer;
import org.hyperskill.hstest.stage.StageTest;
import org.hyperskill.hstest.testcase.CheckResult;
import org.hyperskill.hstest.testing.TestedProgram;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LastPencilTest extends StageTest<String> {
    public LastPencilTest() {
        testData = new Object[][]{
                {5, 1, new int[]{2, 1, 2}},
                {20, 1, new int[]{3, 2, 3, 1, 2, 3, 3, 3}},
                {30, 1, new int[]{3, 2, 3, 1, 2, 3, 3, 3, 2, 1, 2, 3, 2}},
                {15, 1, new int[]{8, 7}},
                {5, 2, new int[]{2, 1, 2}},
                {20, 2, new int[]{3, 2, 3, 1, 2, 3, 3, 3}},
                {30, 2, new int[]{3, 2, 3, 1, 2, 3, 3, 3, 2, 1, 2, 3, 2}},
                {15, 2, new int[]{8, 7}},
        };
    }

    @DynamicTest
    CheckResult checkOutput() {
        TestedProgram main = new TestedProgram();
        String output = main.start().toLowerCase();
        String[] lines = output.strip().split("\\n");

        if (lines.length != 1 || !Pattern.matches("^(how many pencils would you like to use)\\??:?$", lines[0])) {
            return CheckResult.wrong("When the game starts, it should output only one line asking the user about the " +
                    "amount of pencils they would like to use with the \"How many pencils would you like to use\" string.");
        }

        String output2 = main.execute("1").replaceAll(" ", "").toLowerCase();

        //regex
        String whoWillBeRegex = "^(whowillbethefirst).*";
        String playerChecking = ".*\\([a-zA-Z_0-9]+,[a-zA-Z_0-9]+\\):?";

        if (output2.split("\\n").length != 1) {  //Checking \n lines
            return CheckResult.wrong("When the user replies with the amount of pencils, the game should print 1 non-empty " +
                    "line asking \"Who will be the first\" player.\n"
                    + output2.split("\n").length + " lines were found in the output.");
        }

        output2 = output2.strip();
        // I noticed an interesting feature, this test becomes a big mystery if not used in output2 "strip"
        // Example "!Pattern.matches(whoWillBeRegex, output2.strip())" and "!Pattern.matches(whoWillBeRegex, output2)"
        if (!Pattern.matches(whoWillBeRegex, output2)) //Checking a task "Who will be the first"
            return CheckResult.wrong("Discrepancy with the task, pay attention to the line" +
                    "\"Who will be the first\"");

        //I think the lines "Who will be the first" still need to be checked
        if (!Pattern.matches(playerChecking, output2)) //Checking line "(Name1, Name2)")
            return CheckResult.wrong("Discrepancy with the task, output example" +
                    " \"Who will be the first (Name1, Name2):\"");
        return CheckResult.correct();
    }


    Object[][] testData; // array for testing


    @DynamicTest(data = "testData")
    CheckResult checkResult(int amount, int first, int[] moves) {

        //Started tests
        TestedProgram main = new TestedProgram();
        main.start();
        String output2 = main.execute(String.valueOf(amount));
        output2 = output2.replace(" ", "");
        //Get names for tests
        String leftName = output2.substring(output2.lastIndexOf('(') + 1, output2.lastIndexOf(','));
        String rightName = output2.substring(output2.lastIndexOf(',') + 1, output2.lastIndexOf(')'));
//Name change block for tests [start]
        String prevPlayer, nextPlayer;
        if (first == 1) {
            prevPlayer = leftName;
            nextPlayer = rightName;
        } else {
            prevPlayer = rightName;
            nextPlayer = leftName;
        }
//Name change block for tests [Finish]
        String output3 = main.execute(prevPlayer).toLowerCase();

        String[] lines = output3.trim().split("\\n"); //Creates an array for checking "\n"
        List<String> linesNonEmpty = Arrays.stream(lines).filter(s -> s.length() != 0).collect(Collectors.toList()); //Creates a list for checking empty lines

//NEW TEST
        if (leftName.equals(rightName)) { //Checking player name1 != name2
            return CheckResult.wrong("The names of the players must be different," +
                    " lines were found in the output: Name1 - \""
                    + leftName + "\" Name2 - \"" + rightName + "\".");
        }
//END NEW TEST
        if (linesNonEmpty.size() != 2) {  //Checking Name` turn
            return CheckResult.wrong("When the player provides the initial game conditions"
                    + ", your program should print 2 non-empty lines:\n"
                    + "one with with vertical bar symbols representing the number of pencils, "
                    + "the other with the \"Name` turn\" string.\n"
                    + linesNonEmpty.size() + " lines were found in the output.");
        }

        List<String> checkPencils = Arrays.stream(lines).filter(s -> s.contains("|")).toList(); //Create list lines containing | for testing
        if (checkPencils.size() == 0) { // Checking how many pencils are in a line
            return CheckResult.wrong("When the player provides the initial game conditions"
                    + ", your program should print one line with several vertical bar "
                    + "symbols ('|') representing pencils.");
        }
        if (checkPencils.size() > 1) { //Checking how many lines with pencils
            return CheckResult.wrong("When the player provides the initial game conditions"
                    + ", your program should print only one line with several vertical bar "
                    + "symbols ('|') representing pencils.");
        }
//        if (new HashSet<>(checkPencils.get(0).chars().mapToObj(c -> (char) c).collect(Collectors.toList())).size() != 1) {
        // distinct() makes it possible to get the number of non-unique elements, thus getting 2, the test fails
        if (checkPencils.get(0).chars().distinct().count() != 1) {
            return CheckResult.wrong("The line with pencils should not contain any symbols other than the '|' symbol.");
        }

        if (checkPencils.get(0).length() != amount) { //Checking for compliance with the amount transferred by the player | Characters
            return CheckResult.wrong("The line with pencils should contain as many '|' symbols as the player provided.");
        }

        boolean checkTurn = false;
        for (String line : lines) {//Validation variable for compliance with the selected player
            if (line.toLowerCase().contains(prevPlayer.toLowerCase()) && line.contains("turn")) {
                checkTurn = true;
                break;
            }
        }

        if (!checkTurn) { //Checking for compliance with the selected player
            return CheckResult.wrong("When the player provides the initial game conditions"
                    + " there should be a line in output that contains the \""
                    + prevPlayer + "'s turn\""
                    + " string if " + prevPlayer + " is the first player.");
        }

        int onTable = amount; // Pencils amount

        for (int i : moves) {
            onTable -= i;
            String output = main.execute(String.valueOf(i)).toLowerCase();
            lines = output.trim().split("\\n");
            linesNonEmpty = Arrays.stream(lines).filter(s -> //Fill in with non-empty lines for check.
                    s.length() != 0).collect(Collectors.toList());

            if (onTable <= 0) { //Checking the output after the last pencil has been taken
                if (linesNonEmpty.size() != 0) {
                    return CheckResult.wrong("After the last pencil is taken," +
                            " there should be no output.");
                } else {
                    break;
                }
            }

            if (linesNonEmpty.size() != 2) { //Check for eligibility, for non-empty lines after player selection
                return CheckResult.wrong("When one of the players enters the amount " +
                        "of pencils they want to remove, your program should print" +
                        " 2 non-empty lines.");
            }


            checkPencils = Arrays.stream(lines).filter(s ->
                    s.contains("|")).collect(Collectors.toList()); //Creating List that contains output with | in a row
// Checking line contains | [start]
            if (checkPencils.size() == 0) {
                return CheckResult.wrong("When one of the players enters the amount " +
                        "of pencils they want to remove, "
                        + "your program should print one line with vertical bar" +
                        " symbols ('|') representing pencils.");
            }
            if (checkPencils.size() > 1) {
                return CheckResult.wrong("When one of the players enters the amount " +
                        "of pencils they want to remove, "
                        + "your program should print only one line with vertical" +
                        " bar symbols ('|') representing pencils.");
            }
            if (!checkPencils.get(0).replaceAll("\\|", "").isEmpty()) {
                return CheckResult.wrong("The line with pencils should not contain any" +
                        " symbols other than the '|' symbol.");
            }
            if (checkPencils.get(0).length() != onTable) {
                return CheckResult.wrong("When one of the players enters the amount of" +
                        " pencils they want to remove, "
                        + "the line with pencils should contain as many '|' symbols " +
                        "as there are pencils left.");
            }
// Checking line contains | [finish]

            checkTurn = false;
            for (String line : lines) { // Creating boolean variable to check for presence in a string "NAME turn"
                if (line.toLowerCase().contains(nextPlayer.toLowerCase()) && line.contains("turn")) {
                    checkTurn = true;
                    break;
                }
            }
            if (!checkTurn) {
                return CheckResult.wrong(// Player rotation match checks
                        String.format("When %s enters the amount of pencils " +
                                "they want to remove, " +
                                "there should be a line in the output " +
                                "that contains \"%s turn\".", prevPlayer, nextPlayer));
            }
            String temp = prevPlayer;
            prevPlayer = nextPlayer;
            nextPlayer = temp;

        }
        if (!main.isFinished()) {//Checking, if conditions print, but the program then sends a request
            return CheckResult.wrong("Your program should not request " +
                    "anything when there are no pencils left.");
        }
        return CheckResult.correct();
    }
}