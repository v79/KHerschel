package org.liamjd.herschel.state

class GameModel(var year: Int = 2050) {
	var era = Era.EARTH
}

/**
 * The game will progress through different eras, and at each stage the map will zoom out to reveal more of the solar system.
 * For now, we will stop at the full solar system view; eventual target is to take this galactic.
 */
enum class Era {
	EARTH,
	MARS,
	INNER_PLANETS,
	SOLAR_SYSTEM
}
