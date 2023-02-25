package fr.omny.flow.repository;

import java.util.List;

import org.bson.Document;
import org.junit.jupiter.api.Test;

import fr.omny.flow.data.mongodb.MongoQuery;

public class MongoRepositoryTest {

  @Test
  public void test() {}

  @MongoQuery(fields = "{'name': 1}", value = "")
  public List<Document> list(String name) {
    return null;}
}
