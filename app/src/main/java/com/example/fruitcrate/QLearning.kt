package com.example.fruitcrate
class QLearning {
    private val numStates = 24
    private val numActions = Action.entries.size // {Left , Right, Stay}
    private val qTable = Array(numStates) { DoubleArray(numActions) }
    private val alpha = 0.2
    private val gamma = 0.9
    var epsilon = 0.2

    fun chooseAction(state: Int): Action { //keşif
        return if (Math.random() < epsilon) {
            Action.entries.random()
        } else { // yoksa en yüksek
            val bestIndex = qTable[state].indices.maxByOrNull { qTable[state][it] } ?: 0
            Action.entries[bestIndex]
        }
    }

    fun update(state: Int, action: Action, reward: Double, nextState: Int) {
        val a = action.ordinal
        val maxNext = qTable[nextState].maxOrNull() ?: 0.0
        qTable[state][a] += alpha * (reward + gamma * maxNext - qTable[state][a])
    }

    fun decayEpsilon(factor: Double) {
        if (epsilon > 0.01) {
            epsilon *= factor
        }
    }
}
