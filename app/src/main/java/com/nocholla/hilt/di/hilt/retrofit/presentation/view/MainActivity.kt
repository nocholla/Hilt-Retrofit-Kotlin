package com.nocholla.hilt.di.hilt.retrofit.presentation.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nocholla.hilt.di.hilt.retrofit.domain.model.Task
import com.nocholla.hilt.di.hilt.retrofit.presentation.viewmodel.TaskViewModel
import com.nocholla.hilt.di.hilt.retrofit.presentation.theme.HiltRetrofitTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskApp() {
    val viewModel: TaskViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showDialog by remember { mutableStateOf(false) }

    HiltRetrofitTheme {
        Scaffold(
            topBar = { TopAppBar(title = { Text("My Task List") }) },
            floatingActionButton = {
                FloatingActionButton(onClick = { showDialog = true }) {
                    Icon(Icons.Filled.Add, "Add new task")
                }
            }
        ) { paddingValues ->
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
            ) {
                when {
                    uiState.isLoading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    uiState.error != null -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Error: ${uiState.error}",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    uiState.tasks.isEmpty() -> {
                        EmptyStateView(Modifier.fillMaxSize())
                    }
                    else -> {
                        TaskList(
                            tasks = uiState.tasks,
                            onToggleCompletion = { taskId -> viewModel.toggleTaskCompletion(taskId) },
                            onRemoveTask = { taskId -> viewModel.removeTask(taskId) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "© 2025 My Compose App",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp)
                )
            }

            if (showDialog) {
                NewTaskDialog(
                    onAddTask = { taskName ->
                        viewModel.addTask(taskName)
                        showDialog = false
                    },
                    onDismiss = { showDialog = false }
                )
            }
        }
    }
}

@Composable
fun TaskList(
    tasks: List<Task>,
    onToggleCompletion: (Int) -> Unit,
    onRemoveTask: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.padding(horizontal = 8.dp)) {
        items(tasks, key = { it.id }) { task ->
            TaskItem(
                task,
                onToggleCompletion,
                onRemoveTask
            )
            Divider()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskItem(
    task: Task,
    onToggleCompletion: (Int) -> Unit,
    onRemoveTask: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = task.name,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .padding(4.dp),
                    color = if (task.isCompleted) Color.Gray else MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (task.isCompleted) FontWeight.Light else FontWeight.Normal,
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            leadingContent = {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = if (task.isCompleted) Color.LightGray else MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (task.isCompleted) Color.Transparent else MaterialTheme.colorScheme.primary)
                        .padding(8.dp)
                )
            },
            trailingContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = task.isCompleted,
                        onCheckedChange = { onToggleCompletion(task.id) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary,
                            uncheckedColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(onClick = { onRemoveTask(task.id) }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Remove task",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            modifier = Modifier.clickable { onToggleCompletion(task.id) }
        )
    }
}

@Composable
fun EmptyStateView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "No tasks",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "No tasks yet! Click '+' to add one.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun NewTaskDialog(
    onAddTask: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var taskName by remember { mutableStateOf("") }
    val isTaskNameValid = taskName.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Task") },
        text = {
            OutlinedTextField(
                value = taskName,
                onValueChange = { taskName = it },
                label = { Text("Task Name") },
                singleLine = true,
                isError = !isTaskNameValid && taskName.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isTaskNameValid) {
                        onAddTask(taskName)
                    }
                },
                enabled = isTaskNameValid
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true, name = "Light Theme Preview")
@Composable
fun TaskAppPreviewLight() {
    HiltRetrofitTheme(darkTheme = false) {
        TaskApp()
    }
}

@Preview(showBackground = true, name = "Dark Theme Preview")
@Composable
fun TaskAppPreviewDark() {
    HiltRetrofitTheme(darkTheme = true) {
        TaskApp()
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyStatePreview() {
    HiltRetrofitTheme {
        EmptyStateView(
            Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TaskItemPreview() {
    HiltRetrofitTheme {
        Column {
            TaskItem(
                Task(0, "Buy groceries", false),
                {},
                {})
            TaskItem(
                Task(1, "Finish report", true),
                {},
                {})
        }
    }
}