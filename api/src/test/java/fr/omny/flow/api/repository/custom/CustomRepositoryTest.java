package fr.omny.flow.api.repository.custom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.Filters;

import fr.omny.flow.api.aop.MongoRepositoryProxy;
import fr.omny.flow.api.data.Id;
import fr.omny.flow.api.data.Repository;
import fr.omny.flow.api.data.RepositoryFactory;
import fr.omny.flow.api.data.Val;
import fr.omny.flow.api.utils.tuple.Tuple2;
import fr.omny.odi.Injector;
import fr.omny.odi.Utils;
import lombok.Getter;
import lombok.Setter;

@Testcontainers
@Execution(ExecutionMode.SAME_THREAD)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class CustomRepositoryTest {

	@Container
	static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:5.0.15"));

	static MongoClient mongoClient;

	@BeforeAll
	public static void setup() {
		mongoDBContainer.start();
		var host = mongoDBContainer.getHost();
		var port = mongoDBContainer.getFirstMappedPort();

		String uri = "mongodb://" + host + ":" + port + "/test";
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
				CodecRegistries.fromProviders(PojoCodecProvider.builder().register(Data.class)
						.build()),
				MongoClientSettings.getDefaultCodecRegistry());

		mongoClient = MongoClients.create(MongoClientSettings.builder().uuidRepresentation(UuidRepresentation.STANDARD)
				.codecRegistry(codecRegistry).applyConnectionString(new ConnectionString(uri)).build());
	}

	@AfterAll
	public static void tearDown() {
		mongoDBContainer.stop();
	}

	@BeforeEach
	public void setupEach() {
		Injector.startTest();
		Injector.addService(MongoClient.class, mongoClient);
		Injector.addService(String.class, "databaseName", "test");
		Injector.addService(RepositoryFactory.class, new RepositoryFactory());

		Injector.getService(RepositoryFactory.class)
				.registerFactory(CustomRepository.class, (repositoryClass) -> {
					Tuple2<Class<?>, Class<?>> typeAndId = RepositoryFactory.findTypeAndId(repositoryClass,
							CustomRepository.class);
					var typeClass = typeAndId.getKey();
					var idClass = typeAndId.getValue();
					try {
						CustomRepository<?, ?> customRepository = Utils.callConstructor(
								CustomMongoDBRepository.class, false, typeClass, idClass, "ratio",
								RepositoryFactory.mappingFactory(repositoryClass, CustomRepository.class));
						return MongoRepositoryProxy.createRepositoryProxy(
								repositoryClass, typeClass,
								customRepository);
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e) {
						throw new RuntimeException(e);
					}

				});

		DataRepository dataRepository = Injector.getService(RepositoryFactory.class)
				.createRepository(DataRepository.class);
		dataRepository.deleteAll();
		Injector.addService(DataRepository.class, dataRepository, true);
	}

	@AfterEach
	public void tearDownEach() {
		// DataRepository dataRepository = Injector.getService(DataRepository.class);
		// dataRepository.deleteAll();
		Injector.wipeTest();
	}

	@Test
	public void test_Create_Read_Data() {
		DataRepository dataRepository = Injector.getService(DataRepository.class);
		dataRepository.save(new Data("test", 64, 0.2));

		assertTrue(dataRepository.findById("test").isPresent());

		Data data = dataRepository.findById("test").get();

		assertEquals(64, data.getQuantity());
		assertEquals(0.2, data.getPercentage());
	}

	@Test
	public void test_Update_Data() {
		DataRepository dataRepository = Injector.getService(DataRepository.class);
		dataRepository.save(new Data("test", 64, 0.2));

		assertTrue(dataRepository.findById("test").isPresent());

		Data data = dataRepository.findById("test").get();

		assertEquals(64, data.getQuantity());
		assertEquals(0.2, data.getPercentage());

		data.setPercentage(0.4);
		dataRepository.save(data);

		assertTrue(dataRepository.findById("test").isPresent());
		data = dataRepository.findById("test").get();
		assertEquals(0.4, data.getPercentage());
	}

	@Test
	public void test_Delete_Data() {
		DataRepository dataRepository = Injector.getService(DataRepository.class);
		dataRepository.save(new Data("test", 64, 0.2));

		assertTrue(dataRepository.findById("test").isPresent());
		assertEquals(1, dataRepository.count());

		Data data = dataRepository.findById("test").get();

		assertEquals(64, data.getQuantity());
		assertEquals(0.2, data.getPercentage());

		dataRepository.delete(data);

		assertEquals(0, dataRepository.count());
		assertTrue(dataRepository.findById("test").isEmpty());
	}

	@Test
	public void test_Delete_Data_ById() {
		DataRepository dataRepository = Injector.getService(DataRepository.class);
		dataRepository.save(new Data("test", 64, 0.2));

		assertTrue(dataRepository.findById("test").isPresent());
		assertEquals(1, dataRepository.count());

		Data data = dataRepository.findById("test").get();

		assertEquals(64, data.getQuantity());
		assertEquals(0.2, data.getPercentage());

		dataRepository.deleteById("test");

		assertEquals(0, dataRepository.count());
		assertTrue(dataRepository.findById("test").isEmpty());
	}

	@Test
	public void test_Find_WithRaw_Bson() {
		DataRepository dataRepository = Injector.getService(DataRepository.class);
		dataRepository.save(new Data("test", 64, 0.2));

		assertTrue(dataRepository.findById("test").isPresent());
		assertEquals(1, dataRepository.count());

		List<Data> result = dataRepository.executeQuery(Filters.eq("quantity", 64), new Document());

		assertEquals(1, result.size());

		Data data = result.get(0);

		assertEquals("test", data.getId());
		assertEquals(64, data.getQuantity());
		assertEquals(0.2, data.getPercentage());
	}

	@Test
	public void test_Find_With_CustomQuery() {
		DataRepository dataRepository = Injector.getService(DataRepository.class);
		dataRepository.save(new Data("test", 64, 0.2));

		assertTrue(dataRepository.findById("test").isPresent());
		assertEquals(1, dataRepository.count());

		Optional<Data> result = dataRepository.findByQuantity(64);

		assertTrue(result.isPresent());

		Data data = result.get();

		assertEquals("test", data.getId());
		assertEquals(64, data.getQuantity());
		assertEquals(0.2, data.getPercentage());
	}

	@Test
	public void test_Find_With_CustomQuery_Multiple() {
		DataRepository dataRepository = Injector.getService(DataRepository.class);
		dataRepository.save(new Data("test", 64, 0.2));
		dataRepository.save(new Data("test2", 32, 0.2));

		assertTrue(dataRepository.findById("test").isPresent());
		assertTrue(dataRepository.findById("test2").isPresent());
		assertEquals(2, dataRepository.count());

		List<Data> result = StreamSupport.stream(dataRepository.findAllByPercentage(0.2).spliterator(), false).toList();

		assertEquals(2, result.size());

		assertEquals("test", result.get(0).getId());
		assertEquals(64, result.get(0).getQuantity());
		assertEquals(0.2, result.get(0).getPercentage());

		assertEquals("test2", result.get(1).getId());
		assertEquals(32, result.get(1).getQuantity());
		assertEquals(0.2, result.get(1).getPercentage());
	}

	@Repository
	public static interface DataRepository extends CustomRepository<Data, String> {

		Optional<Data> findByQuantity(int quantity);

		Iterable<Data> findAllByPercentage(double percentage);

	}

	@Getter
	@Setter
	public static class Data {

		@BsonId
		@Id
		private String id;

		@BsonProperty
		@Val
		private int quantity;

		@BsonProperty
		@Val
		private double percentage;

		/**
		 * 
		 */
		public Data() {
		}

		/**
		 * @param id
		 * @param quantity
		 * @param percentage
		 */
		public Data(String id, int quantity, double percentage) {
			this.id = id;
			this.quantity = quantity;
			this.percentage = percentage;
		}

	}

}
