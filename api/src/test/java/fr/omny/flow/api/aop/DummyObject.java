package fr.omny.flow.api.aop;


import fr.omny.flow.api.data.Val;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DummyObject {

	@Val
	private String world = "Hello world!";

	DummyObject() {}

	public void selfInvoke(String newWorld) {
		setWorld(newWorld);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((world == null) ? 0 : world.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DummyObject other = (DummyObject) obj;
		if (world == null) {
			if (other.world != null)
				return false;
		} else if (!world.equals(other.world))
			return false;
		return true;
	}

}
