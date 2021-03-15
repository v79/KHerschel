package org.liamjd.herschel.state

/**
 * Base class for all planets, space stations, asteroids
 * All entities have an ID, an optional parent, and an action.
 * Actions may take 1 or more turns - building a new facility or running a special project, perhaps.
 * Attributes
 */
abstract class Entity() {

	val id: Int = IDProvider.getId()
	var parent: Int? = null
	private val attributes = mutableMapOf<String, AttributeValue>()

	init {
		IDProvider.register(this)
	}

	var action: GameAction? = null
		protected set

	open fun addAction(gameAction: GameAction) {
		action = gameAction
		action?.apply {
			status = GameActionStatus.PENDING
		}
	}

	abstract fun update(year: Int, era: Era)

	open fun addAttribute(attribute: Attribute) {
		attributes[attribute.name] = attribute.value
	}

	open fun removeAttribute(name: String) {
		attributes.remove(name)
	}

	open fun getAttributeValue(name: String): AttributeValue? {
		return attributes[name]
	}

	override fun toString(): String {
		return "${this::class.simpleName}: $id, parent: $parent, children: ${IDProvider.getChildrenOf(id)?.size}"
	}

}

object IDProvider {
	private val idMap = mutableMapOf<Int, Entity>()
	private var currentId = 0
	fun getId(): Int {
		return ++currentId
	}

	fun register(entity: Entity) {
		idMap[entity.id] = entity
	}

	fun <T> getEntity(id: Int): T? {
		val entity = idMap[id]
		if (entity != null) {
			return entity as T
		}
		return null
	}

	fun getChildrenOf(id: Int): List<Entity>? {
		val children = mutableListOf<Entity>()
		for (e in idMap) {
			if (e.value.parent == id) {
				children.add(e.value)
			}
		}
		return if (children.isEmpty()) {
			null
		} else children
	}

	fun debugEntityTree() {
		idMap.forEach {
			if(it.value.parent == null) {
				println("${it.value}")
			} else {
				println("\t${it.value}")
			}
		}
	}

	fun reset() {
		idMap.clear()
	}
}

class EntityUpdateSystem {

	val entities: MutableList<Entity> = mutableListOf()

	val count get() = entities.size

	private fun register(entity: Entity) {
		entities.add(entity)
		val children = IDProvider.getChildrenOf(entity.id)
		children?.forEach {
			register(it)
		}
	}

	private fun unregister(entity: Entity) {
		entities.remove(entity)
		// TODO: what about the children?
	}

	operator fun plusAssign(entity: Entity) = register(entity)
	operator fun minusAssign(entity: Entity) = unregister(entity)

	inline fun <reified T> process(year: Int, era: Era) {
		entities.forEach {
			if (it is T) {
				it.update(year, era)
			}
		}
	}

	@Deprecated("Use ID provider map instead", ReplaceWith("IDProvider.getEntity()"))
	fun getEntityById(idToFind: Int): Entity? {
		return entities.firstOrNull { it.id == idToFind }
	}
}

interface AttributeValue
class Attribute(val name: String, val value: AttributeValue)
