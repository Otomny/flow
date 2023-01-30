package fr.omny.flow.commands;

public class SubCmd implements CommandComponent {

	private String name;
	private boolean optional;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isOptional() {
		return this.optional;
	}

}
