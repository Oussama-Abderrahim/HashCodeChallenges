package morepizza;

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

    public void estimate()
    {
        /// TODO: try more methods
        estimateGreedy();
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