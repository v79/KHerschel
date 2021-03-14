package org.liamjd.herschel

import com.soywiz.klock.seconds
import com.soywiz.korge.animate.animate
import com.soywiz.korge.input.mouse
import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.AlphaTransition
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.ui.UIView
import com.soywiz.korge.view.*
import com.soywiz.korge.view.filter.PageFilter
import com.soywiz.korge.view.tween.moveBy
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import org.liamjd.herschel.state.GameState

class SolarSystem(val gameState: GameState) : Scene() {
	private val topBarPadding = 5.0

	override suspend fun Container.sceneInit() {
		views.gameWindow.title = "Herschel: Solar System"

		val menuButton = text("Main Menu") {
			onClick {
				sceneContainer.changeTo<MainMenu>(time = 1.seconds, transition = AlphaTransition)
			}
		}
		val year = text("Year: ${gameState.year}") {
			position(views.virtualWidthDouble - topBarPadding - this.width, topBarPadding)
		}

		solidRect(width = views.virtualWidthDouble, height = 40.0, Colors.DARKBLUE) {
			addChild(menuButton)
			addChild(year)
		}

		val actionCardStartPosX = 160.0
		val actionCardStartPosY = views.virtualHeightDouble - 50
		val cardWidth = 120
		val cardFilter = PageFilter()
		val actionCardArray = arrayListOf<Card>()
		for (actionCard in CardColour.values()) {
//			val c = Card(actionCard.color,actionCard.name)
//			c.position((actionCardStartPosX + (actionCard.ordinal * cardWidth)),actionCardStartPosY)
//			c.addFilter(cardFilter)
			val c = card(
				actionCard.color,
				actionCard.name
			)
			c.xy((actionCardStartPosX + (actionCard.ordinal * cardWidth)), actionCardStartPosY)
			c.filter = cardFilter
			c.mouse {
				onOver {
					if (!c.peaking) {
						println("mouse over - peaking: ${c.peaking}")
						animate {
							c.peaking = true
							c.moveBy(0.0, -100.0)
						}
					}
				}

				onOut {
					if (c.peaking && !c.expanded) {
						animate {
							c.moveTo((actionCardStartPosX + (actionCard.ordinal * cardWidth)), actionCardStartPosY)
							c.peaking = false
						}
						c.setSize(120.0,200.0)
					}
					if(c.expanded) {

					}
				}

				onClick {
					if(!c.expanded && c.peaking) {
						c.expanded = true
						animate {
							parallel {
								c.moveTo(views.virtualWidthDouble /2 - 60,views.virtualHeightDouble / 2 )
								c.scaleBy(1.1,1.1)
							}
						}
					} else {
						c.expanded = false
						c.scale = 1.0
						c.position((actionCardStartPosX + (actionCard.ordinal * cardWidth)), actionCardStartPosY)
					}
				}
			}
			actionCardArray.add(c)
		}
	}
}

class Card(color: RGBA, name: String) : UIView(120.0, 200.0) {
	private val cardBack = RoundRect(120.0, 200.0, fill = color, rx = 20.0)
	var peaking = false
	var expanded = false

	init {
		addChild(cardBack)
	}

	suspend fun peak() {
		moveBy(20.0, -200.0)
	}


}

fun Container.card(color: RGBA, name: String, callback: @ViewDslMarker Card.() -> Unit = {}) =
	Card(color, name).addTo(this)

enum class CardColour(val color: RGBA) {
	BLUE(Colors.BLUE),
	GREEN(Colors.GREEN),
	ORANGE(Colors.ORANGE),
	PINK(Colors.PINK),
	YELLOW(Colors.YELLOW)
}
