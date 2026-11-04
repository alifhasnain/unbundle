package com.alifhasnain.unbundle.ui

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.SystemClock
import android.provider.Settings
import android.text.format.DateFormat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alifhasnain.unbundle.ApkInfo
import com.alifhasnain.unbundle.R
import com.alifhasnain.unbundle.Step
import com.alifhasnain.unbundle.UiState
import com.alifhasnain.unbundle.UnbundleViewModel
import com.alifhasnain.unbundle.ui.theme.Livery
import com.alifhasnain.unbundle.ui.theme.TicketRed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date

private const val APK_MIME = "application/vnd.android.package-archive"
private val StubHeight = 76.dp
private val ButtonShape = RoundedCornerShape(6.dp)
// One dash geometry and one beat for every marching line, so they read as the same ink.
private const val MarchMillis = 700
private val DashOn = 6.dp
private val DashOff = 5.dp
private val LocalReduceMotion = staticCompositionLocalOf { false }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnbundleScreen(vm: UnbundleViewModel = viewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    val reduceMotion = remember {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }
    val now by produceState(SystemClock.elapsedRealtime(), state.working) {
        while (state.working) {
            value = SystemClock.elapsedRealtime()
            delay(100)
        }
    }

    val pick = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri -> uri?.let(vm::convert) }
    val save = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument(APK_MIME)) { uri ->
        if (uri != null) scope.launch { snackbar.showSnackbar(vm.save(uri) ?: "APK saved") }
    }
    // The picker filters by MIME type only. Android stores .aab as octet-stream (older builds: x-authorware-bin),
    // so this hides images, documents and APKs but can still list other untyped binaries. Converter validates the pick.
    val chooseBundle = { pick.launch(arrayOf("application/octet-stream", "application/x-authorware-bin")) }
    val copy = { text: String, what: String ->
        scope.launch {
            clipboard.setClipEntry(ClipEntry(ClipData.newPlainText(what, text)))
            // Android 13+ confirms clipboard writes itself.
            if (Build.VERSION.SDK_INT < 33) snackbar.showSnackbar("$what copied")
        }
        Unit
    }

    CompositionLocalProvider(LocalReduceMotion provides reduceMotion) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = { Header(state, onNew = chooseBundle) },
            bottomBar = {
                Actions(
                    state,
                    onChoose = chooseBundle,
                    onSave = { state.apk?.let { save.launch("${it.packageName}-${it.versionName ?: it.versionCode}.apk") } },
                    onInstall = { state.apk?.let { apk -> install(context, apk)?.let { scope.launch { snackbar.showSnackbar(it) } } } },
                )
            },
            snackbarHost = { SnackbarHost(snackbar) },
        ) { padding ->
            Box(
                Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.TopCenter,
            ) {
                Column(
                    Modifier.widthIn(max = 560.dp).fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Stubs(state, now)
                    Ticket(state, now, onCopy = copy)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Header(state: UiState, onNew: () -> Unit) {
    Column(Modifier.background(Livery)) {
        TopAppBar(
            title = { Text("UNBUNDLE") },
            actions = {
                if (state.bundle != null && !state.working) {
                    IconButton(onClick = onNew) { Icon(painterResource(R.drawable.ic_add), "Choose another bundle") }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Livery,
                titleContentColor = Color.White,
                actionIconContentColor = Color.White,
            ),
        )
        val bundle = state.bundle
        Text(
            text = if (bundle == null) "Choose an .aab to convert on this phone" else listOfNotNull(bundle.name, bundle.sizeBytes?.let(::size)).joinToString(" · "),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.86f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
        )
        // Livery cheatline.
        Box(Modifier.fillMaxWidth().height(4.dp).background(TicketRed))
    }
}

private enum class StubStatus { Pending, Active, Used, Void }

@Composable
private fun Stubs(state: UiState, now: Long) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Step.entries.forEach { step ->
            val t = state.steps[step.ordinal]
            val status = when {
                t.start > 0 && t.end == 0L && state.error != null -> StubStatus.Void
                t.end > 0 -> StubStatus.Used
                t.start > 0 -> StubStatus.Active
                else -> StubStatus.Pending
            }
            val time = when (status) {
                StubStatus.Pending -> "—"
                StubStatus.Active -> duration(now - t.start)
                StubStatus.Used -> duration(t.end - t.start)
                StubStatus.Void -> "VOID"
            }
            key(step) { Stub(step.name.uppercase(), status, time, Modifier.weight(1f)) }
        }
    }
}

@Composable
private fun Stub(label: String, status: StubStatus, time: String, modifier: Modifier) {
    val colors = MaterialTheme.colorScheme
    val reduceMotion = LocalReduceMotion.current
    val shape = CutCornerShape(6.dp)
    val march = if (status == StubStatus.Active && !reduceMotion) {
        rememberInfiniteTransition(label = "stub").animateFloat(0f, 1f, infiniteRepeatable(tween(MarchMillis, easing = LinearEasing)), label = "march").value
    } else 0f
    // Tear-off: a used stub dips and tilts once, then settles. Saved so a rotation or theme change doesn't replay it.
    var torn by rememberSaveable(status) { mutableStateOf(status != StubStatus.Used || reduceMotion) }
    val tear = remember { Animatable(0f) }
    LaunchedEffect(status) {
        if (!torn) {
            tear.animateTo(0f, keyframes { durationMillis = 420; 1f at 110; 0f at 420 })
            torn = true
        }
    }
    val border = when (status) {
        StubStatus.Pending -> colors.outline
        StubStatus.Active -> colors.primary
        StubStatus.Used -> colors.tertiary
        StubStatus.Void -> colors.error
    }
    val fill = when (status) {
        StubStatus.Used -> colors.tertiaryContainer
        StubStatus.Void -> colors.error.copy(alpha = 0.1f)
        else -> colors.surface
    }
    Column(
        modifier
            .graphicsLayer {
                rotationZ = -4f * tear.value
                translationY = 6.dp.toPx() * tear.value
            }
            .background(fill, shape)
            .drawBehind {
                val dash = DashOn.toPx()
                val gap = DashOff.toPx()
                val dashed = status == StubStatus.Pending || status == StubStatus.Active
                drawOutline(
                    shape.createOutline(size, layoutDirection, this),
                    border,
                    style = Stroke(
                        width = (if (status == StubStatus.Active) 2 else 1).dp.toPx(),
                        pathEffect = if (dashed) PathEffect.dashPathEffect(floatArrayOf(dash, gap), -march * (dash + gap)) else null,
                    ),
                )
            }
            .semantics(mergeDescendants = true) { stateDescription = status.name }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = if (status == StubStatus.Pending) colors.onSurfaceVariant else colors.onSurface,
            maxLines = 1,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            time,
            style = MaterialTheme.typography.titleMedium.tabular(),
            color = when (status) {
                StubStatus.Pending -> colors.onSurfaceVariant
                StubStatus.Void -> colors.error
                StubStatus.Used -> colors.onTertiaryContainer
                StubStatus.Active -> colors.tertiary
            },
            maxLines = 1,
        )
    }
}

@Composable
private fun Ticket(state: UiState, now: Long, onCopy: (String, String) -> Unit) {
    val colors = MaterialTheme.colorScheme
    val shape = remember { TicketShape(StubHeight) }
    Box {
        Column(
            Modifier
                .fillMaxWidth()
                .shadow(3.dp, shape, ambientColor = Livery, spotColor = Livery)
                .background(colors.surface, shape)
                .border(1.dp, colors.primary.copy(alpha = 0.55f), shape)
                .clip(shape),
        ) {
            Row(
                Modifier.fillMaxWidth().background(TicketRed).padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("UNIVERSAL APK", style = MaterialTheme.typography.labelMedium, color = Color.White)
                Text(
                    when {
                        state.error != null -> "VOID"
                        state.apk != null -> "VALID"
                        state.working -> "PRINTING"
                        else -> "NOT ISSUED"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                )
            }
            Route(state, now)
            Fields(state, onCopy)
            BarcodeStub(state.apk, void = state.error != null, onCopy)
        }
        // Stamped across the stub, where the APK's barcode would have printed; it clears every line of text.
        if (state.error != null) VoidStamp(Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp))
    }
}

@Composable
private fun Route(state: UiState, now: Long) {
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current
    val reduceMotion = LocalReduceMotion.current
    val legs = Step.entries.size
    val target = if (state.apk != null) 1f else state.steps.count { it.end > 0 } / legs.toFloat()
    val progress by animateFloatAsState(target, tween(if (reduceMotion) 0 else 700), label = "plane")
    val flying = state.working && !reduceMotion
    val transition = rememberInfiniteTransition(label = "route")
    val march by transition.animateFloat(0f, 1f, infiniteRepeatable(tween(MarchMillis, easing = LinearEasing)), label = "dashes")
    val bob by transition.animateFloat(-1f, 1f, infiniteRepeatable(tween(MarchMillis), RepeatMode.Reverse), label = "bob")
    val plane = painterResource(R.drawable.ic_flight)
    val planeColor = colors.secondary

    val started = state.steps.first().start
    val ended = state.steps.maxOf { it.end }
    val departed = if (state.bundle == null) "—" else DateFormat.getTimeFormat(context).format(Date(state.departedAt))
    val (arrivalLabel, arrival) = when {
        started == 0L -> "ARR" to "—"
        state.apk != null -> "ARR IN" to duration(ended - started)
        state.error != null -> "STOPPED" to duration(ended.coerceAtLeast(now) - started)
        else -> "EN ROUTE" to duration(now - started)
    }

    Column(Modifier.padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 10.dp)) {
        Row(verticalAlignment = Alignment.Top) {
            Endpoint("AAB", "DEP", departed, Alignment.Start)
            Canvas(Modifier.weight(1f).height(40.dp).padding(horizontal = 18.dp)) {
                val y = size.height / 2
                val x = size.width * progress
                val dash = DashOn.toPx()
                val gap = DashOff.toPx()
                val stroke = 2.dp.toPx()
                drawLine(colors.primary, Offset(0f, y), Offset(x, y), stroke)
                drawLine(
                    colors.outline, Offset(x, y), Offset(size.width, y), stroke,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(dash, gap), if (flying) -march * (dash + gap) else 0f),
                )
                for (i in 1 until legs) {
                    val wx = size.width * i / legs
                    drawCircle(if (wx <= x + 1f) planeColor else colors.outline, 3.5.dp.toPx(), Offset(wx, y))
                }
                val s = 26.dp.toPx()
                translate(x - s / 2, y - s / 2 + if (flying) bob * 2.dp.toPx() else 0f) {
                    with(plane) { draw(Size(s, s), colorFilter = ColorFilter.tint(planeColor)) }
                }
            }
            Endpoint("APK", arrivalLabel, arrival, Alignment.End)
        }
    }
}

