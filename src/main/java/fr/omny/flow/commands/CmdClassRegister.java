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

import fr.omny.flow.aop.ClassRegister;
import fr.omny.flow.plugins.FlowPlugin;
import fr.omny.guis.utils.ReflectionUtils;
import fr.omny.odi.Injector;
import fr.omny.odi.Utils;
import fr.omny.odi.utils.PreClass;

public class CmdClassRegister implements ClassRegister {

	@Override
	public List<Object> register(FlowPlugin plugin) {
		Predicate<PreClass> commandsFilter = preClass -> preClass.isSuperClass(Cmd.class) && preClass.isNotByteBuddy();
		Set<Class<?>> commands = Stream.concat(Utils.getClasses(plugin.getPackageName(), commandsFilter).stream(),
				Utils.getClasses("fr.omny.flow", commandsFilter).stream()).collect(Collectors.toSet());

		List<Object> generated = new ArrayList<>();
		Optional<CommandMap> map = ReflectionUtils.get(Bukkit.getServer(), "commandMap");
		map.ifPresentOrElse(commandMap -> {
			commands.forEach(klass -> {
				try {
					Cmd cmdInstance = (Cmd) Utils.callConstructor(klass);
					Injector.wire(cmdInstance);
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

}
