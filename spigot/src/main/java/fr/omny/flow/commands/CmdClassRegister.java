package fr.omny.flow.commands;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.Plugin;

import fr.omny.flow.api.aop.ClassRegister;
import fr.omny.flow.api.process.FlowProcess;
import fr.omny.guis.utils.ReflectionUtils;
import fr.omny.odi.Injector;
import fr.omny.odi.Utils;
import fr.omny.odi.utils.PreClass;

public class CmdClassRegister implements ClassRegister {

	@Override
	public List<Object> register(FlowProcess process) {
		Predicate<PreClass> commandsFilter = preClass -> preClass.isSuperClass(Cmd.class) && preClass.isNotByteBuddy();
		Set<Class<?>> commands = Stream.concat(ClassRegister.getDeclared(process, commandsFilter),
				Utils.getClasses("fr.omny.flow", commandsFilter).stream()).collect(Collectors.toSet());

		Plugin plugin = (Plugin) process;
		List<Object> generated = new ArrayList<>();
		Optional<CommandMap> map = ReflectionUtils.get(Bukkit.getServer(), "commandMap");
		map.ifPresentOrElse(commandMap -> {
			commands.forEach(klass -> {
				try {
					Cmd cmdInstance = (Cmd) Utils.callConstructor(klass);
					deepWire(cmdInstance);
					generated.add(cmdInstance);
					commandMap.register(cmdInstance.getName(), "", cmdInstance);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| SecurityException e) {
					e.printStackTrace();
				}
			});
		}, () -> plugin.getLogger().warning("Could not find commandMap, unable to auto register commands instances"));
		return generated;
	}

	@Override

	public void postWire(Object object) {
		if (object instanceof Cmd cmd) {
			deepWire(cmd);
		}
	}

	private void deepWire(Cmd cmd) {
		Injector.wire(cmd);
		cmd.getComps()
				.values()
				.stream()
				.flatMap(List::stream)
				.forEach(cmdComp -> {
					Injector.wire(cmdComp);
					if (cmdComp instanceof SubCmd subCmd) {
						deepWire(subCmd);

					}
				});
	}

	private void deepWire(SubCmd subCmd) {
		for (int k : subCmd.getComps().keySet()) {
			for (CommandComponent cmpComp : subCmd.getComps().get(k)) {
				Injector.wire(cmpComp);
				if (cmpComp instanceof SubCmd nestedSubCmd) {
					deepWire(nestedSubCmd);
				}
			}
		}
	}

}
