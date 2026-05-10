package io.ii.presentation.components.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastRoundToInt
import io.ii.presentation.theme.TaskDecomposeComponentDefaults
import io.ii.presentation.components.inputs.OptionalDescriptionInput
import io.ii.presentation.components.inputs.TaskTitleInput
import io.ii.presentation.states.SubtaskState
import io.ii.presentation.theme.LocalDimensions
import io.ii.presentation.screens.PreviewScreen
import kotlinx.coroutines.delay

/**
 * Задержка между последовательным появлением строк дерева.
 */
private const val ANIMATION_DELAY = 160L

/**
 * Длительность fade/expand/slide-анимации одной строки дерева.
 */
private const val ANIMATION_DURATION = 200

/**
 * Базовая задержка между появлением символов в typewriter-анимации.
 */
private const val TYPEWRITER_CHARACTER_DELAY = 24L

@Composable
internal fun TaskTreeCard(
    rootTitle: String,
    subtasks: List<SubtaskState>,
    modifier: Modifier = Modifier,
    enableTypingAnimation: Boolean = true,
    animationSpeedCoefficient: Float = 1f
) {
    val dimensions = LocalDimensions.current
    val nodes = remember(subtasks) { subtasks.flattenTree() }
    val animationSpeed = animationSpeedCoefficient.coerceIn(1f, 10f).fastRoundToInt()

    Card(
        modifier = modifier,
        colors = TaskDecomposeComponentDefaults.cardColors()
    ) {
        Column(
            modifier = Modifier.padding(vertical = dimensions.padding.paddingM)
        ) {
            TaskTreeRootItem(title = rootTitle)

            nodes.forEach { node ->
                AnimatedTaskTreeItem(
                    node = node,
                    enableTypingAnimation = enableTypingAnimation,
                    animationSpeedCoefficient = animationSpeed
                )
            }
        }
    }
}

private data class TaskTreeNodeUi(
    val item: SubtaskState,
    val parentContinuations: List<Boolean>,
    val isLast: Boolean,
    val indexInTree: Int
)

@Composable
private fun AnimatedTaskTreeItem(
    node: TaskTreeNodeUi,
    enableTypingAnimation: Boolean,
    animationSpeedCoefficient: Int,
    modifier: Modifier = Modifier
) {
    var visible by remember(node.item.id) { mutableStateOf(false) }

    LaunchedEffect(node.item.id) {
        delay(node.indexInTree * ANIMATION_DELAY / animationSpeedCoefficient)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(durationMillis = ANIMATION_DURATION / animationSpeedCoefficient)
        ) + expandVertically(
            animationSpec = tween(durationMillis = ANIMATION_DURATION / animationSpeedCoefficient),
            expandFrom = Alignment.Top
        ) + slideInVertically(
            animationSpec = tween(durationMillis = ANIMATION_DURATION / animationSpeedCoefficient),
            initialOffsetY = { -it / 3 }
        ),
        modifier = modifier
    ) {
        TaskTreeItemRow(
            item = node.item,
            parentContinuations = node.parentContinuations,
            isLast = node.isLast,
            enableTypingAnimation = enableTypingAnimation,
            animationSpeedCoefficient = animationSpeedCoefficient
        )
    }
}

