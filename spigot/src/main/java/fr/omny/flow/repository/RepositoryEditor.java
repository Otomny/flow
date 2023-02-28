package fr.omny.flow.repository;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bukkit.entity.Player;

import fr.omny.flow.api.data.CrudRepository;
import fr.omny.guis.OClass;
import fr.omny.guis.OField;
import fr.omny.guis.OGui;
import fr.omny.guis.attributes.Updateable;
import lombok.Getter;

@Getter
@OClass
/**
 * Wrapper used to edit data from repository
 */
public final class RepositoryEditor<T> implements Updateable {

	/**
	 * Create a repository wrapper editor to be called by the
	 * {@link fr.omny.guis.OGui#open(org.bukkit.entity.Player, Object)}
	 * 
	 * @param <T>        The data type of the repository
	 * @param repository The repository containing the data
	 * @return
	 */
	public static <T> RepositoryEditor<T> of(CrudRepository<T, ?> repository) {
		return new RepositoryEditor<>(repository);
	}

	/**
	 * Open an Editor for the player to be able to modify
	 * 
	 * @param <T>        The data type of the repository
	 * @param player     The player that will edit the repository
	 * @param repository The repository containing the data
	 */
	public static <T> void open(Player player, CrudRepository<T, ?> repository) {
		open(player, repository, () -> {
			player.closeInventory();
		});
	}

	/**
	 * 
	 * @param <T>
	 * @param player
	 * @param repository
	 * @param onClose
	 */
	public static <T> void open(Player player, CrudRepository<T, ?> repository, Runnable onClose) {
		OGui.open(player, of(repository), onClose);
	}

	@OField(value = "Data")
	private List<T> datas;

	private CrudRepository<T, ?> repository;

	private RepositoryEditor(CrudRepository<T, ?> repository) {
		this.repository = repository;
		this.datas = StreamSupport.stream(repository.findAll().spliterator(), false).collect(Collectors.toList());
	}

	@Override
	public void update() {
	}

	@Override
	public void fieldUpdate(Field field) {
		if (field.getName().equals("datas")) {
			for (T data : datas) {
				repository.save(data);
			}
		}
	}

}
