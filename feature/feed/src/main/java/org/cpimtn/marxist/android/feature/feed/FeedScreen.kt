package org.cpimtn.marxist.android.feature.feed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FeedScreen() {
    Column {
        Text("கட்டுரைகள்", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            "நடுநிலையை உரசிப்பார்ப்பதற்கான உரைகற்களில் ஒன்றாக வர்க்க நிலைப்பாடு குறித்துப் பார்த்தோம். இப்போது இன்னொரு உரைகல்லான  பாலின அணுகுமுறையை விசாரிப்போம். பெரும்பாலான ஊடகங்களில் பெண்களைப் பற்றிய சித்தரிப்புகள் பாலின சமத்துவக் கண்ணோட்டத்தைப் புறக்கணிப்பதாக, சமுதாயத்தில் ஊறிப்போயிருக்கிற பெண் அவமதிப்புக் கலாச்சாரத்தை  வெளிப்படுத்துவதாக இருப்பதைக் கவனித்தால் அதிர்ச்சியே ஏற்படும். \n" +
                    "\n" +
                    "ஊடகங்கள் சமூகத்தின் கண்ணாடியாகச் செயல்படுகின்றன என்று எடுத்துக்கொள்வதானால், ஆணாதிக்க சமூகத்தின் பார்வைகளையே அவை பிரதிபலிக்கின்றன என்று உரக்கச் சொல்லலாம். பெண்கள் அதிகாரம், பொறுப்பு, தலைமைப் பாங்குகளில் குறைத்தே காட்டப்படுகிறார்கள். திரைப்படம், தொலைக்காட்சித் தொடர்கள், விளம்பரங்கள் போன்றவற்றில் பெண்கள் எவ்வாறு சித்தரிக்கப்படுகிறார்கள்?",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}