package fr.omny.flow.command;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.omny.flow.commands.Cmd;
import fr.omny.flow.commands.arguments.IntegerArgument;
import fr.omny.flow.commands.wrapper.Arguments;
import fr.omny.flow.entity.DummyCommandSender;
import fr.omny.odi.Injector;
import lombok.Getter;

public class OptionalCommandTest {
	
	@BeforeEach
	public void setupForEach(){
		Injector.startTest();
	}

	@AfterEach
	public void tearDownForEach(){
		Injector.wipeTest();
	}

	@Test
	public void test_ExecuteCommand_NoArguments() {
		var sender = new DummyCommandSender();
		var cmd = new DummyCommand();
		var result = cmd.execute(sender, "dummy", new String[] {});
		assertTrue(result);
		assertNotNull(cmd.args);
		assertEquals(0, cmd.args.count());
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
			rc(0, new IntegerArgument("Count", true));
		}

		@Override
		public void execute(CommandSender sender, Arguments args) {
			this.args = args;
		}

	}

}
