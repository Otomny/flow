package fr.omny.flow.api.repository;

// @Testcontainers
public class MongoTest {

	// @Container
	// final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:5.0.15"));



	// MongoClient mongoClient;

	// @BeforeAll
	// public void setup() {
	// 	mongoDBContainer.start();
	// 	var host = mongoDBContainer.getHost();
	// 	var port = mongoDBContainer.getFirstMappedPort();

	// 	String uri = "mongodb://" + host + ":" + port + "/test";
	// 	mongoClient = MongoClients.create(uri);
	// }

	// @AfterAll
	// public void tearDown() {
	// 	mongoDBContainer.stop();
	// }

	// @BeforeEach
	// public void setupEach() {
	// 	Injector.startTest();
	// 	Injector.addService(MongoClient.class, mongoClient);
	// 	Injector.addService(String.class, "databaseName", "test");
	// }

	// @AfterEach
	// public void tearDownEach() {
	// 	Injector.wipeTest();
	// }

}
