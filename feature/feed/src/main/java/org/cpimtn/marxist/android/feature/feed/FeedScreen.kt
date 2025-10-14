package org.cpimtn.marxist.android.feature.feed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jskaleel.android.ui.theme.MarxistReaderTheme

@Composable
fun FeedScreen(
    navigateToDetails: (String) -> Unit,
    navigateToSearch: () -> Unit
) {
    Column(modifier = Modifier.verticalScroll(enabled = true, state = rememberScrollState())) {
        Text("கட்டுரைகள்", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "நடுநிலையை உரசிப்பார்ப்பதற்கான உரைகற்களில் ஒன்றாக வர்க்க நிலைப்பாடு குறித்துப் பார்த்தோம். இப்போது இன்னொரு உரைகல்லான  பாலின அணுகுமுறையை விசாரிப்போம். பெரும்பாலான ஊடகங்களில் பெண்களைப் பற்றிய சித்தரிப்புகள் பாலின சமத்துவக் கண்ணோட்டத்தைப் புறக்கணிப்பதாக, சமுதாயத்தில் ஊறிப்போயிருக்கிற பெண் அவமதிப்புக் கலாச்சாரத்தை  வெளிப்படுத்துவதாக இருப்பதைக் கவனித்தால் அதிர்ச்சியே ஏற்படும். \n"
                    + "\n" +
                    "நடுநிலையை உரசிப்பார்ப்பதற்கான உரைகற்களில் ஒன்றாக வர்க்க நிலைப்பாடு குறித்துப் பார்த்தோம். இப்போது இன்னொரு உரைகல்லான  பாலின அணுகுமுறையை விசாரிப்போம். பெரும்பாலான ஊடகங்களில் பெண்களைப் பற்றிய சித்தரிப்புகள் பாலின சமத்துவக் கண்ணோட்டத்தைப் புறக்கணிப்பதாக, சமுதாயத்தில் ஊறிப்போயிருக்கிற பெண் அவமதிப்புக் கலாச்சாரத்தை  வெளிப்படுத்துவதாக இருப்பதைக் கவனித்தால் அதிர்ச்சியே ஏற்படும். \n",
            style = MaterialTheme.typography.bodyMedium
        )
        Button(onClick = { navigateToDetails("123") }) {
            Text("Go to Details")
        }
        Button(onClick = navigateToSearch) {
            Text("Go to Search")
        }
    }
}

@Preview
@Composable
fun FeedScreenPreview() {
    MarxistReaderTheme {

        FeedScreen(
            navigateToDetails = {},
            navigateToSearch = {}
        )
    }
}

