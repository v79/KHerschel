package org.liamjd.herschel.state

class Planet(var buildSlotCount: Int = 0, val name: String) : Entity() {

	var population: Double = 0.0
	val buildSlots = mutableListOf<BuildSlot>()

	override fun update(year: Int, era: Era) {
		//TODO("Not yet implemented")
	}

	fun addBuildSlot(): BuildSlot {
		val buildSlot = BuildSlot()
		buildSlot.parent = this.id
		buildSlots.add(buildSlot)
		buildSlotCount++
		return buildSlot
	}

	fun getFreeBuildSlot(): BuildSlot? {
		return buildSlots.find { bs -> bs.status == BuildSlotStatus.EMPTY }
	}

	override fun toString(): String {
		return "${super.toString()}: '$name'"
	}

}

class BuildSlot : Entity() {

	var status = BuildSlotStatus.EMPTY
	private val validActions = arrayOf(GameActionType.BUILD_ITEM, GameActionType.DESTROY_ITEM)

	override fun addAction(gameAction: GameAction) {
		if (gameAction.type !in validActions) {
			println("Error: attempted to add an invalid action type of ${gameAction.type} to BuildSlot")
			return
		}
		action = gameAction
		action?.apply {
			status = GameActionStatus.PENDING
		}
	}

	override fun update(year: Int, era: Era) {
		if (action != null) {
			status = BuildSlotStatus.IN_PROGRESS
			action?.apply {
				val childEntity = IDProvider.getChildrenOf(this@BuildSlot.id)?.firstOrNull()
				if (this.facility != null && childEntity == null) {
					// we are constructing a facility but nothing exists yet. Create it.
					this.facility.parent = this@BuildSlot.id
				}
				turnsLeft--
				if (turnsLeft == 0) {
					this@BuildSlot.status = BuildSlotStatus.COMPLETE
					action = null // TODO: really want to plug this into a messaging system
				}
			}
		}
	}

	override fun toString(): String {
		return "${super.toString()}: action: $action $status"
	}
}


enum class BuildSlotStatus {
	EMPTY,
	PENDING,
	IN_PROGRESS,
	COMPLETE
}

class Facility(val name: String, private val buildTime: Int) : Entity() {
	var health: Int = 0
	var status: FacilityStatus = FacilityStatus.PENDING
	private var buildTimeRemaining = buildTime

	override fun update(year: Int, era: Era) {
		if (status == FacilityStatus.PENDING) {
			// start building
			status = FacilityStatus.UNDER_CONSTRUCTION

		}
		if(status == FacilityStatus.UNDER_CONSTRUCTION) {
			buildTimeRemaining--
			health = (100 / buildTime) * (buildTime - buildTimeRemaining)
		}
		if(buildTimeRemaining == 0) {
			status = FacilityStatus.OPERATING
		}
	}

	override fun toString(): String {
		return "${super.toString()}: '$name' $status, health: $health"
	}

}

enum class FacilityStatus {
	PENDING,
	UNDER_CONSTRUCTION,
	OPERATING,
	DAMAGED,
	DESTROYED
}
