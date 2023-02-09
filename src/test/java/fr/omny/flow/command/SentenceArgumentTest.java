package fr.omny.flow.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.bukkit.command.CommandSender;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.omny.flow.commands.Cmd;
import fr.omny.flow.commands.arguments.IntegerArgument;
import fr.omny.flow.commands.arguments.SentenceArgument;
import fr.omny.flow.commands.wrapper.Arguments;
import fr.omny.flow.entity.DummyCommandSender;
import fr.omny.odi.Injector;
import lombok.Getter;

public class SentenceArgumentTest {
	
	@Before
	public void setupForEach() {
		Injector.startTest();
	}

	@After
	public void tearDownForEach() {
		Injector.wipeTest();
	}

	@Test
	public void test_ExecuteCommand_NoArguments() {
		var sender = new DummyCommandSender();
		var cmd = new DummyCommand();
		var result = cmd.execute(sender, "dummy", new String[] {});
		assertFalse(result);
		assertNull(cmd.args);
		assertEquals("§cUsage: /dummy <Count> <Sentence>", sender.getReceivedMessages().get(0));
	}

	@Test
	public void test_ExecuteCommand_Arguments() {
		var sender = new DummyCommandSender();
		var cmd = new DummyCommand();
		var result = cmd.execute(sender, "dummy", "51 Hello world !".split("\\s+"));
		assertTrue(result);
		assertEquals(2, cmd.getArgs().count());
		assertTrue(cmd.getArgs().isPresent(0, Integer.class));
		assertTrue(cmd.getArgs().isPresent(1, String.class));
		assertEquals(Integer.valueOf(51), cmd.getArgs().get(0, Integer.class));
		assertEquals("Hello world !", cmd.getArgs().get(1, String.class));
	}

	public static class DummyCommand extends Cmd {

		@Getter
		private Arguments args;

		public DummyCommand() {
			super("dummy");
			rc(0, new IntegerArgument("Count", false));
			rc(1, new SentenceArgument("Sentence", false));
		}

		@Override
		public void execute(CommandSender sender, Arguments args) {
			this.args = args;
		}

	}

}
