package fr.omny.flow.api.utils.random;

/**
 * 
 */
public interface WeightedEntry extends Comparable<WeightedEntry> {

	/**
	 * 
	 * @return The weight of this entry
	 */
	double getWeight();

	@Override
	default int compareTo(WeightedEntry entry) {
		return Double.valueOf(this.getWeight() - entry.getWeight()).intValue();
	}

}
