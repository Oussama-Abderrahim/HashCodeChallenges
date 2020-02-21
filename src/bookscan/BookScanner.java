package bookscan;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;

class BookScanner {

    private static int score_total = 0;
    private static int test_case = 0;

    private static final int[] best_scores = new int[]{21, 5822900, 5689135, 5028010, 4938762, 5330120};

    private static final String PROBLEM_NAME = "bookscan";
    private static final String INPUTS_DIR = "./res/" + PROBLEM_NAME + "/inputs/";
    private static final String OUTPUTS_DIR = "./res/" + PROBLEM_NAME + "/outputs/";

    private static final String[] inputs = new String[]{
            "a_example.txt",
            "b_read_on.txt",
            "c_incunabula.txt",
            "d_tough_choices.txt",
            "e_so_many_books.txt",
            "f_libraries_of_the_world.txt"
    };

    private int B; // the number of different books
    private int L; // the number of libraries
    private int D; // the number of days

    private ArrayList<Book> books; // the index and score of each book
    private ArrayList<Library> libraries;

    private ArrayList<Library> choosenLibraries;    // in order
    private int largestBookScore;


    public static void main(String[] args) throws IOException {
        BookScanner solver = new BookScanner();
        score_total = 0;
        test_case = 0;

        for (String testFileName : inputs) {
            solver.reset();
            solver.readInput(INPUTS_DIR + testFileName);

            solver.solveLast();

            solver.computeScore(testFileName);
            solver.writeResults(OUTPUTS_DIR + testFileName.replaceAll("\\.in$", ""));
        }
        System.out.println("Total score : " + score_total);
    }

    public BookScanner() {
        this.reset();
    }

    public void reset() {
        /// TODO : reset
        this.B = 0;
        this.L = 0;
        this.D = 0;
        this.books = new ArrayList<>();
        this.libraries = new ArrayList<>();
        this.choosenLibraries = new ArrayList<>();
        this.largestBookScore = 1;
    }

    private void computeScore(String filename) {
        int score = 0;

        ArrayList<Integer> scannedBooksCount = new ArrayList<>();

        for (Library lib : choosenLibraries) {
            for (Book book : lib.scannedBooks) {
                if (!scannedBooksCount.contains(book.index)) {
                    scannedBooksCount.add(book.index);
                    score += book.score;
                }
            }
        }

        System.out.print("Score for " + filename + " is : " + score);
        if (score > best_scores[test_case]) System.out.println(" UP");
        else if (score < best_scores[test_case]) System.out.println(" DOWN");
        else System.out.println(" SAME");

        score_total += score;
        test_case++;
    }

    public void removeFromAllLibraries(ArrayList<Book> books, Library excludedLibrary) {
        for (Library lib : libraries)
            if (lib != excludedLibrary)
                for (Book b : books) {
                    lib.removeBook(b);
                    if (b.score > largestBookScore)
                        largestBookScore = b.score;
                }
    }

    public void solveLast() {

        /* Remove ALL duplicates and sort Books by score */
        libraries.sort(Comparator.comparingDouble(l -> -1 * l.getProfitPerDayRatio(D - 1)));
        for (Library library : libraries) {
            removeFromAllLibraries(library.libraryBooks, library);
            library.libraryBooks.sort(Comparator.comparingInt(o -> -1 * o.score));
        }
        int day = 0;

        while (!libraries.isEmpty() && day < D) {
            // Compute ratio for each library (without counting already scanned books)
            int finalDay = day;

            libraries.sort(Comparator.comparingDouble(l -> -1 * l.getProfitPerDayRatio(D - finalDay - 1)));

            // Add the highest score library
            if (libraries.get(0).T + finalDay < D && !libraries.get(0).libraryBooks.isEmpty()) {
                int x = libraries.get(0).registerAndScan(day);
                if (x > day) {
                    day += libraries.get(0).T;
                    choosenLibraries.add(libraries.get(0));
                    removeFromAllLibraries(libraries.get(0).scannedBooks, null);
                }
            }
            libraries.remove(0);
        }

    }


    public void readInput(String filePath) throws IOException {
        String fileName = Paths.get(filePath).getFileName().toString().replaceFirst("[.][^.]+$", "");

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            String[] values = line.split("\\s+");

            B = Integer.parseInt(values[0]);
            L = Integer.parseInt(values[1]);
            D = Integer.parseInt(values[2]);

            line = br.readLine();
            values = line.split("\\s+");
            for (int i = 0; i < values.length; i++) {
                books.add(new Book(i, Integer.parseInt(values[i])));
            }

            // Read L sections
            for (int l = 0; l < L; l++) {
                // Read first line Nj Tj Mj
                line = br.readLine();
                values = line.split("\\s+");
                Library lib = new Library(l, Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2]));

                // Read second line ( Nj books )
                line = br.readLine();
                values = line.split("\\s+");
                for (int i = 0; i < values.length; i++) {
                    lib.addBook(Integer.parseInt(values[i]));
                }
                libraries.add(lib);
            }

        }
    }

    private void writeResults(String filePath) throws FileNotFoundException, UnsupportedEncodingException  // ignore this
    {
        PrintWriter writer = new PrintWriter(filePath + ".out", "UTF-8");
        writer.print(choosenLibraries.size() + "\n");

        int day = 0;
        for (Library lib : choosenLibraries) {
            writer.print(lib.index + " " + lib.scannedBooks.size() + "\n");
            for (Book book : lib.scannedBooks) {
                writer.print(book.index + " ");
            }
            writer.println();
        }
        writer.close();
    }

    private class Book {
        int index;
        int score;

        public Book(int i, int s) {
            this.index = i;
            this.score = s;
        }
    }

    private class Library {
        int index;
        int N;  // number of books in lib
        int T;  // number of days to finish signup
        int M;  // number of books that can be shipped per day
        int maxScore = 0;

        ArrayList<Book> libraryBooks;
        ArrayList<Book> scannedBooks;

        public Library(int i, int N, int T, int M) {
            this.index = i;
            this.N = N;
            this.T = T;
            this.M = M;
            this.maxScore = 0;

            libraryBooks = new ArrayList<>();
            scannedBooks = new ArrayList<>();
        }

        public int getLatestDay() {
            return D - 1 - T - N / M;
        }

        public void addBook(int index) {
            this.maxScore += books.get(index).score;
            this.libraryBooks.add(books.get(index));
        }

        public double getProfitPerDayRatio(int remainingDay) {
            if (remainingDay - T <= 0 || libraryBooks.size() == 0) return 0;
            double s = 0;
            for (int i = 0; i < Math.min(M * (remainingDay - T), N); i++) {
                s += libraryBooks.get(i).score;
            }

            return s / T;
        }

        public int registerAndScan(int day) {

            int start = day;
            day += T;
            int i = 0;
            for (day = day; day < D && i < this.libraryBooks.size(); day++) {
                // add todays books
                for (int m = 0; m < M && i < this.libraryBooks.size(); m++) {
                    this.scannedBooks.add(this.libraryBooks.get(i));
                    i++;
                }
            }

            if (this.scannedBooks.size() <= 0) // in case we scanned nothing
                return start;
            return start + T;
        }

        public void removeBook(Book book) {
            this.libraryBooks.remove(book);
            this.maxScore -= book.score;
            N -= 1;
        }
    }
}