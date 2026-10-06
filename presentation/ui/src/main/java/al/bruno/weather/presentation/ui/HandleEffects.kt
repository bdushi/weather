package al.bruno.weather.presentation.ui

import al.bruno.weather.core.viewmodel.MviHost
import al.bruno.weather.core.viewmodel.UIEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle

/**
 * Handles [host]'s pending effects one at a time, only while the screen is at least STARTED, and
 * confirms each with [MviHost.effectHandled] after [onEffect] returns.
 *
 * If the screen stops while [onEffect] is suspended (e.g. a snackbar is showing), the effect stays
 * queued and is handled again when the screen comes back. A confirmed effect is never handled twice.
 * Navigation handlers must navigate before returning.
 */
@Composable
fun <Eff : UIEffect> HandleEffects(
    host: MviHost<*, *, Eff>,
    onEffect: suspend (Eff) -> Unit,
) {
    val handler by rememberUpdatedState(onEffect)
    val lifecycleOwner = LocalLifecycleOwner.current
    val pending by host.effects.collectAsStateWithLifecycle()
    val current = pending.firstOrNull() ?: return

    LaunchedEffect(current.id, lifecycleOwner) {
        var handled = false
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            if (!handled) {
                handler(current.effect)
                handled = true
                host.effectHandled(current.id)
            }
        }
    }
}
