package fr.omny.flow.command;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.omny.flow.commands.Cmd;
import fr.omny.flow.commands.SubCmd;
import fr.omny.flow.commands.arguments.IntegerArgument;
import fr.omny.flow.commands.wrapper.Arguments;
import fr.omny.flow.entity.DummyCommandSender;
import fr.omny.odi.Injector;
import lombok.Getter;

public class SubCommandTest {

	public static boolean SUBCOMMAND_ARGUMENT_OPTIONAL = false;

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
		var cmd = new DummyCommand(false);
		var result = cmd.execute(sender, "dummy", new String[] {});
		assertFalse(result);
		assertNull(cmd.args);
	}

	@Test
	public void test_ExecuteCommand_NoArguments_Optional() {
		var sender = new DummyCommandSender();
		var cmd = new DummyCommand(true);
		var result = cmd.execute(sender, "dummy", new String[] {});
		assertTrue(result);
		assertNotNull(cmd.args);
	}

	@Test
	public void test_ExecuteCommand_IncorrectArguments() {
		var sender = new DummyCommandSender();
		var cmd = new DummyCommand(false);
		var result = cmd.execute(sender, "dummy", new String[] {
				"51" });
		assertFalse(result);
		assertNull(cmd.args);
		assertEquals("§cUsage: /dummy <setdummy>", sender.getReceivedMessages().get(0));
	}

	@Test
	public void test_ExecuteCommand_CorrectArguments() {
		var sender = new DummyCommandSender();
		var cmd = new DummyCommand(false);
		var result = cmd.execute(sender, "dummy", new String[] {
				"setdummy", "51" });
		assertTrue(result);
		assertNull(cmd.args);
		assertNotNull(cmd.getSubCmd().args);
		assertTrue(cmd.getSubCmd().args.isPresent(0, Integer.class));
		assertEquals(Integer.valueOf(51), cmd.getSubCmd().args.get(0, Integer.class));
	}

	@Test
	public void test_ExecuteCommand_CorrectOptionalArguments() {
		SUBCOMMAND_ARGUMENT_OPTIONAL = true;
		var sender = new DummyCommandSender();
		var cmd = new DummyCommand(false);
		var result = cmd.execute(sender, "dummy", new String[] {
				"setdummy" });
		assertTrue(result);
		assertNull(cmd.args);
		assertNotNull(cmd.getSubCmd().args);
		assertFalse(cmd.getSubCmd().args.isPresent(0, Integer.class));

		SUBCOMMAND_ARGUMENT_OPTIONAL = false;
	}

	@Getter
	public static class DummyCommand extends Cmd {

		private Arguments args;
		private DummySubCommand subCmd;

		public DummyCommand(boolean subCmdOptional) {
			super("dummy");
			var subCmd = new DummySubCommand(subCmdOptional);
			rc(0, subCmd);
			this.subCmd = subCmd;
		}

		@Override
		public void execute(CommandSender sender, Arguments args) {
			this.args = args;
		}

	}

	@Getter
	public static class DummySubCommand extends SubCmd {

		private Arguments args;

		public DummySubCommand(boolean optional) {
			super("setdummy", optional);
			rc(0, new IntegerArgument("Count", SUBCOMMAND_ARGUMENT_OPTIONAL));
		}

		@Override
		public void execute(CommandSender sender, Arguments args) {
			this.args = args;
		}

	}

}
