package org.liamjd.herschel.state

import com.soywiz.korio.async.runBlockingNoSuspensions
import com.soywiz.korio.file.std.resourcesVfs
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.liamjd.herschel.science.technology.Technology

class GameState(startingYear: Int) {

	val format = Json { prettyPrint = true; encodeDefaults = true }

	val gm = GameModel(startingYear)
	val techTree: List<Technology>

	val entitySystem: EntityUpdateSystem = EntityUpdateSystem()
	val orderSystem: OrderSystem = OrderSystem()

	fun nextTurn() {
		orderSystem.processOrderQueue(gm.year,gm.era)
		entitySystem.process<Entity>(gm.year,gm.era)
		gm.year++

		if(gm.year == 2055) {
			techTree.find { it.key == "1_quantumComputer" }?.researched = true
		}
	}

	init {
		println("Loading Tech Tree")
		val techTreeJson = runBlockingNoSuspensions {
			resourcesVfs["technologies/techs.json"].readString()
		}
		techTree = Json.decodeFromString(techTreeJson)
	}

	fun getAvailableTechnologies(era: Era): List<Technology> {
		return techTree.filter { !it.researched }
	}

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


