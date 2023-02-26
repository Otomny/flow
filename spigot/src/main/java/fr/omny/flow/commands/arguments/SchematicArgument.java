package fr.omny.flow.commands.arguments;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.bukkit.command.CommandSender;

import fr.omny.flow.commands.CmdArgument;
import fr.omny.flow.commands.wrapper.Arguments;
import fr.omny.flow.world.schematic.Schematic;
import fr.omny.flow.world.schematic.SchematicRepository;
import fr.omny.odi.Autowired;

public class SchematicArgument extends CmdArgument<Schematic> {

	@Autowired
	private SchematicRepository repository;

	public SchematicArgument(boolean optional) {
		super("Schematic", optional);
	}

	@Override
	public Optional<Schematic> getValue(String textValue, CommandSender sender, Arguments precedentArguments) {
		return StreamSupport.stream(repository.findAll().spliterator(), false)
				.filter(schematic -> schematic.getName().equalsIgnoreCase(textValue))
				.findFirst();
	}

	@Override
	public List<String> getValues(CommandSender sender, Arguments precedentArguments) {
		return StreamSupport.stream(repository.findAll().spliterator(), false)
				.map(Schematic::getName)
				.toList();
	}

}
