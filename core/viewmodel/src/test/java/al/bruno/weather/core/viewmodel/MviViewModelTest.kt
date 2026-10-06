package al.bruno.weather.core.viewmodel

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MviViewModelTest {

    data class CounterState(val count: Int = 0) : UIState

    sealed interface CounterEvent : UIEvent {
        data object Increment : CounterEvent
        data object Notify : CounterEvent
        data object Work : CounterEvent
        data object Boom : CounterEvent
    }

    data class Toast(val text: String) : UIEffect

    data class Cmd(val label: String)

    private val counterReducer = Reducer<CounterState, CounterEvent, Toast, Cmd> { state, event ->
        when (event) {
            CounterEvent.Increment -> Next(state.copy(count = state.count + 1))
            CounterEvent.Notify -> Next(state, effects = listOf(Toast("a"), Toast("b")))
            CounterEvent.Work -> Next(state.copy(count = 10), commands = listOf(Cmd("first"), Cmd("second")))
            CounterEvent.Boom -> error("bug in reducer")
        }
    }

    private class TestViewModel(
        reducer: Reducer<CounterState, CounterEvent, Toast, Cmd>,
        debugChecks: Boolean = false,
    ) : MviViewModel<CounterState, CounterEvent, Toast, Cmd>(CounterState(), reducer, debugChecks) {
        val executed = mutableListOf<Pair<Cmd, Int>>()

        override fun execute(command: Cmd) {
            executed += command to state.value.count
        }

        fun startUnique(key: Any, block: suspend () -> Unit) = launchUnique(key) { block() }
    }

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `events go through the reducer`() {
        val vm = TestViewModel(counterReducer)
        vm.sendEvent(CounterEvent.Increment)
        vm.sendEvent(CounterEvent.Increment)
        assertEquals(CounterState(count = 2), vm.state.value)
    }

    @Test
    fun `commands run in order after the new state is set`() {
        val vm = TestViewModel(counterReducer)
        vm.sendEvent(CounterEvent.Work)
        assertEquals(listOf(Cmd("first") to 10, Cmd("second") to 10), vm.executed)
    }

    @Test
    fun `effects are queued in order with unique ids until handled`() {
        val vm = TestViewModel(counterReducer)
        vm.sendEvent(CounterEvent.Notify)
        vm.sendEvent(CounterEvent.Notify)

        val queued = vm.effects.value
        assertEquals(listOf(Toast("a"), Toast("b"), Toast("a"), Toast("b")), queued.map { it.effect })
        assertEquals(queued.size, queued.map { it.id }.toSet().size)

        vm.effectHandled(queued[0].id)
        assertEquals(queued.drop(1), vm.effects.value)
    }

    @Test
    fun `handling an unknown id changes nothing`() {
        val vm = TestViewModel(counterReducer)
        vm.sendEvent(CounterEvent.Notify)
        val before = vm.effects.value
        vm.effectHandled(id = 999)
        assertEquals(before, vm.effects.value)
    }

    @Test
    fun `a throwing reducer is rethrown by default and state is unchanged`() {
        val vm = TestViewModel(counterReducer)
        vm.sendEvent(CounterEvent.Increment)
        assertThrows(IllegalStateException::class.java) { vm.sendEvent(CounterEvent.Boom) }
        assertEquals(CounterState(count = 1), vm.state.value)
    }

    @Test
    fun `debug checks catch an impure reducer`() {
        var calls = 0
        val impure = Reducer<CounterState, CounterEvent, Toast, Cmd> { state, _ -> Next(state.copy(count = ++calls)) }
        val vm = TestViewModel(impure, debugChecks = true)

        val error = assertThrows(IllegalStateException::class.java) { vm.sendEvent(CounterEvent.Increment) }
        assertTrue(error.message!!.contains("not pure"))
        assertEquals(CounterState(), vm.state.value)
    }

    @Test
    fun `debug checks accept a pure reducer`() {
        val vm = TestViewModel(counterReducer, debugChecks = true)
        vm.sendEvent(CounterEvent.Work)
        assertEquals(CounterState(count = 10), vm.state.value)
    }

    @Test
    fun `launchUnique cancels running work with the same key only`() {
        val vm = TestViewModel(counterReducer)
        val first = CompletableDeferred<Unit>()
        val other = CompletableDeferred<Unit>()
        var firstCancelled = false

        vm.startUnique("fetch") {
            try {
                awaitCancellation()
            } finally {
                firstCancelled = true
                first.complete(Unit)
            }
        }
        vm.startUnique("history") { other.await() }
        vm.startUnique("fetch") { awaitCancellation() }

        assertTrue(firstCancelled)
        assertFalse(other.isCompleted)
    }
}
