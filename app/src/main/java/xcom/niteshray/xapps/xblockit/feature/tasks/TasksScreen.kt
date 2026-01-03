package xcom.niteshray.xapps.xblockit.feature.tasks

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import xcom.niteshray.xapps.xblockit.ui.theme.*

// Priority now uses grayscale for minimalistic design
enum class TaskPriority(val color: Color, val label: String, val icon: String) {
    HIGH(PureWhite, "High", "●"),
    MEDIUM(MediumGray, "Medium", "◐"),
    LOW(DimGray, "Low", "○")
}

enum class TaskCategory(val icon: String, val label: String) {
    WORK("💼", "Work"),
    PERSONAL("👤", "Personal"),
    HEALTH("💪", "Health"),
    LEARNING("📚", "Learning"),
    OTHER("📌", "Other")
}

data class Task(
    val id: Int,
    val title: String,
    val category: TaskCategory = TaskCategory.PERSONAL,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val isCompleted: Boolean = false,
    val dueDate: LocalDate = LocalDate.now()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen() {
    var tasks by remember {
        mutableStateOf(
            listOf(
                Task(1, "Complete project proposal", TaskCategory.WORK, TaskPriority.HIGH),
                Task(2, "30 min morning workout", TaskCategory.HEALTH, TaskPriority.MEDIUM),
                Task(3, "Read 20 pages", TaskCategory.LEARNING, TaskPriority.LOW),
                Task(4, "Reply to important emails", TaskCategory.WORK, TaskPriority.HIGH),
                Task(5, "Meditate for 10 mins", TaskCategory.HEALTH, TaskPriority.LOW)
            )
        )
    }
    
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf<TaskCategory?>(null) }
    
    val filteredTasks = remember(tasks, selectedFilter) {
        if (selectedFilter == null) tasks else tasks.filter { it.category == selectedFilter }
    }
    
    val completedCount = tasks.count { it.isCompleted }
    val totalCount = tasks.size
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header Section - Clean black with white text
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(20.dp)
                .padding(top = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "My Tasks",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMM d")),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Add Task Button - White with shine
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(48.dp)
                        .border(
                            width = 1.dp,
                            color = GlowWhite,
                            shape = CircleShape
                        )
                ) {
                    Icon(Icons.Default.Add, "Add Task")
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Progress Card with shine border
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.border(
                    width = 1.dp,
                    color = BorderGlow,
                    shape = RoundedCornerShape(16.dp)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Circular Progress
                    Box(
                        modifier = Modifier.size(60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { 1f },
                            modifier = Modifier.fillMaxSize(),
                            color = DarkBorder,
                            strokeWidth = 6.dp,
                        )
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 6.dp,
                        )
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column {
                        Text(
                            text = "Today's Progress",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$completedCount of $totalCount tasks completed",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        
        // Category Filter
        LazyRow(
            modifier = Modifier.padding(vertical = 16.dp),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    label = "All",
                    icon = "📋",
                    isSelected = selectedFilter == null,
                    onClick = { selectedFilter = null }
                )
            }
            items(TaskCategory.entries.toTypedArray()) { category ->
                FilterChip(
                    label = category.label,
                    icon = category.icon,
                    isSelected = selectedFilter == category,
                    onClick = { selectedFilter = if (selectedFilter == category) null else category }
                )
            }
        }
        
        // Tasks List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Pending Tasks
            val pendingTasks = filteredTasks.filter { !it.isCompleted }
            if (pendingTasks.isNotEmpty()) {
                item {
                    Text(
                        text = "To Do (${pendingTasks.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                items(pendingTasks, key = { it.id }) { task ->
                    TaskItem(
                        task = task,
                        onToggle = { 
                            tasks = tasks.map { 
                                if (it.id == task.id) it.copy(isCompleted = !it.isCompleted) 
                                else it 
                            }
                        },
                        onDelete = { tasks = tasks.filter { it.id != task.id } }
                    )
                }
            }
            
            // Completed Tasks
            val completedTasks = filteredTasks.filter { it.isCompleted }
            if (completedTasks.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Completed (${completedTasks.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                items(completedTasks, key = { it.id }) { task ->
                    TaskItem(
                        task = task,
                        onToggle = { 
                            tasks = tasks.map { 
                                if (it.id == task.id) it.copy(isCompleted = !it.isCompleted) 
                                else it 
                            }
                        },
                        onDelete = { tasks = tasks.filter { it.id != task.id } }
                    )
                }
            }
            
            // Empty State
            if (filteredTasks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🎉", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "All done!",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "No tasks in this category",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
    
    // Add Task Dialog
    if (showAddDialog) {
        AddTaskDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { title, category, priority ->
                tasks = tasks + Task(
                    id = (tasks.maxOfOrNull { it.id } ?: 0) + 1,
                    title = title,
                    category = category,
                    priority = priority
                )
                showAddDialog = false
            }
        )
    }
}

@Composable
fun FilterChip(
    label: String,
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        animationSpec = tween(200),
        label = "chipBg"
    )
    
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        modifier = Modifier
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                color = if (isSelected) GlowWhite else BorderGlow,
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (task.isCompleted) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (task.isCompleted) 0.6f else 1f,
        animationSpec = tween(200),
        label = "alpha"
    )
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .border(
                width = 1.dp,
                color = if (task.isCompleted) Color.Transparent else BorderGlow,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox - White border, white fill when checked
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(
                        if (task.isCompleted)
                            MaterialTheme.colorScheme.primary
                        else
                            Color.Transparent
                    )
                    .border(
                        width = 2.dp,
                        color = if (task.isCompleted) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            task.priority.color,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(14.dp))
            
            // Task Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = task.category.icon,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = task.category.label,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    // Priority indicator - grayscale dot
                    Text(
                        text = task.priority.icon,
                        fontSize = 10.sp,
                        color = task.priority.color.copy(alpha = alpha)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = task.priority.label,
                        fontSize = 12.sp,
                        color = task.priority.color.copy(alpha = alpha)
                    )
                }
            }
            
            // Delete Button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onAdd: (String, TaskCategory, TaskPriority) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(TaskCategory.PERSONAL) }
    var priority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                "Add New Task",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    "Category",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(TaskCategory.entries.toTypedArray()) { cat ->
                        FilterChip(
                            label = cat.label,
                            icon = cat.icon,
                            isSelected = category == cat,
                            onClick = { category = cat }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    "Priority",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TaskPriority.entries.forEach { pri ->
                        FilterChip(
                            label = pri.label,
                            icon = pri.icon,
                            isSelected = priority == pri,
                            onClick = { priority = pri }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (title.isNotBlank()) onAdd(title, category, priority) },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Add Task")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text("Cancel")
            }
        }
    )
}
