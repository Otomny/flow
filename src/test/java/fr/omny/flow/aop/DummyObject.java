package fr.omny.flow.aop;


import fr.omny.flow.data.Val;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DummyObject {

	@Val
	private String world = "Hello world!";

	DummyObject(){}

	public void selfInvoke(String newWorld){
		setWorld(newWorld);
	}

}
