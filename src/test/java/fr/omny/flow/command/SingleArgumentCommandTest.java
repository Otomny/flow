package fr.omny.flow.command;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.bukkit.command.CommandSender;
import org.junit.Test;

import fr.omny.flow.commands.Cmd;
import fr.omny.flow.commands.arguments.IntegerArgument;
import fr.omny.flow.commands.wrapper.Arguments;
import fr.omny.flow.entity.DummyCommandSender;
import lombok.Getter;

public class SingleArgumentCommandTest {

	@Test
	public void test_ExecuteCommand_NoArguments() {
		var sender = new DummyCommandSender();
		var cmd = new DummyCommand();
		var result = cmd.execute(sender, "dummy", new String[] {});
		assertFalse(result);
		assertEquals("", sender.getReceivedMessages().get(0));
	}

	@Test
	public void test_ExecuteCommand_Arguments() {
		var sender = new DummyCommandSender();
		var cmd = new DummyCommand();
		var result = cmd.execute(sender, "dummy", new String[] {
				"51" });
		assertTrue(result);
		assertTrue(cmd.getArgs().isPresent(0, Integer.class));
		assertEquals(Integer.valueOf(51), cmd.getArgs().get(0, Integer.class));
	}

	public static class DummyCommand extends Cmd {

		@Getter
		private Arguments args;

		public DummyCommand() {
			super("dummy");
			rc(0, new IntegerArgument("Count", false));
		}

		@Override
		public void execute(CommandSender sender, Arguments args) {
			this.args = args;
		}

	}

}
