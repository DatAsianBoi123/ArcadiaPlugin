package com.datasiqn.arcadia.rand;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2DoubleLinkedOpenHashMap;
import org.jetbrains.annotations.NotNull;

public class WeightedRandom<T> {
    private final Object2DoubleLinkedOpenHashMap<T> weights;
    private final double[] probabilities;
    private final int[] alias;

    public WeightedRandom(@NotNull Object2DoubleLinkedOpenHashMap<T> weights) {
        this.weights = weights;

        int size = weights.size();
        double total = weights.values().doubleStream().sum();
        probabilities = new double[size];
        alias = new int[size];

        IntList small = new IntArrayList();
        IntList large = new IntArrayList();
        {
            int i = 0;
            for (double weight : weights.values()) {
                double mappedVal = weight * (size / total);
                probabilities[i] = mappedVal;
                if (mappedVal < 1) small.add(i);
                else large.add(i);
                i++;
            }
        }

        while (!small.isEmpty() && !large.isEmpty()) {
            alias[small.getInt(0)] = large.getInt(0);
            probabilities[large.getInt(0)] += probabilities[small.getInt(0)] - 1;
            small.removeInt(0);
            if (probabilities[large.getInt(0)] > 1) large.add(large.removeInt(0));
            else if (probabilities[large.getInt(0)] < 1) small.add(large.removeInt(0));
        }

        while (!small.isEmpty()) {
            probabilities[small.removeInt(0)] = 1;
        }

        while (!large.isEmpty()) {
            probabilities[large.removeInt(0)] = 1;
        }
    }

    public int generateRandomIndex() {
        int random = (int) (Math.random() * weights.size());
        int i;
        if (Math.random() < probabilities[random]) i = random;
        else i = alias[random];
        return i;
    }

    public T generateRandom() {
        return weights.keySet().stream().skip(generateRandomIndex()).findFirst().orElseThrow();
    }
}
