package fr.omny.flow.command;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.omny.flow.commands.Cmd;
import fr.omny.flow.commands.arguments.IntegerArgument;
import fr.omny.flow.commands.arguments.SentenceArgument;
import fr.omny.flow.commands.arguments.StringArgument;
import fr.omny.flow.commands.wrapper.Arguments;
import fr.omny.flow.entity.DummyCommandSender;
import fr.omny.odi.Injector;
import lombok.Getter;

public class SentenceArgumentTest {

	@BeforeEach
	public void setupForEach() {
		Injector.startTest();
	}

	@AfterEach
	public void tearDownForEach() {
		Injector.stopTest();
	}

	@Test
	public void test_ExecuteCommand_NoArguments() {
		var sender = new DummyCommandSender();
		var cmd = new DummyCommand();
		var result = cmd.execute(sender, "dummy", new String[] {});
		assertFalse(result);
		assertNull(cmd.args);
		assertEquals("§cUsage: /dummy <Count> <String> <Sentence>", sender.getReceivedMessages().get(0));
	}

	@Test
	public void test_ExecuteCommand_Arguments() {
		var sender = new DummyCommandSender();
		var cmd = new DummyCommand();
		var result = cmd.execute(sender, "dummy", "51 tree Hello world !".split("\\s+"));
		assertTrue(result);
		assertEquals(3, cmd.getArgs().count());
		assertTrue(cmd.getArgs().isPresent(0, Integer.class));
		assertTrue(cmd.getArgs().isPresent(1, String.class));
		assertTrue(cmd.getArgs().isPresent(2, String.class));
		assertEquals(Integer.valueOf(51), cmd.getArgs().get(0, Integer.class));
		assertEquals("tree", cmd.getArgs().get(1, String.class));
		assertEquals("Hello world !", cmd.getArgs().get(2, String.class));
	}

	public static class DummyCommand extends Cmd {

		@Getter
		private Arguments args;

		public DummyCommand() {
			super("dummy");
			rc(0, new IntegerArgument("Count", false));
			rc(1, new StringArgument("String", false));
			rc(2, new SentenceArgument("Sentence", false));
		}

		@Override
		public void execute(CommandSender sender, Arguments args) {
			this.args = args;
		}

	}

}