/** An airport-code pair: the code, then a flight-data time under it. */
@Composable
private fun Endpoint(code: String, label: String, time: String, align: Alignment.Horizontal) {
    val timeColor = if (time == "—") MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.tertiary
    Column(horizontalAlignment = align) {
        Text(
            code,
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 34.sp, letterSpacing = 3.sp),
            color = MaterialTheme.colorScheme.primary,
        )
        Row {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.alignByBaseline(),
            )
            Spacer(Modifier.width(6.dp))
            Text(time, style = MaterialTheme.typography.titleMedium.tabular(), color = timeColor, modifier = Modifier.alignByBaseline())
        }
    }
}

@Composable
private fun Fields(state: UiState, onCopy: (String, String) -> Unit) {
    val apk = state.apk
    val divider = MaterialTheme.colorScheme.outlineVariant
    Column(Modifier.padding(horizontal = 16.dp)) {
        HorizontalDivider(color = divider)
        if (state.error != null) {
            // A void ticket carries only its reason; empty data rows would push it under the action bar.
            Field("REASON", state.error, 0, plain = true)
        } else {
            Field(
                "APP · PACKAGE", apk?.packageName, 1,
                overline = apk?.label,
                leading = apk?.let { { AppIcon(it) } },
                onClick = apk?.let { { onCopy(it.packageName, "Package name") } }, onClickLabel = "Copy package name",
            )
            HorizontalDivider(color = divider)
            Row(Modifier.height(IntrinsicSize.Min)) {
                Field("VERSION NAME", apk?.versionName, 2, Modifier.weight(1f), big = true)
                VerticalDivider(color = divider, modifier = Modifier.padding(end = 12.dp))
                Field("VERSION CODE", apk?.versionCode?.toString(), 3, Modifier.weight(1f), big = true)
            }
            HorizontalDivider(color = divider)
            Row(Modifier.height(IntrinsicSize.Min)) {
                Field("MIN / TARGET SDK", apk?.let { "${it.minSdk} / ${it.targetSdk}" }, 4, Modifier.weight(1f))
                VerticalDivider(color = divider, modifier = Modifier.padding(end = 12.dp))
                Field("APK SIZE", apk?.let { size(it.sizeBytes) }, 5, Modifier.weight(1f))
            }
        }
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun AppIcon(apk: ApkInfo) {
    val bitmap = remember(apk) { apk.icon.asImageBitmap() }
    Image(bitmap, contentDescription = null, modifier = Modifier.size(28.dp))
    Spacer(Modifier.width(10.dp))
}

/** One ruled ticket box. A value carbon-strikes in when it first arrives. */
@Composable
private fun Field(
    label: String,
    value: String?,
    index: Int,
    modifier: Modifier = Modifier,
    big: Boolean = false,
    plain: Boolean = false,
    overline: String? = null,
    leading: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    onClickLabel: String? = null,
) {
    val colors = MaterialTheme.colorScheme
    val reduceMotion = LocalReduceMotion.current
    // Saved so a rotation or theme change shows the value as already struck.
    var struck by rememberSaveable(value) { mutableStateOf(value == null || reduceMotion) }
    val strike = remember(value) { Animatable(if (struck) 1f else 0f) }
    LaunchedEffect(value) {
        if (!struck) {
            delay(index * 70L)
            strike.animateTo(1f, tween(320))
            struck = true
        }
    }
    val style = when {
        plain -> MaterialTheme.typography.bodyLarge
        big -> MaterialTheme.typography.headlineMedium.tabular()
        else -> MaterialTheme.typography.titleMedium.tabular()
    }
    Column(
        modifier
            .fillMaxHeight()
            .then(if (onClick != null) Modifier.clickable(onClickLabel = onClickLabel, onClick = onClick) else Modifier)
            .padding(vertical = 8.dp),
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = colors.onSurfaceVariant)
        Spacer(Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            leading?.invoke()
            // Carbon strike: the figure lands slightly off register, then settles into it.
            Column(
                Modifier.graphicsLayer {
                    alpha = strike.value
                    translationX = 3.dp.toPx() * (1f - strike.value)
                    translationY = -2.dp.toPx() * (1f - strike.value)
                },
            ) {
                overline?.let { Text(it, style = MaterialTheme.typography.bodyLarge, color = colors.onSurface) }
                Text(
                    value ?: "—",
                    style = style,
                    color = when {
                        value == null -> colors.onSurfaceVariant
                        plain -> colors.onSurface
                        else -> colors.tertiary
                    },
                )
            }
        }
    }
}

@Composable
private fun BarcodeStub(apk: ApkInfo?, void: Boolean, onCopy: (String, String) -> Unit) {
    val colors = MaterialTheme.colorScheme
    val bits = remember(apk) {
        apk?.sha256?.flatMap { c -> c.digitToInt(16).let { n -> (3 downTo 0).map { n shr it and 1 == 1 } } }.orEmpty()
    }
    Column(
        Modifier
            .fillMaxWidth()
            .height(StubHeight)
            .drawBehind {
                // Perforation between the notches.
                val inset = 14.dp.toPx()
                drawLine(
                    colors.outline, Offset(inset, 0f), Offset(size.width - inset, 0f), 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 4.dp.toPx())),
                )
            }
            .then(if (apk != null) Modifier.clickable(onClickLabel = "Copy SHA-256") { onCopy(apk.sha256, "SHA-256") } else Modifier)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        if (apk == null) {
            // A void stub is left blank for the VOID stamp.
            if (!void) {
                Text("SHA-256 prints here after conversion", style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
            }
        } else {
            val ink = colors.onSurface
            Canvas(Modifier.fillMaxWidth().height(28.dp).semantics { contentDescription = "Barcode of the APK's SHA-256" }) {
                val w = size.width / bits.size
                bits.forEachIndexed { i, on -> if (on) drawRect(ink, Offset(i * w, 0f), Size(w + 0.5f, size.height)) }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "SHA-256  " + apk.sha256.take(12).uppercase() + "…" + apk.sha256.takeLast(12).uppercase(),
                style = MaterialTheme.typography.labelSmall.tabular(),
                color = colors.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun VoidStamp(modifier: Modifier) {
    val color = MaterialTheme.colorScheme.error
    val reduceMotion = LocalReduceMotion.current
    var stamped by rememberSaveable { mutableStateOf(reduceMotion) }
    val hit = remember { Animatable(if (stamped) 1f else 0f) }
    LaunchedEffect(Unit) {
        if (!stamped) {
            hit.animateTo(1f, tween(260))
            stamped = true
        }
    }
    Text(
        "VOID",
        style = MaterialTheme.typography.displaySmall.copy(fontSize = 52.sp, letterSpacing = 8.sp),
        color = color,
        modifier = modifier
            .graphicsLayer {
                rotationZ = -8f
                alpha = 0.9f * hit.value
                val scale = 1.6f - 0.6f * hit.value
                scaleX = scale
                scaleY = scale
            }
            .border(4.dp, color, RoundedCornerShape(6.dp))
            .padding(horizontal = 18.dp)
            .semantics { contentDescription = "Conversion failed" },
    )
}

@Composable
private fun Actions(state: UiState, onChoose: () -> Unit, onSave: () -> Unit, onInstall: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .wrapContentWidth()
                .widthIn(max = 560.dp)
                .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 16.dp),
        ) {
            // Kept beside the buttons so the caveat is read before tapping Install.
            Text(
                text = when {
                    state.error != null -> "Nothing was saved or installed."
                    state.apk != null -> "Signed with this phone's own key. Uninstall any Play Store copy before installing."
                    state.working -> "Large bundles can take a minute or more."
                    else -> "Converts on this phone. Nothing is uploaded."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 10.dp),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (state.bundle == null || state.error != null) {
                    Button(onChoose, Modifier.weight(1f).height(56.dp), shape = ButtonShape) {
                        Text(if (state.error != null) "CHOOSE ANOTHER BUNDLE" else "CHOOSE BUNDLE")
                    }
                } else {
                    FilledTonalButton(
                        onSave, Modifier.weight(1f).height(56.dp), enabled = state.apk != null, shape = ButtonShape,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                    ) {
                        Text("SAVE APK")
                    }
                    Button(onInstall, Modifier.weight(1f).height(56.dp), enabled = state.apk != null, shape = ButtonShape) {
                        Text("INSTALL")
                    }
                }
            }
        }
    }
}

/** Hands the APK to the system installer. Returns an error message, or null when the installer opened. */
private fun install(context: Context, apk: ApkInfo): String? {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.files", apk.file)
    val intent = Intent(Intent.ACTION_VIEW)
        .setDataAndType(uri, APK_MIME)
        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    // File managers also claim APKs; go straight to the system installer instead of a chooser.
    context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_SYSTEM_ONLY).firstOrNull()
        ?.let { intent.setPackage(it.activityInfo.packageName) }
    return try {
        context.startActivity(intent)
        null
    } catch (_: ActivityNotFoundException) {
        "No package installer found on this phone."
    }
}

