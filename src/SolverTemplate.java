import utils.SparceMatrix;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

class SolverTemplate {

    private static final String PROBLEM_NAME = "test";
    private static final String INPUTS_DIR = "./res/" + PROBLEM_NAME + "/inputs/";
    private static final String OUTPUTS_DIR = "./res/" + PROBLEM_NAME + "/outputs/";

    private static final String[] inputs = new String[]{
            "a_example.in"
            , "b_small.in"
            , "c_medium.in"
            , "d_quite_big.in"
            , "e_also_big.in"
    };



    public static void main(String[] args) throws IOException {
        SolverTemplate solver = new SolverTemplate();

        for (String testFileName : inputs) {
            solver.reset();
            solver.readInput(INPUTS_DIR + testFileName);

            solver.solve();

            solver.computeScore(testFileName);
            solver.writeResults(OUTPUTS_DIR + testFileName.replaceAll("\\.in$", ""));
        }
    }

    public SolverTemplate() {
        this.reset();
    }

    public void reset() {
        /// TODO : reset
    }

    private void computeScore(String filename) {
        int score = 0;

        System.out.println("Score for " + filename + " is : " + score);
    }

    public void solve() {
        /// TODO: Solve
    }


    public void readInput(String filePath) throws IOException {
        String fileName = Paths.get(filePath).getFileName().toString().replaceFirst("[.][^.]+$", "");

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            String[] values = line.split("\\s+");

            /// TODO : read first line
//            Integer.parseInt(values[0]);
//            Integer.parseInt(values[1]);

            line = br.readLine();
            values = line.split("\\s+");
            for (int i = 0; i < values.length; i++) {
               /// Todo : read second line
            }

        }
    }

    private void writeResults(String filePath) throws FileNotFoundException, UnsupportedEncodingException  // ignore this
    {
        PrintWriter writer = new PrintWriter(filePath + ".out", "UTF-8");
        writer.print("\n");
        /// TODO : write results
//        for () {
//            writer.print(" ");
//        }
        writer.close();
    }
}