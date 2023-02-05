package fr.omny.flow.translation;

import java.util.Map;

import org.redisson.api.annotation.REntity;
import org.redisson.api.annotation.RId;

import fr.omny.flow.data.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@REntity
public class Translation {
	
	@Id
	@RId
	private String key;
	private Map<String, String> translations;

}
