package fr.omny.flow.api.utils.random;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import fr.omny.flow.api.utils.tuple.Tuple;
import fr.omny.flow.api.utils.tuple.Tuple2;

public class RandomUtils {

	/**
	 * @param <T>
	 * @param collection
	 * @return
	 */
	public static <T extends WeightedEntry> double getTotalProbability(Collection<T> collection) {
		return collection.stream().mapToDouble(WeightedEntry::getWeight).sum();
	}

	/**
	 * @param <T>
	 * @param collection
	 * @return
	 * @throws NoSuchElementException
	 */
	public static <T extends WeightedEntry> T getWeightedEntry(Collection<T> collection) throws NoSuchElementException {
		return getWeightedEntry(ThreadLocalRandom.current(), collection);
	}

	/**
	 * @param <T>
	 * @return
	 */
	public static <T extends WeightedEntry> Optional<T> getWeightedEntryOptional(Random random,
			Collection<T> collection) {
		try {
			return Optional.of(getWeightedEntry(random, collection));
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	/**
	 * @param <T>
	 * @param random
	 * @param collection
	 * @return
	 * @throws NoSuchElementException
	 */
	public static <T extends WeightedEntry> T getWeightedEntry(Random random, Collection<T> collection)
			throws NoSuchElementException {
		if (collection.isEmpty()) {
			throw new NoSuchElementException();
		}
		List<T> sorted = new ArrayList<>(collection);
		Collections.sort(sorted);
		double probabilitySum = getTotalProbability(collection);

		List<Tuple2<T, Double>> list = new ArrayList<>();

		double start = 0;
		double randomIndex = start;

		for (T type : sorted) {
			double currIndex = randomIndex + (type.getWeight() / probabilitySum);
			list.add(Tuple.of(type, currIndex));
			randomIndex = currIndex;
		}
		double randomValue = random.nextDouble();
		for (var pair : list) {
			if (pair.getValue() >= randomValue) {
				return pair.getKey();
			}
		}
		return sorted.get(0);

	}

	/**
	 * @param <T>
	 * @param random
	 * @param collection
	 * @param amount
	 * @return
	 * @throws NoSuchElementException
	 */
	public static <T extends WeightedEntry> List<T> getWeightedEntry(Random random, Collection<T> collection, int amount)
			throws NoSuchElementException {
		if (collection.isEmpty()) {
			throw new NoSuchElementException();
		}
		List<T> sorted = new ArrayList<>(collection);
		Collections.sort(sorted);
		double probabilitySum = getTotalProbability(collection);

		List<Tuple2<T, Double>> list = new ArrayList<>();

		double start = 0;
		double randomIndex = start;

		for (T type : sorted) {
			double currIndex = randomIndex + (type.getWeight() / probabilitySum);
			list.add(Tuple.of(type, currIndex));
			randomIndex = currIndex;
		}
		List<T> generatedWeightedEntries = new ArrayList<>();
		MainLoop: for (int i = 0; i < amount; i++) {
			double randomValue = random.nextDouble();
			for (var pair : list) {
				if (pair.getValue() >= randomValue) {
					generatedWeightedEntries.add(pair.getKey());
					continue MainLoop;
				}
			}
			generatedWeightedEntries.add(sorted.get(0));
		}
		return generatedWeightedEntries;
	}

	/**
	 * 
	 * @param <T>
	 * @param random
	 * @param collection
	 * @param amount
	 * @return
	 */
	public static <T extends WeightedEntry> List<T> getWeightedEntryUnique(Random random, Collection<T> collection,
			int amount) {
		if (collection.isEmpty()) {
			throw new NoSuchElementException();
		}
		List<T> sorted = new ArrayList<>(collection);
		Collections.sort(sorted);
		double probabilitySum = getTotalProbability(collection);

		List<Tuple2<T, Double>> list = new ArrayList<>();

		double start = 0;
		double randomIndex = start;

		for (T type : sorted) {
			double currIndex = randomIndex + (type.getWeight() / probabilitySum);
			list.add(Tuple.of(type, currIndex));
			randomIndex = currIndex;
		}
		List<T> generatedWeightedEntries = new ArrayList<>();
		MainLoop: while (generatedWeightedEntries.size() < amount) {
			double randomValue = random.nextDouble();
			for (var pair : list) {
				if (pair.getValue() >= randomValue
						&& !generatedWeightedEntries.contains(pair.getKey())) {
					generatedWeightedEntries.add(pair.getKey());
					continue MainLoop;
				}
			}
			generatedWeightedEntries.add(sorted.get(0));
		}
		return generatedWeightedEntries;
	}

}
