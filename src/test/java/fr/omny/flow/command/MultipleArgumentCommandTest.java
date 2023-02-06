package fr.omny.flow.command;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.omny.flow.commands.Cmd;
import fr.omny.flow.commands.arguments.DoubleArgument;
import fr.omny.flow.commands.arguments.EnumArgument;
import fr.omny.flow.commands.arguments.EnumStringArgument;
import fr.omny.flow.commands.arguments.IntegerArgument;
import fr.omny.flow.commands.wrapper.Arguments;
import fr.omny.flow.entity.DummyCommandSender;
import fr.omny.odi.Injector;
import lombok.Getter;

public class MultipleArgumentCommandTest {

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
		assertEquals("§cUsage: /dummy <Count> <(Percentage|Mask)> <Material> <Type>", sender.getReceivedMessages().get(0));
	}

	public static class DummyCommand extends Cmd {

		@Getter
		private Arguments args;

		public DummyCommand() {
			super("dummy");
			rc(0, new IntegerArgument("Count", false));
			rc(1, new DoubleArgument("Percentage", false));
			rc(1, new IntegerArgument("Mask", false));
			rc(2, new EnumArgument<Material>(Material.class, false));
			rc(3, new EnumStringArgument("Type", false, "type1", "type2"));
		}

		@Override
		public void execute(CommandSender sender, Arguments args) {
			this.args = args;
		}

	}

}