/** Ticket outline: cut corners and a notch on each side where the barcode stub tears off. */
private class TicketShape(private val notchFromBottom: Dp) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val cut = with(density) { 10.dp.toPx() }
        val r = with(density) { 9.dp.toPx() }
        val y = size.height - with(density) { notchFromBottom.toPx() }
        val w = size.width
        return Outline.Generic(Path().apply {
            moveTo(cut, 0f)
            lineTo(w - cut, 0f)
            lineTo(w, cut)
            lineTo(w, y - r)
            arcTo(Rect(w - r, y - r, w + r, y + r), 270f, -180f, false)
            lineTo(w, size.height - cut)
            lineTo(w - cut, size.height)
            lineTo(cut, size.height)
            lineTo(0f, size.height - cut)
            lineTo(0f, y + r)
            arcTo(Rect(-r, y - r, r, y + r), 90f, -180f, false)
            lineTo(0f, cut)
            close()
        })
    }
}

private fun TextStyle.tabular() = copy(fontFeatureSettings = "tnum")

private fun size(bytes: Long) = if (bytes >= 1_000_000) "%.1f MB".format(bytes / 1e6) else "%d KB".format(bytes / 1000)

private fun duration(ms: Long) = if (ms < 60_000) "%.1f s".format(ms / 1000f) else "%d:%02d".format(ms / 60_000, ms / 1000 % 60)
