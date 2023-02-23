package fr.omny.flow.utils;

import java.util.ArrayList;
import java.util.List;

public final class ListUtils {

	ListUtils() {
	}

	@SafeVarargs
	public static <T> List<T> concat(List<T>... lists) {
		List<T> finalList = new ArrayList<>();
		for (var list : lists) {
			finalList.addAll(list);
		}
		return finalList;
	}

}
