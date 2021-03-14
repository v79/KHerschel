package org.liamjd.herschel.state

class GameOrder(val name: String, val cost: Int = 1, val type: GameActionType, val target: Entity, val facility: Entity? = null)

class OrderSystem {
	private var capacity = 5
	var count = 0
		private set

	private val orderQueue = mutableListOf<GameOrder>()

	/**
	 * Attempt to add the order to the order system. First it checks the capacity of the queue.
	 * The order is rejected if the cost of the action is greater than the capacity, or if the action
	 * will put the queue over-capacity.
	 * Queue capacity may change over time.
	 */
	fun addOrder(order: GameOrder): Boolean {
		if(order.cost > capacity) {
			println("ActionSystem cannot add action $order - cost is greater than capacity $capacity")
			return false
		}
		if(count + order.cost > capacity) {
			println("ActionSystem cannot add action $order - cost ${order.cost} would exceed capacity $capacity")
			return false
		}
		orderQueue.add(order)
		count++
		return true
	}

	/**
	 * Process the order queue. For each item in the queue,
	 * execute it - applying it to the target - and remove it from the queue
	 */
	fun processOrderQueue(year: Int, era: Era) {
		orderQueue.forEach { order ->
			order.target.addAction(GameAction(order.name,order.cost,order.type, order.facility))
			count--
		}
		//println("clearing order queue, not sure if there are circumstances when this would be wrong")
		orderQueue.clear()
	}
}
