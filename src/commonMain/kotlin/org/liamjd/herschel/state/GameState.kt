package org.liamjd.herschel.state

class GameState(startingYear: Int) {
	var year: Int = startingYear
	var era = Era.EARTH

	val entitySystem: EntityUpdateSystem = EntityUpdateSystem()
	val orderSystem: OrderSystem = OrderSystem()

	fun nextTurn() {
		orderSystem.processOrderQueue(year,era)
		entitySystem.process<Entity>(year,era)
		year++
	}

	init {

	}

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

/**
 * A GameAction is an ongoing (but short-lived) activity in response to an order. It will take [turnsLeft] game turns to complete.
 * The action may result in a new entity in the game map - a [facility], but may return a value (not yet modelled)
 */
data class GameAction(val name: String, var turnsLeft: Int, val type: GameActionType, val facility: Entity? = null) {
	var status: GameActionStatus = GameActionStatus.PENDING
}

enum class GameActionType {
	BUILD_ITEM,
	SEND_PROBE,
	DESTROY_ITEM
}

enum class GameActionStatus {
	PENDING,
	IN_PROGRESS,
	COMPLETED,
	CANCELLED
}


