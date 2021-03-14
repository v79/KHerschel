package org.liamjd.herschel.state

import kotlin.test.*

class GameStateTest {

	@Test
	fun `set up initial game state, no entities`() {
		// setup
		val gs = GameState(2050)

		// execute

		// verify
		assertTrue(gs.entitySystem.entities.size == 0)
		assertEquals(0, gs.entitySystem.count)
	}

	@Test
	fun `on next turn, year increments`() {
		// setup
		val gs = GameState(2050)

		// execute
		gs.nextTurn()

		// verify
		assertEquals(2051, gs.year)
		assertEquals(Era.EARTH, gs.era)
		assertTrue(gs.entitySystem.entities.size == 0)
		assertEquals(0, gs.entitySystem.count)
	}

	@Test
	fun `can register a new entity`() {
		// setup
		val gs = GameState(2050)

		// execute
		gs.entitySystem += TestEntity()

		// verify
		assertEquals(2050, gs.year)
		assertEquals(1, gs.entitySystem.count)
	}

	@Test
	fun `can unregister an entity`() {
		// setup
		val gs = GameState(2050)
		val entity = TestEntity()

		// execute
		gs.entitySystem += entity

		// verify
		assertEquals(2050, gs.year)
		assertEquals(1, gs.entitySystem.count)

		// execute
		gs.entitySystem -= entity

		// verify
		assertEquals(2050, gs.year)
		assertEquals(0, gs.entitySystem.count)
	}

	@Test
	@Ignore // unregistering a non-entity has no effect; meaningless test?
	fun `cannot unregister an entity which is not registered`() {
		// setup
		val gs = GameState(2050)
		val entity = TestEntity()

		// execute
		gs.entitySystem -= entity

		// verify
		assertEquals(0, gs.entitySystem.count)
	}

	@Test
	fun `next turn processes all Entities`() {
		// setup
		val gs = GameState(2050)
		val entity = TestEntity()
		gs.entitySystem += entity

		// verify
		assertEquals(0, entity.scienceCount)

		// execute
		gs.nextTurn()

		// verify
		assertEquals(2051, gs.year)
		assertEquals(Era.EARTH, gs.era)
		assertEquals(1, entity.scienceCount)
	}

	@Test
	fun `entity system can process only Entities of a specific class`() {
		// setup
		val gs = GameState(2050)
		val entity = TestEntity()
		val planet = TestPlanet()
		gs.entitySystem += entity
		gs.entitySystem += planet

		// execute
		gs.entitySystem.process<TestPlanet>(gs.year, gs.era)

		// verify
		assertEquals(2050, gs.year) // not calling gs.nextTurn
		assertEquals(Era.EARTH, gs.era)
		assertEquals(0, entity.scienceCount)
		assertEquals(1_001_000, planet.population)
	}

	@Test
	fun `action system can add an action which the queue can accommodate`() {
		// setup
		val gs = GameState(2050)
		val planet = Planet(1, "My planet")
		gs.entitySystem += planet
		val buildLab = GameOrder("build lab", 1, GameActionType.BUILD_ITEM, planet)

		// execute
		val buildResult = gs.orderSystem.addOrder(buildLab)

		// verify
		assertTrue(buildResult)
		assertEquals(1, gs.orderSystem.count)
	}

	@Test
	fun `action system will not add an action which the queue cannot accommodate`() {
		// setup
		val gs = GameState(2050)
		val planet = Planet(1, "My planet")
		gs.entitySystem += planet
		val buildLab = GameOrder("build lab", 50, GameActionType.BUILD_ITEM, planet)

		// execute
		val buildResult = gs.orderSystem.addOrder(buildLab)

		// verify
		assertFalse(buildResult)
		assertEquals(0, gs.orderSystem.count)
		assertNull(planet.action)
	}

	@Test
	fun `pending 1-build action is processed at the end of the turn and removed from the queue`() {
		// setup
		val gs = GameState(2050)
		val planet = Planet(1, "My planet")
		gs.entitySystem += planet
		val buildLab = GameOrder("build lab", 1, GameActionType.BUILD_ITEM, planet)
		val buildResult = gs.orderSystem.addOrder(buildLab)

		// execute
		gs.nextTurn()

		// verify
		assertEquals(2051, gs.year)
		val foundPlanet = gs.entitySystem.getEntityById(234)
		assertEquals(
			0,
			gs.orderSystem.count,
			"remaining action count is ${gs.orderSystem.count}"
		) // action is removed if it is completed
	}

