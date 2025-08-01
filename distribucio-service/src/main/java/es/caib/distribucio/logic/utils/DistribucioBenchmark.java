package es.caib.distribucio.logic.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
public class DistribucioBenchmark {

    private List<Integer> numbers;

    @Setup
    public void setUp() {
        // Inicialitza amb una llista de números aleatoris
        numbers = generateRandomNumbers(1000);
    }

    // TODO: Fer feina amb dades reals
    private List<Integer> generateRandomNumbers(int size) {
        List<Integer> list = new ArrayList<>(size);
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            list.add(random.nextInt());
        }
        return list;
    }

    public void performTask() {
        // Còpia la llista per a cada execució del benchmark
        List<Integer> sortedList = new ArrayList<>(numbers);
        Collections.sort(sortedList); // Ordena la llista
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(1)
    public void testPerformTask() {
        performTask();
    }

}
