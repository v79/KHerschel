import com.soywiz.korge.Korge
import com.soywiz.korge.scene.Module
import com.soywiz.korge.scene.Scene
import com.soywiz.korinject.AsyncInjector
import com.soywiz.korma.geom.SizeInt
import org.liamjd.herschel.state.GameState
import org.liamjd.herschel.MainMenu
import org.liamjd.herschel.SolarSystem
import kotlin.reflect.KClass

suspend fun main() = Korge(Korge.Config(module = HerschelModule))

object HerschelModule : Module() {
	override val mainScene: KClass<out Scene> = SolarSystem::class
	override val size: SizeInt = SizeInt(1024,768)
	override val title: String = "Herschel"


	override suspend fun AsyncInjector.configure() {
		mapInstance(GameState(2050))
		mapPrototype { SolarSystem(get()) }
		mapPrototype { MainMenu() }
	}
}
