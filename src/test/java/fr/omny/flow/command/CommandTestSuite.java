package fr.omny.flow.command;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
		SingleArgumentCommandTest.class, MultipleArgumentCommandTest.class, OptionalCommandTest.class,
		TabCompleteCommandTest.class, SubCommandTest.class })
public class CommandTestSuite {

}
