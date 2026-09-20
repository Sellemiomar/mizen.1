package com.example.ui.screens.lead

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.FinancingProductEntity
import com.example.data.local.LeadEntity
import com.example.data.model.AppLanguage
import com.example.ui.components.MizenEligibilityDisclaimer
import com.example.ui.theme.MizenGold
import com.example.ui.theme.MizenPrimary
import com.example.ui.theme.MizenSuccess

@Composable
fun LeadRequestScreen(
    product: FinancingProductEntity,
    onSubmitLead: (fullName: String, phone: String, email: String, governorate: String, notes: String, consent: Boolean) -> Unit,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var governorate by remember { mutableStateOf("Tunis") }
    var notes by remember { mutableStateOf("") }
    var consentGiven by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    val tunisianGovernorates = listOf(
        "Tunis", "Ariana", "Ben Arous", "Manouba", "Nabeul", "Zaghouan", "Bizerte",
        "Béja", "Jendouba", "Le Kef", "Siliana", "Sousse", "Monastir", "Mahdia",
        "Sfax", "Kairouan", "Kasserine", "Sidi Bouzid", "Gabès", "Médenine",
        "Tataouine", "Gafsa", "Tozeur", "Kébili"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Product Summary Banner
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = null,
                        tint = MizenPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (lang == AppLanguage.AR) "طلب مرافقة وتوصيل" else "Demande d'orientation et de mise en relation",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${product.providerName} • ${product.productName}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MizenPrimary
                )
            }
        }

        // Mandatory Disclaimer
        MizenEligibilityDisclaimer(lang = lang)

        // Form Fields
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = if (lang == AppLanguage.AR) "معلومات الاتصال بك" else "Vos coordonnées",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text(if (lang == AppLanguage.AR) "الاسم واللقب" else "Nom complet") },
                    placeholder = { Text("Mohamed Ben Ali") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("lead_input_fullname")
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(if (lang == AppLanguage.AR) "رقم الهاتف في تونس" else "Numéro de téléphone (+216)") },
                    placeholder = { Text("98 123 456") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("lead_input_phone")
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(if (lang == AppLanguage.AR) "البريد الإلكتروني" else "Adresse e-mail") },
                    placeholder = { Text("exemple@domaine.tn") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("lead_input_email")
                )

                OutlinedTextField(
                    value = governorate,
                    onValueChange = { governorate = it },
                    label = { Text(if (lang == AppLanguage.AR) "الولاية (المقر أو موقع المشروع)" else "Gouvernorat d'implantation") },
                    placeholder = { Text("Tunis, Sousse, Sfax...") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("lead_input_gov")
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(if (lang == AppLanguage.AR) "ملاحظات إضافية حول المشروع" else "Précisions sur votre projet") },
                    placeholder = { Text("Ex: Local trouvé, devis proforma prêts...") },
                    maxLines = 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("lead_input_notes")
                )

                // Mandatory Explicit Consent
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { consentGiven = !consentGiven }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = consentGiven,
                        onCheckedChange = { consentGiven = it },
                        colors = CheckboxDefaults.colors(checkedColor = MizenPrimary),
                        modifier = Modifier.testTag("lead_consent_checkbox")
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (lang == AppLanguage.AR) "أوافق على معالجة معطياتي وتوجيه طلبي للاستفادة من المرافقة البنكية حسب معايير ميزان."
                        else "J'autorise Mizen à analyser mon profil et à me contacter pour la structuration de mon dossier.",
                        fontSize = 11.5.sp,
                        lineHeight = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Button(
                    onClick = {
                        if (fullName.isBlank()) {
                            errorMessage = if (lang == AppLanguage.AR) "يرجى إدخال الاسم واللقب" else "Veuillez renseigner votre nom complet."
                        } else if (phone.isBlank() || phone.length < 8) {
                            errorMessage = if (lang == AppLanguage.AR) "يرجى إدخال رقم هاتف صحيح" else "Veuillez renseigner un numéro de téléphone valide."
                        } else if (!consentGiven) {
                            errorMessage = if (lang == AppLanguage.AR) "يرجى الموافقة على شروط المعالجة" else "Veuillez cocher le consentement obligatoire."
                        } else {
                            errorMessage = null
                            onSubmitLead(fullName, phone, email, governorate, notes, consentGiven)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MizenGold),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_lead_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (lang == AppLanguage.AR) "إرسال طلب المرافقة" else "Transmettre ma demande",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun LeadSuccessScreen(
    lead: LeadEntity,
    onBackToHome: () -> Unit,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MizenSuccess.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MizenSuccess,
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = if (lang == AppLanguage.AR) "تم تسجيل طلبك بنجاح" else "Demande enregistrée avec succès",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = "Réf : ${lead.referenceCode}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MizenPrimary
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = if (lang == AppLanguage.AR) "تم حفظ ملفك المبدئي لتمويل ${lead.selectedProductName} لدى ${lead.selectedProviderName}."
            else "Votre profil a été enregistré pour l'offre ${lead.selectedProductName} auprès de ${lead.selectedProviderName}.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Legal reminder
        MizenEligibilityDisclaimer(lang = lang)

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onBackToHome,
            colors = ButtonDefaults.buttonColors(containerColor = MizenPrimary),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("lead_success_home_btn")
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (lang == AppLanguage.AR) "العودة للرئيسية" else "Retour à l'accueil")
        }
    }
}
