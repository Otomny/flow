package fr.omny.flow.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

	@BeforeEach
	public void setupForEach() {
		Injector.startTest();
	}

	@AfterEach
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
