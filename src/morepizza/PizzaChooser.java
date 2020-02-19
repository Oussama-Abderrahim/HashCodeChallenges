package morepizza;

import utils.SparceMatrix;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

class PizzaChooser {

    private static final String INPUTS_DIR = "./res/morepizza/inputs/";
    private static final String OUTPUTS_DIR = "./res/morepizza/outputs/";

    private static final String[] inputs = new String[]{
            "a_example.in"
            , "b_small.in"
            , "c_medium.in"
            , "d_quite_big.in"
            , "e_also_big.in"
    };

    private ArrayList<Pizza> types; // Types of pizza ( index + number of Slices )
    private ArrayList<Pizza> choosenTypes; // Types of pizza choosen ( index + number of Slices )

    private int M;  // Maximum number of pizza Slices
    private int N;  // Number of different types of Pizza

    static class Pizza {
        int index;
        int nbSlices;

        Pizza(int i, int nb) {
            this.index = i;
            this.nbSlices = nb;
        }

        public void printPizza() {
            System.out.println("Pizza n°" + index + " has " + nbSlices + " slices");
        }
    }

    public static void main(String[] args) throws IOException {
        PizzaChooser pizzaChooser = new PizzaChooser();

        for (String testFileName : inputs) {
            pizzaChooser.reset();
            pizzaChooser.readInput(INPUTS_DIR + testFileName);

            pizzaChooser.estimate();
            pizzaChooser.computeScore();
            pizzaChooser.writeResults(OUTPUTS_DIR + testFileName.replaceAll("\\.in$", ""));
        }
    }

    public PizzaChooser() {
        this.types = new ArrayList<>();
    }

    public void reset() {
        this.types = new ArrayList<>();
        this.choosenTypes = new ArrayList<>();
        N = 0;
        M = 0;
    }

    private void computeScore() {
        int score = 0;
        for (Pizza pizza : choosenTypes)
            score += pizza.nbSlices;

        System.out.println("Score for (N,M) = (" + N + "," + M + ") is : " + score);
    }

    public void estimate() {
        /// TODO: try more methods
        estimateGreedy3();
    }

    private void estimateGreedy2() {

        // remove all slices if greater than M
        for (Pizza p : types) {
            if (p.nbSlices > M) types.remove(p);
        }

        // Sort Java 8 Final Form
        types.sort(Comparator.comparingInt(o -> o.nbSlices));

        // to save intermediate results

        // Greedy solution
        int maxScore = 0;
        int i = 0;
        while (maxScore <= M && maxScore + types.get(i).nbSlices <= M) {
            maxScore += types.get(i).nbSlices;
            choosenTypes.add(types.get(i));
            i++;
        }

        // get Score without ith element
        for (i = 0; i < types.size(); i++) {
            ArrayList<Pizza> tempChoosen = new ArrayList<>();

            int j = 0;
            int score = 0;

            while (j < N && score < M && (i == j || score + types.get(j).nbSlices <= M)) {
                if (i != j) {
                    score += types.get(j).nbSlices;
                    tempChoosen.add(types.get(j));
                }
                j++;
            }

            // Compare
            if (score > maxScore) {
                maxScore = score;
                choosenTypes = tempChoosen;
            }

            if (score >= M) break;
        }
    }

