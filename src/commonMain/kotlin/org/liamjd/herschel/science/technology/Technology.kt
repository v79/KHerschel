package org.liamjd.herschel.science.technology

import kotlinx.serialization.Serializable

@Serializable
data class Technology(val key: String, var name: String = "",
					  var description: String = "", var techLevel: TechLevel = TechLevel.LEVEL_1,
					  val parents: MutableSet<String> = mutableSetOf(),
					  val enablesFacilities: MutableSet<String> = mutableSetOf(),
					  val deprecatesFacilities: MutableSet<String> = mutableSetOf(),
					  val enablesActions: MutableSet<String> = mutableSetOf(),
					  val deprecatesActions: MutableSet<String> = mutableSetOf(), var spritePath: String = "",
					  var researched: Boolean = false
) {

	fun toOneLineString(): String {
		return "$name - ${description.subSequence(0,100)}... Lvl: $techLevel"
	}
}

class Facility(val key: String) {
	var name: String = ""
}

enum class TechLevel {
	LEVEL_1,
	LEVEL_2,
	LEVEL_3,
	LEVEL_4
}
