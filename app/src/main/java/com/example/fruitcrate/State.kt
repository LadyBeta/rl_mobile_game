package com.example.fruitcrate
enum class Action { LEFT, RIGHT, STAY }
enum class FruitPos { LEFT, CENTER, RIGHT }
enum class BrickDist { FAR, NEAR }
enum class PoisonDist { FAR, NEAR }
enum class SpeedState { NORMAL, SLOW }

data class EnvState(
    val fruit: FruitPos,
    val brick: BrickDist,
    val poison: PoisonDist,
    val speed: SpeedState
)

// Encode fonksiyonu
fun encode(fruit: FruitPos, brick: BrickDist, poison: PoisonDist, speed: SpeedState): Int {
    return fruit.ordinal * 8 + brick.ordinal * 4 + poison.ordinal * 2 + speed.ordinal
}

fun encode(state: EnvState): Int = encode(state.fruit, state.brick, state.poison, state.speed)