    private void estimateGreedy3() {

        // remove all slices if greater than M
        for (Pizza p : types) {
            if (p.nbSlices > M) types.remove(p);
        }

        // Sort Java 8 Final Form
        types.sort(Comparator.comparingInt(o -> o.nbSlices));

        // to save intermediate results

        // Greedy solution
        int[] dp = new int[N];

        int maxScore = 0;
        int i = 0;
        while (maxScore <= M && maxScore + types.get(i).nbSlices <= M) {
            maxScore += types.get(i).nbSlices;
            choosenTypes.add(types.get(i));
            dp[i] = maxScore;
            i++;
        }

        int greedyIndex = i - 1;
        int maxIndex = greedyIndex;
        int maxExcludedIndexI = -1;
        int maxExcludedIndexK = -1;


        for (int k = 0; k < types.size(); k++) {

            // get Score without ith element
            for (i = 0; i < types.size(); i++) {

                int j = greedyIndex;
                int score = dp[j];

                if (i <= j)
                    score = score - types.get(i).nbSlices;
                if (k <= j && k != i)
                    score = score - types.get(k).nbSlices;

                j++;

                while (j < N && score < M && (i == j || k == j || score + types.get(j).nbSlices <= M)) {
                    if (i != j && k != j) {
                        score += types.get(j).nbSlices;
                    }
                    j++;
                }

                // Compare
                if (score > maxScore) {
                    maxScore = score;
                    maxIndex = j - 1;
                    maxExcludedIndexI = i;
                    maxExcludedIndexK = k;
                }

                if (score >= M) break;
            }
        }
        // Build the solution
        ArrayList<Pizza> tempChoosen = new ArrayList<>();

        for (int j = 0; j <= maxIndex; j++) {
            tempChoosen.add(types.get(j));
        }

        if (maxExcludedIndexI != -1 && maxExcludedIndexI <= maxIndex)
            tempChoosen.remove(types.get(maxExcludedIndexI));

        if (maxExcludedIndexK != -1 && maxExcludedIndexK <= maxIndex)
            tempChoosen.remove(types.get(maxExcludedIndexK));

        choosenTypes = tempChoosen;
    }


    private void estimateGreedy() {

        // remove all slices if greater than M
        for (Pizza p : types) {
            if (p.nbSlices > M) types.remove(p);
        }

        // Sort Java 8 Final Form
        types.sort(Comparator.comparingInt(o -> -1 * o.nbSlices));


        // Greedy solution
        int x = 0;
        int i = 0;
        while (x <= M && x + types.get(i).nbSlices <= M) {
            x += types.get(i).nbSlices;
            choosenTypes.add(types.get(i));
            i++;
        }
    }

    /**
     * A dynamic solution to check whether a subset whose sum equals n exists
     *
     * @param n   number of elements
     * @param sum the value
     * @return (Boolean)
     */
    boolean isSubsetSum(int n, int sum) {
        SparceMatrix<Boolean> subset = new SparceMatrix<>(false);

        // If sum is 0, then answer is true
        for (int i = 0; i <= n; i++)
            subset.set(0, i, true);

        // If sum is not 0 and set is empty,
        // then answer is false
        for (int i = 1; i <= sum; i++)
            subset.set(i, 0, true);

        // Fill the subset table in botton
        // up manner
        for (int i = 1; i <= sum; i++) {
            for (int j = 1; j <= n; j++) {
                subset.set(i, j, subset.get(i, j - 1));
                if (i >= types.get(j - 1).nbSlices)
                    subset.set(i, j, subset.get(i, j) || subset.get(i - types.get(j - 1).nbSlices, j - 1));
            }
        }

        return subset.get(sum, n);
    }


    public void readInput(String filePath) throws IOException {
        String fileName = Paths.get(filePath).getFileName().toString().replaceFirst("[.][^.]+$", "");

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            String[] values = line.split("\\s+");

            M = Integer.parseInt(values[0]);
            N = Integer.parseInt(values[1]);

            line = br.readLine();
            values = line.split("\\s+");
            for (int i = 0; i < N; i++) {
                types.add(new Pizza(i, Integer.parseInt(values[i])));
            }

        }
    }

    private void writeResults(String filePath) throws FileNotFoundException, UnsupportedEncodingException  // ignore this
    {
        PrintWriter writer = new PrintWriter(filePath + ".out", "UTF-8");
        writer.print("" + choosenTypes.size() + "\n");
        for (Pizza pizza : choosenTypes) {
            writer.print(pizza.index + " ");
        }
        writer.close();
    }
}