package al.bruno.presentation.ui

import al.bruno.weather.presentation.model.CacheSearchUiModel
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchEngine(
    query: String,
    itemsList: List<CacheSearchUiModel>,
    modifier: Modifier = Modifier,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onItemSelected: (CacheSearchUiModel) -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    contentDescription: String,
    shape: RoundedCornerShape = RoundedCornerShape(8.dp),
    itemContent: @Composable (CacheSearchUiModel, (CacheSearchUiModel) -> Unit) -> Unit,
) {
    // Manage search state internally
    var isSearching by remember { mutableStateOf(false) }

    // Toggle search state and clear the query when closed
    val onToggleSearch: () -> Unit = {
        isSearching = !isSearching
    }
    SearchBarInput(
        query = query,
        onQueryChange = onQueryChange,
        onSearch = onSearch,
        expanded = isSearching,
        onExpandedChange = { onToggleSearch() },
        modifier = modifier.fillMaxWidth(),
        leadingIcon = leadingIcon ?: {
            if (isSearching) {
                IconButton(onClick = {
                    onToggleSearch()
                    onSearch(query)
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = contentDescription
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = contentDescription
                )
            }
        },
        trailingIcon = {
            if (isSearching) {
                IconButton(onClick = {
                    onSearch("")
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Clear,
                        contentDescription = contentDescription
                    )
                }
            }
        },
        placeholder = placeholder,
        shape = shape,
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(
                count = itemsList.size,
                itemContent = { index ->
                    itemContent(itemsList[index]) {
                        onItemSelected(itemsList[index])
                        onToggleSearch()
                    }
                }
            )
        }

    }
}