package fr.omny.flow.command;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.omny.flow.commands.Cmd;
import fr.omny.flow.commands.SubCmd;
import fr.omny.flow.commands.arguments.DoubleArgument;
import fr.omny.flow.commands.arguments.IntegerArgument;
import fr.omny.flow.commands.wrapper.Arguments;
import fr.omny.flow.entity.DummyCommandSender;
import fr.omny.odi.Injector;
import lombok.Getter;

public class TabCompleteCommandTest {

	@BeforeEach
	public void setupForEach() {
		Injector.startTest();
	}

	@AfterEach
	public void tearDownForEach() {
		Injector.wipeTest();
	}

	@Test
	public void test_TabComplete_NoArguments() {

		var sender = new DummyCommandSender();
		var cmd = new DummyCmd();
		var result = cmd.tabComplete(sender, cmd.getName(), new String[] {});
		assertFalse(result.isEmpty());
		assertEquals(4, result.size());
		assertTrue(result.contains("1.0"));
		assertTrue(result.contains("-1.0"));
		assertTrue(result.contains("0.0"));
		assertTrue(result.contains("setcount"));
	}

	@Test
	public void test_TabComplete_StartArguments() {

		var sender = new DummyCommandSender();
		var cmd = new DummyCmd();
		var result = cmd.tabComplete(sender, cmd.getName(), new String[] {
				"s" });
		assertFalse(result.isEmpty());
		assertEquals(1, result.size());
		assertTrue(result.contains("setcount"));

	}

	@Test
	public void test_TabComplete_EndArguments() {

		var sender = new DummyCommandSender();
		var cmd = new DummyCmd();
		var result = cmd.tabComplete(sender, cmd.getName(), new String[] {
				"setcount" });
		assertFalse(result.isEmpty());
		assertFalse(result.isEmpty());
		assertEquals(3, result.size());
		assertTrue(result.contains("1"));
		assertTrue(result.contains("-1"));
		assertTrue(result.contains("0"));

	}

	@Test
	public void test_TabComplete_StartArgument_SubCmd() {

		var sender = new DummyCommandSender();
		var cmd = new DummyCmd();
		var result = cmd.tabComplete(sender, cmd.getName(), "setcount ".split("\\s+"));
		assertFalse(result.isEmpty());
		assertEquals(3, result.size());
		assertTrue(result.contains("1"));
		assertTrue(result.contains("-1"));
		assertTrue(result.contains("0"));
	}

	@Getter
	public static class DummyCmd extends Cmd {

		private Arguments args;
		private DummySubCmd subCmd;

		public DummyCmd() {
			super("dummy");
			this.subCmd = new DummySubCmd();
			rc(0, new DoubleArgument("Percentage", false));
			rc(0, subCmd);
		}

		@Override
		public void execute(CommandSender sender, Arguments args) {
			this.args = args;
		}

	}

	@Getter
	public static class DummySubCmd extends SubCmd {

		private Arguments args;

		public DummySubCmd() {
			super("setcount", false);
			rc(0, new IntegerArgument("Count", false));
		}

		@Override
		public void execute(CommandSender sender, Arguments args) {
			this.args = args;
		}

	}

}