	@Test
	fun `pending 2-build action is processed at the end of the turn and removed from the queue`() {
		// setup
		val gs = GameState(2050)
		val planet = Planet(1, "My planet")
		gs.entitySystem += planet
		val buildLab = GameOrder("build lab", 2, GameActionType.BUILD_ITEM, planet)
		val buildResult = gs.orderSystem.addOrder(buildLab)

		// execute
		gs.nextTurn()

		// verify
		assertTrue(buildResult)
		assertEquals(2051, gs.year)
		assertEquals(
			0,
			gs.orderSystem.count,
			"remaining action count is ${gs.orderSystem.count}"
		) // action is removed if it is completed
	}

	@Test
	fun `pending 1-build action completes at the end of the turn and the action is done`() {
		// setup
		val gs = GameState(2050)
		val planet = TestPlanet()
		gs.entitySystem += planet
		val buildLab = GameOrder("build lab", 1, GameActionType.BUILD_ITEM, planet)
		val buildResult = gs.orderSystem.addOrder(buildLab)

		// execute
		gs.nextTurn()

		// verify
		assertTrue(buildResult)
		assertEquals(2051, gs.year)
		val foundPlanet = IDProvider.getEntity<TestPlanet>(planet.id)
		assertNotNull(foundPlanet) { p ->
			assertNull(p.action)
		}
	}

	@Test
	fun `pending 2-build action does not complete at the end of the turn and turnsLeft is decremented`() {
		// setup
		val gs = GameState(2050)
		val planet = TestPlanet()
		gs.entitySystem += planet
		val buildLab = GameOrder("build lab", 2, GameActionType.BUILD_ITEM, planet)
		val buildResult = gs.orderSystem.addOrder(buildLab)

		// execute
		gs.nextTurn()

		// verify
		assertTrue(buildResult)
		assertEquals(2051, gs.year)
		val foundPlanet = IDProvider.getEntity<TestPlanet>(planet.id)
		assertNotNull(foundPlanet) { p ->
			assertNotNull(p.action) { a ->
				assertEquals(1, a.turnsLeft)
				assertEquals(GameActionStatus.IN_PROGRESS, a.status)
			}
		}
	}

	@Test
	fun `pending 2-build action is completed at the end of two turns and is removed`() {
		// setup
		val gs = GameState(2050)
		val planet = TestPlanet()
		gs.entitySystem += planet
		val buildLab = GameOrder("build lab", 2, GameActionType.BUILD_ITEM, planet)
		val buildResult = gs.orderSystem.addOrder(buildLab)

		// execute
		gs.nextTurn()
		gs.nextTurn()

		// verify
		assertTrue(buildResult)
		assertEquals(2052, gs.year)
		val foundPlanet = IDProvider.getEntity<TestPlanet>(planet.id)
		assertNotNull(foundPlanet) { p ->
			assertNull(p.action)
		}
	}

	@Test
	fun `pending 2-build action is added to a BuildSlot on a planet and is completed at the end of two turns and is removed`() {
		// setup
		val gs = GameState(2050)
		val planet = Planet(0, "My Planet")
		planet.addBuildSlot()
		gs.entitySystem += planet
		val buildSlot = planet.getFreeBuildSlot()
		val buildLab = GameOrder("build lab", 2, GameActionType.BUILD_ITEM, buildSlot!!)
		val buildResult = gs.orderSystem.addOrder(buildLab)

		// execute
		gs.nextTurn()
		gs.nextTurn()

		// verify
		assertTrue(buildResult)
		assertEquals(2052, gs.year)
		val foundPlanet = IDProvider.getEntity<Planet>(planet.id)
		assertNotNull(foundPlanet) { p ->
			assertNull(p.action)
			assertNull(p.getFreeBuildSlot())
		}
	}

