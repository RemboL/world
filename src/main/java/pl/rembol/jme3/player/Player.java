package pl.rembol.jme3.player;

import com.jme3.math.ColorRGBA;

public class Player {

	private String name;

	private static Integer playerCounter = 0;
	private Integer id;

	private ColorRGBA color = null;

	public Player(String name) {
		this.name = name;
		id = playerCounter++;
	}

	public Player(String name, ColorRGBA color) {
		this(name);
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public Integer getId() {
		return id;
	}

	public ColorRGBA getColor() {
		return color;
	}

	@Override
	public boolean equals(Object that) {
		if (!Player.class.isInstance(that)) {
			return false;
		}

		return Player.class.cast(that).getId().equals(this.getId());
	}
}
