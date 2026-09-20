package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.MizenStrings
import com.example.data.model.VerificationStatus
import com.example.ui.theme.MizenGold
import com.example.ui.theme.MizenInfo
import com.example.ui.theme.MizenPrimary
import com.example.ui.theme.MizenWarning

@Composable
fun MizenEligibilityDisclaimer(
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MizenPrimary.copy(alpha = 0.06f))
            .border(1.dp, MizenPrimary.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Information légale",
                tint = MizenPrimary,
                modifier = Modifier.size(20.dp)
            )
            Column {
                Text(
                    text = when (lang) {
                        AppLanguage.AR -> "تنبيه الشفافية والمطابقة"
                        AppLanguage.EN -> "Transparency & Eligibility Notice"
                        AppLanguage.FR -> "Notice de transparence & éligibilité"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MizenPrimary
                )
                Spacer(modifier = Modifier.size(2.dp))
                Text(
                    text = MizenStrings.disclaimer(lang),
                    fontSize = 11.5.sp,
                    lineHeight = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun VerificationBadge(
    status: String,
    lang: AppLanguage,
    date: String? = null,
    modifier: Modifier = Modifier
) {
    val (bg, textColor, label) = when (status) {
        VerificationStatus.VERIFIED.name -> Triple(
            Color(0xFFE0F2FE),
            Color(0xFF0369A1),
            when (lang) {
                AppLanguage.AR -> "مصدر رسمي مؤكد"
                AppLanguage.EN -> "Verified Source"
                AppLanguage.FR -> "Source officielle vérifiée"
            }
        )
        VerificationStatus.NEEDS_VERIFICATION.name -> Triple(
            Color(0xFFFEF3C7),
            Color(0xFFB45309),
            when (lang) {
                AppLanguage.AR -> "يتطلب تدقيقاً"
                AppLanguage.EN -> "Needs verification"
                AppLanguage.FR -> "À vérifier auprès du prêteur"
            }
        )
        else -> Triple(
            Color(0xFFF1F5F9),
            Color(0xFF475569),
            when (lang) {
                AppLanguage.AR -> "بيانات عامة جزئية"
                AppLanguage.EN -> "Incomplete public data"
                AppLanguage.FR -> "Données partielles"
            }
        )
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.VerifiedUser,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = if (date != null) "$label • $date" else label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}