	@Test
	fun `cannot add an invalid action type to a BuildSlot`() {
		val gs = GameState(2050)
		val planet = Planet(0, "My Planet")
		planet.addBuildSlot()
		gs.entitySystem += planet
		val buildSlot = planet.getFreeBuildSlot()
		val buildLab = GameOrder("send probe", 2, GameActionType.SEND_PROBE, buildSlot!!)
		val buildResult = gs.orderSystem.addOrder(buildLab)

		// execute
		gs.nextTurn()

		// verify
		val foundBuildSlot = IDProvider.getChildrenOf(planet.id)?.first() as BuildSlot
		assertEquals(BuildSlotStatus.EMPTY, foundBuildSlot.status)
	}

	@Test
	fun `a build action creates a new facility in the given slot`() {
		// setup
		val gs = GameState(2050)
		val planet = Planet(name = "Small Planet")
		val buildSlot = planet.addBuildSlot()
		gs.entitySystem += planet
		gs.entitySystem += buildSlot
		val lab = Facility("Research Laboratory", 1)
		gs.entitySystem += lab
		val buildLab = GameOrder("build lab", 1, GameActionType.BUILD_ITEM, planet.getFreeBuildSlot()!!, lab)
		val buildResult = gs.orderSystem.addOrder(buildLab)

		// execute
		gs.nextTurn()

		// verify
		assertTrue(buildResult)
		assertEquals(2051, gs.year)
		val foundPlanet = IDProvider.getEntity<Planet>(planet.id)
		assertNotNull(foundPlanet) { p ->
			assertNull(p.action)
			assertNull(p.getFreeBuildSlot()?.action)
			val buildSlot = IDProvider.getChildrenOf(p.id)?.first()
			assertNotNull(buildSlot) { bs ->
				assertNull(bs.action)
				val building = IDProvider.getChildrenOf(bs.id)?.first()
				assertNotNull(building) {
					val facility = it as Facility
					assertEquals("Research Laboratory", facility.name)
					assertEquals(100, facility.health)
					assertEquals(FacilityStatus.OPERATING, facility.status)
				}
			}
		}
	}

	@Test
	fun `a build action creates which takes 5 turns is not complete after 3 turns`() {
		// setup
		val gs = GameState(2050)
		val planet = Planet(name = "Small Planet")
		val buildSlot = planet.addBuildSlot()
		gs.entitySystem += planet
		gs.entitySystem += buildSlot
		val lab = Facility("Research Laboratory", 5)
		gs.entitySystem += lab
		val buildLab = GameOrder("build lab", 1, GameActionType.BUILD_ITEM, planet.getFreeBuildSlot()!!, lab)
		val buildResult = gs.orderSystem.addOrder(buildLab)

		// execute
		gs.nextTurn()
		gs.nextTurn()
		gs.nextTurn()

		// verify
		assertTrue(buildResult)
		assertEquals(2053, gs.year)
		val foundPlanet = IDProvider.getEntity<Planet>(planet.id)
		assertNotNull(foundPlanet) { p ->
			assertNull(p.action)
			assertNull(p.getFreeBuildSlot()?.action)
			val buildSlot = IDProvider.getChildrenOf(p.id)?.first()
			assertNotNull(buildSlot) { bs ->
				assertNull(bs.action)
				val building = IDProvider.getChildrenOf(bs.id)?.first()
				assertNotNull(building) {
					val facility = it as Facility
					assertEquals("Research Laboratory", facility.name)
					assertEquals(60, facility.health)
					assertEquals(FacilityStatus.UNDER_CONSTRUCTION, facility.status)
				}
			}
		}

		IDProvider.debugEntityTree()
	}
}


class TestEntity : Entity() {
	var scienceCount = 0
	override fun update(year: Int, era: Era) {
		scienceCount++
	}
}

class TestPlanet : Entity() {
	var population = 1_000_000

	override fun update(year: Int, era: Era) {
		population += 1000

		println("Action: $action: ${action?.name}, ${action?.turnsLeft}")

		action?.let { a ->
			a.status = GameActionStatus.IN_PROGRESS
			a.turnsLeft--
		}
		if (action?.turnsLeft == 0) {
			action = null
		}
	}
}

