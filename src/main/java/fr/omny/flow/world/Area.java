package fr.omny.flow.world;

import org.bukkit.Location;

import fr.omny.guis.OClass;
import fr.omny.guis.OField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@OClass
public class Area {
	
	@OField
	private Location start;
	@OField
	private Location end;

}
