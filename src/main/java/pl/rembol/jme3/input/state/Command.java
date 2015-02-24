package pl.rembol.jme3.input.state;

public enum Command {

	MOVE(0, 0, "move", InputStateManager.M), //
	FLATTEN(1, 0, "flatten", InputStateManager.F), //
	BUILD(2, 0, "build", InputStateManager.B), //
	BUILD_HOUSE(0, 0, "house", InputStateManager.H), //
	BUILD_WAREHOUSE(1, 0, "warehouse", InputStateManager.W), //
	CANCEL(2, 2, "cancel", InputStateManager.C), //
	RECRUIT(0, 0, "ballman", InputStateManager.R); //

	private int positionX;
	private int positionY;
	private String iconName;
	private String commandKey;

	private Command(int positionX, int positionY, String iconName,
			String commandKey) {
		this.positionX = positionX;
		this.positionY = positionY;
		this.iconName = iconName;
		this.commandKey = commandKey;
	}

	public int getPositionX() {
		return positionX;
	}

	public int getPositionY() {
		return positionY;
	}

	public String getIconName() {
		return iconName;
	}

	public String getCommandKey() {
		return commandKey;
	}
}