@Composable
private fun TaskTreeItemRow(
    item: SubtaskState,
    parentContinuations: List<Boolean>,
    isLast: Boolean,
    enableTypingAnimation: Boolean,
    animationSpeedCoefficient: Int,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.Top
    ) {
        TaskTreeLines(
            modifier = Modifier.fillMaxHeight(),
            parentContinuations = parentContinuations,
            isLast = isLast
        )

        val textModifier = Modifier
            .weight(1f)
            .padding(
                top = dimensions.padding.padding4,
                bottom = dimensions.padding.paddingS
            )

        if (enableTypingAnimation) {
            TypewriterText(
                modifier = textModifier,
                text = item.title,
                animationSpeedCoefficient = animationSpeedCoefficient,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        } else {
            Text(
                modifier = textModifier,
                text = item.title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun TypewriterText(
    text: String,
    animationSpeedCoefficient: Int,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier
) {
    var visibleCharacters by remember(text) { mutableIntStateOf(0) }

    LaunchedEffect(text, animationSpeedCoefficient) {
        visibleCharacters = 0

        text.indices.forEach { index ->
            visibleCharacters = index + 1
            delay(TYPEWRITER_CHARACTER_DELAY / animationSpeedCoefficient)
        }
    }

    Box(modifier = modifier) {
        Text(
            text = text,
            style = style,
            color = Color.Transparent
        )

        Text(
            text = text.take(visibleCharacters),
            style = style,
            color = color
        )
    }
}

private fun List<SubtaskState>.flattenTree(): List<TaskTreeNodeUi> {
    val result = mutableListOf<TaskTreeNodeUi>()

    fun addItems(
        items: List<SubtaskState>,
        parentContinuations: List<Boolean>
    ) {
        items.forEachIndexed { index, item ->
            val isLast = index == items.lastIndex

            result += TaskTreeNodeUi(
                item = item,
                parentContinuations = parentContinuations,
                isLast = isLast,
                indexInTree = result.size
            )

            addItems(
                items = item.subtasks,
                parentContinuations = parentContinuations + !isLast
            )
        }
    }

    addItems(
        items = this,
        parentContinuations = emptyList()
    )

    return result
}

@Composable
private fun TaskTreeRootItem(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier.fillMaxWidth(),
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun TaskTreeLines(
    parentContinuations: List<Boolean>,
    isLast: Boolean,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    val lineColor = MaterialTheme.colorScheme.outlineVariant
    val nestingLevelWidth = 20

    Canvas(
        modifier = modifier.width((parentContinuations.size * nestingLevelWidth + nestingLevelWidth).dp)
    ) {
        val strokeWidth = dimensions.other.treeLineStrokeWidth.toPx()
        val levelWidth = nestingLevelWidth.dp.toPx()
        val centerY = dimensions.other.centerLineHeight.toPx()

        parentContinuations.forEachIndexed { index, shouldContinue ->
            if (shouldContinue) {
                val x = index * levelWidth + levelWidth / 2f

                drawLine(
                    color = lineColor,
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = strokeWidth
                )
            }
        }

        val currentX = parentContinuations.size * levelWidth + levelWidth / 2f

        val branchRadius = dimensions.other.branchRadius.toPx()

        drawLine(
            color = lineColor,
            start = Offset(currentX, 0f),
            end = Offset(
                x = currentX,
                y = if (isLast) {
                    centerY - branchRadius
                } else {
                    size.height
                }
            ),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        val path = Path().apply {

            moveTo(currentX, centerY - branchRadius)

            quadraticTo(
                currentX,
                centerY,
                currentX + branchRadius,
                centerY
            )

            lineTo(size.width, centerY)
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round
            )
        )
    }
}

@Preview
@Composable
private fun TaskTreeCardPreview() {

    val mockSubtasks = listOf(
        SubtaskState(
            id = "1",
            title = "Удалить ненужные программы",
            description = null,
            createdAt = 0L,
            subtasks = listOf(
                SubtaskState(
                    id = "1.1",
                    title = "Открыть список установленных программ",
                    description = null,
                    createdAt = 0L
                ),
                SubtaskState(
                    id = "1.2",
                    title = "Найти редко используемые приложения",
                    description = null,
                    createdAt = 0L
                ),
                SubtaskState(
                    id = "1.3",
                    title = "Удалить ненужные программы",
                    description = null,
                    createdAt = 0L
                )
            )
        ),
        SubtaskState(
            id = "2",
            title = "Очистить временные файлы",
            description = null,
            createdAt = 0L,
            subtasks = listOf(
                SubtaskState(
                    id = "2.1",
                    title = "Очистить корзину",
                    description = null,
                    createdAt = 0L
                ),
                SubtaskState(
                    id = "2.2",
                    title = "Удалить временные файлы системы",
                    description = null,
                    createdAt = 0L
                ),
                SubtaskState(
                    id = "2.3",
                    title = "Очистить папку Downloads",
                    description = null,
                    createdAt = 0L,
                    subtasks = listOf(
                        SubtaskState(
                            id = "2.3.1",
                            title = "Удалить старые архивы",
                            description = null,
                            createdAt = 0L
                        ),
                        SubtaskState(
                            id = "2.3.2",
                            title = "Удалить дубликаты файлов",
                            description = null,
                            createdAt = 0L
                        )
                    )
                )
            )
        ),
        SubtaskState(
            id = "3",
            title = "Проверить автозагрузку",
            description = null,
            createdAt = 0L,
            subtasks = listOf(
                SubtaskState(
                    id = "3.1",
                    title = "Открыть диспетчер задач",
                    description = null,
                    createdAt = 0L
                ),
                SubtaskState(
                    id = "3.2",
                    title = "Отключить лишние программы из автозагрузки",
                    description = null,
                    createdAt = 0L
                )
            )
        ),
        SubtaskState(
            id = "4",
            title = "Проверить компьютер на вредоносное ПО",
            description = null,
            createdAt = 0L,
            subtasks = listOf(
                SubtaskState(
                    id = "4.1",
                    title = "Обновить антивирусные базы",
                    description = null,
                    createdAt = 0L
                ),
                SubtaskState(
                    id = "4.2",
                    title = "Запустить полное сканирование",
                    description = null,
                    createdAt = 0L
                )
            )
        )
    )

    var inputValue by remember { mutableStateOf("Почистить компьютер от мусорных программ и файлов") }

    var input by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(true) }

    var depth by remember { mutableIntStateOf(2) }
    var priority by remember { mutableStateOf(true) }

    PreviewScreen(
        alignment = Alignment.TopStart,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskTitleInput(
                    value = inputValue,
                    isLoading = false,
                    onValueChange = { inputValue = it },
                    onDecomposeClick = {}
                )

                OptionalDescriptionInput(
                    value = input,
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    onValueChange = { input = it }
                )

                DecompositionParamsCard(
                    modifier = Modifier.fillMaxWidth(),
                    depth = depth,
                    hasPriority = priority,
                    onDepthChange = { depth = it },
                    onPriorityChange = { priority = !priority }
                )

                TaskTreeCard(
                    modifier = Modifier.fillMaxWidth(),
                    rootTitle = inputValue,
                    subtasks = mockSubtasks
                )
            }
        }
    )
}
