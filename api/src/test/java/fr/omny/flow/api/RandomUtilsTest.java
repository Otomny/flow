package fr.omny.flow.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import fr.omny.flow.api.utils.NumberUtils;
import fr.omny.flow.api.utils.random.RandomUtils;
import fr.omny.flow.api.utils.random.WeightedEntry;

public class RandomUtilsTest {

	public RandomRecord r(String s, double w) {
		return new RandomRecord(s, w);
	}

	@Test
	public void test_RandomUtils_Collection() {
		List<RandomRecord> records = List
				.of(r("A", 0.1), r("B", 0.2), r("C", 0.3), r("D", 0.4), r("E", 0.5), r("F", 0.6), r("G", 0.7), r("H", 0.8),
						r("I", 0.9), r("J", 1.0));

		final double totalProbability = RandomUtils.getTotalProbability(records);
		final double maxRoll = 10_000_000;

		assertEquals(5.5D, totalProbability, 0);
		assertEquals(0.01818D, get(records, "A").weight / totalProbability, 0.001);
		assertEquals(0.03636D, get(records, "B").weight / totalProbability, 0.001);
		assertEquals(0.09090D, get(records, "E").weight / totalProbability, 0.001);
		assertEquals(0.12727D, get(records, "G").weight / totalProbability, 0.001);

		Map<RandomRecord, AtomicInteger> map = new HashMap<>();
		records.forEach(r -> {
			map.put(r, new AtomicInteger(0));
		});

		for (int i = 0; i < maxRoll; i++) {
			var generated = RandomUtils
					.getWeightedEntry(NumberUtils.random(), records);
			map.get(generated).incrementAndGet();
		}

		for (var entry : map.entrySet()) {
			double count = entry.getValue().doubleValue();
			double realPercentage = count / maxRoll;
			double expectedPercentage = entry.getKey().weight()
					/ totalProbability;
			assertEquals(expectedPercentage, realPercentage, 0.01);
		}

	}

	public RandomRecord get(List<RandomRecord> records, String name) {
		return records.stream().filter(r -> r.name.equals(name)).findFirst().get();
	}

	@Test
	public void test_RandomUtils_Collection_Unique() {
		List<RandomRecord> records = List.of(r("A", 0.1), r("B", 0.2), r("C", 0.3), r("D", 0.4), r("E", 0.5), r("F", 0.6),
				r("G", 0.7), r("H", 0.8),
				r("I", 0.9), r("J", 1.0));

		for (int i = 0; i < 100_000; i++) {
			List<RandomRecord> generated = RandomUtils.getWeightedEntryUnique(ThreadLocalRandom.current(), records, 2);
			assertEquals(generated.size(), new HashSet<>(generated).size());
		}
	}

	/**
	 * RandomRecord
	 */
	public record RandomRecord(String name, double weight) implements WeightedEntry {

		@Override
		public double getWeight() {
			return weight;
		}

	}

}
