package org.liamjd.herschel

import com.soywiz.klock.seconds
import com.soywiz.korge.annotations.KorgeExperimental
import com.soywiz.korge.input.onClick
import com.soywiz.korge.input.onOut
import com.soywiz.korge.input.onOver
import com.soywiz.korge.scene.AlphaTransition
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import org.liamjd.herschel.science.technology.Technology
import org.liamjd.herschel.state.GameModel
import org.liamjd.herschel.state.GameState

class SolarSystem(private val gameState: GameState) : Scene() {

	private val topBarPadding = 5.0
	private var gm: GameModel = gameState.gm
	var availableTechs: List<Technology>? = null

	@KorgeExperimental
	override suspend fun Container.sceneInit() {
		val centerY = views.virtualHeightDouble / 2
		views.gameWindow.title = "Herschel: Solar System"

		val menuButton = text("Main Menu") {
			onClick {
				sceneContainer.changeTo<MainMenu>(time = 1.seconds, transition = AlphaTransition)
			}
		}
		val year = text("Year: ${gm.year}") {
			position(views.virtualWidthDouble - topBarPadding - this.width, topBarPadding)

			addUpdater {
				text = "Year: ${gm.year}"
			}
		}

		val topBar = solidRect(width = views.virtualWidthDouble, height = 40.0, Colors.DARKBLUE) {
			addChild(menuButton)
			addChild(year)
		}

		val nextTurn = text("Next Turn") {
			position(views.virtualWidthDouble - 100,views.virtualHeightDouble - 32.0)
			onClick {
				gameState.nextTurn()
				availableTechs = gameState.getAvailableTechnologies(gm.era)
			}
		}


		val techContainer = container() {
			solidRect(200.0, 300.0, Colors.SLATEBLUE) {
				position(680, 180)
			}

				availableTechs = gameState.getAvailableTechnologies(gm.era)
				var techX = 700
				var techY = 200
				availableTechs?.apply {
					for (tech in this) {
						text(tech.name) {
							position(techX, techY)
							val desc = text(tech.description) {
								color = Colors.YELLOW
								visible = false
								position(this@text.x + 10.0, this@text.y + 10)
							}
							onOver {
								desc.visible = true
							}
							onOut {
								desc.visible = false
							}
						}
						techY += 20
					}

			}
		}
	}

}

