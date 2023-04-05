package fr.omny.flow.world.pasting;

import java.util.List;

import fr.omny.flow.world.BlockUpdate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BlockBatch {

	private Runnable onEnd;
	private boolean skipAir;
	private List<BlockUpdate> blockUpdate;

}
