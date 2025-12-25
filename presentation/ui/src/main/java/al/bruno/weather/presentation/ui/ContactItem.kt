package al.bruno.weather.presentation.ui

import al.bruno.presentation.ui.R
import al.bruno.weather.presentation.model.CacheSearchUiModel
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear

@Composable
fun ContactItem(
    contact: CacheSearchUiModel,
    onClick: (CacheSearchUiModel) -> Unit,
    onDelete: (CacheSearchUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = { onClick(contact) },
        shape = RoundedCornerShape(2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(8.dp),
                text = contact.query,
                style = MaterialTheme.typography.bodyMedium
            )
            IconButton(onClick = {
                onDelete(contact)
            }) {
                Icon(
                    imageVector = Icons.Outlined.Clear,
                    contentDescription = stringResource(R.string.delete)
                )
            }
        }
    }
}