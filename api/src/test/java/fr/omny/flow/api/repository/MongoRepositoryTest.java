package fr.omny.flow.api.repository;

import java.util.List;

import org.bson.Document;
import org.junit.jupiter.api.Test;

import fr.omny.flow.api.data.query.MongoQuery;

public class MongoRepositoryTest {

	@Test
	public void test() {
	}

	@MongoQuery(fields = "{'name': 1}", value = "")
	public List<Document> list(String name) {
		return null;
	}
}
