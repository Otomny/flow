package fr.omny.flow.command;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
   OptionalCommandTest.class,
   SingleArgumentCommandTest.class,
	 SubCommandTest.class
})
public class CommandTestSuite {
	
}
