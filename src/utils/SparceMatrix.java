package utils;

import java.util.HashMap;

public class SparceMatrix<T> {

    HashMap<Integer, HashMap<Integer, T>> matrix;
    private T defaultValue;


    public SparceMatrix(T defaultValue) {
        matrix = new HashMap<>();
        this.defaultValue = defaultValue;
    }


    public void set(int i, int j, T v) {
        this.getLine(i).put(j, v);
    }

    public T get(int i, int j) {
        return this.getLine(i).computeIfAbsent(j, k -> defaultValue);
    }

    private HashMap<Integer, T> getLine(int i)
    {
        return this.matrix.computeIfAbsent(i, k -> new HashMap<>());
    }
}
