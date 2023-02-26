package fr.omny.flow.data;

import org.bukkit.Bukkit;
import org.redisson.api.RTopic;

import fr.omny.flow.api.data.ObjectUpdate;
import fr.omny.flow.api.data.implementation.MongoDBRepository;
import fr.omny.flow.events.data.DataEmitEvent;
import fr.omny.flow.events.data.KnownDataUpdateEvent;
import fr.omny.odi.Component;
import fr.omny.odi.Joinpoint;

@Component
public class MongoDBJoinPoints {

	@Joinpoint(value = "emit", on = MongoDBRepository.class)
	public void onEmit(MongoDBRepository<?, ?> instance, Class<?> dataClass, ObjectUpdate update, RTopic topic) {
		var event = new DataEmitEvent(instance, dataClass, update);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (!event.isCancelled()) {
			topic.publish(update);
		}
	}

	@Joinpoint(value = "update", on = MongoDBRepository.class)
	public void onUpdate(MongoDBRepository<?, ?> instance, Class<?> dataClass, ObjectUpdate update) {
		var event = new KnownDataUpdateEvent(instance, dataClass, update);
		Bukkit.getServer().getPluginManager().callEvent(event);
	}

}
