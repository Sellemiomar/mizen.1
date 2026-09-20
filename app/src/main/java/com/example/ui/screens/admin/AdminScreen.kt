package com.example.ui.screens.admin

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.FinancingProductEntity
import com.example.data.local.LeadEntity
import com.example.data.local.ProviderEntity
import com.example.data.local.SearchHistoryEntity
import com.example.data.model.AppLanguage
import com.example.data.model.LeadStatus
import com.example.data.model.VerificationStatus
import com.example.ui.components.VerificationBadge
import com.example.ui.theme.MizenError
import com.example.ui.theme.MizenGold
import com.example.ui.theme.MizenPrimary
import com.example.ui.theme.MizenSuccess
import com.example.ui.theme.MizenWarning
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminScreen(
    products: List<FinancingProductEntity>,
    providers: List<ProviderEntity>,
    leads: List<LeadEntity>,
    recentSearches: List<SearchHistoryEntity>,
    onSaveProduct: (FinancingProductEntity) -> Unit,
    onArchiveProduct: (String) -> Unit,
    onUpdateLeadStatus: (LeadEntity, String, String) -> Unit,
    onSaveProvider: (ProviderEntity) -> Unit,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Overview, 1: Products, 2: Leads, 3: Providers

    Column(modifier = modifier.fillMaxSize()) {
        // Tab Bar
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MizenPrimary
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Vue d'ensemble", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Produits (${products.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Demandes (${leads.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = { Text("Bailleurs (${providers.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
        }

        when (selectedTab) {
            0 -> AdminOverviewTab(products, providers, leads, recentSearches)
            1 -> AdminProductsTab(products, onSaveProduct, onArchiveProduct)
            2 -> AdminLeadsTab(leads, onUpdateLeadStatus)
            3 -> AdminProvidersTab(providers, onSaveProvider)
        }
    }
}

// ---------------- TAB 0: OVERVIEW ----------------
@Composable
fun AdminOverviewTab(
    products: List<FinancingProductEntity>,
    providers: List<ProviderEntity>,
    leads: List<LeadEntity>,
    searches: List<SearchHistoryEntity>
) {
    val totalViews = products.sumOf { it.viewsCount }
    val verifiedCount = products.count { it.verificationStatus == VerificationStatus.VERIFIED.name }
    val verifiedPct = if (products.isNotEmpty()) (verifiedCount * 100) / products.size else 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Tableau de Bord & Métriques Mizen",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Surveillance du catalogue de financements tunisiens et des demandes",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminStatCard(
                    title = "Produits actifs",
                    value = "${products.size}",
                    subtitle = "$verifiedPct% vérifiés officiellement",
                    icon = Icons.Default.Inventory,
                    modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                    title = "Organismes",
                    value = "${providers.size}",
                    subtitle = "Banques & IMF",
                    icon = Icons.Default.AccountBalance,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminStatCard(
                    title = "Dossiers reçus",
                    value = "${leads.size}",
                    subtitle = "${leads.count { it.status == "NEW" }} nouveaux leads",
                    icon = Icons.Default.Group,
                    modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                    title = "Consultations",
                    value = "$totalViews",
                    subtitle = "${searches.size} recherches tracées",
                    icon = Icons.Default.Visibility,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Text(
                text = "Dernières recherches effectuées",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (searches.isEmpty()) {
            item {
                Text(
                    text = "Aucune recherche récente enregistrée.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(searches.take(5)) { search ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${search.purpose} • ${search.requestedAmount.toLong()} DT",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Apport personnel : ${search.selfContribution.toLong()} DT",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = SimpleDateFormat("HH:mm", Locale.FRANCE).format(Date(search.timestamp)),
                            fontSize = 11.sp,
                            color = MizenPrimary
                        )
                    }
                }
            }
        }
    }
}

// ---------------- TAB 1: PRODUCTS MANAGEMENT ----------------
@Composable
fun AdminProductsTab(
    products: List<FinancingProductEntity>,
    onSaveProduct: (FinancingProductEntity) -> Unit,
    onArchiveProduct: (String) -> Unit
) {
    var editingProduct by remember { mutableStateOf<FinancingProductEntity?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products, key = { it.id }) { product ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product.providerName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MizenPrimary
                            )
                            Text(
                                text = product.productName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Row {
                            IconButton(onClick = { editingProduct = product }) {
                                Icon(Icons.Default.Edit, contentDescription = "Éditer", tint = MizenPrimary, modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = { onArchiveProduct(product.id) }) {
                                Icon(Icons.Default.Archive, contentDescription = "Archiver", tint = MizenError, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "${product.amountMin.toLong()} à ${product.amountMax.toLong()} DT • ${product.durationMaxMonths} mois max • ${product.financingStructure}",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Vérifié : ${product.verificationDate} (${product.verificationStatus})",
                        fontSize = 10.5.sp,
                        color = MizenPrimary
                    )
                }
            }
        }
    }

    // Edit Product Dialog
    editingProduct?.let { prod ->
        var editName by remember { mutableStateOf(prod.productName) }
        var editRate by remember { mutableStateOf(prod.rateStructure) }
        var editContribution by remember { mutableStateOf(prod.customerContributionDescription) }

        AlertDialog(
            onDismissRequest = { editingProduct = null },
            title = { Text("Modifier le produit", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Nom du produit") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editRate,
                        onValueChange = { editRate = it },
                        label = { Text("Structure de taux / marge") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editContribution,
                        onValueChange = { editContribution = it },
                        label = { Text("Apport exigé") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSaveProduct(
                            prod.copy(
                                productName = editName,
                                rateStructure = editRate,
                                customerContributionDescription = editContribution,
                                verificationDate = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(Date())
                            )
                        )
                        editingProduct = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MizenPrimary)
                ) {
                    Text("Enregistrer")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { editingProduct = null }) {
                    Text("Annuler")
                }
            }
        )
    }
}

// ---------------- TAB 2: LEADS MANAGEMENT ----------------
@Composable
fun AdminLeadsTab(
    leads: List<LeadEntity>,
    onUpdateLeadStatus: (LeadEntity, String, String) -> Unit
) {
    var selectedLeadForEdit by remember { mutableStateOf<LeadEntity?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (leads.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Aucune demande enregistrée pour le moment.")
                    }
                }
            }
        } else {
            items(leads, key = { it.id }) { lead ->
                val statusColor = when (lead.status) {
                    "NEW" -> MizenGold
                    "QUALIFIED" -> MizenPrimary
                    "APPROVED" -> MizenSuccess
                    "REJECTED" -> MizenError
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = lead.referenceCode,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MizenPrimary
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(statusColor.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = lead.status,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = statusColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "${lead.fullName} • ${lead.phone} • ${lead.governorate}",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Produit : ${lead.selectedProductName} (${lead.selectedProviderName})",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "Besoin : ${lead.requestedAmount.toLong()} DT • Apport : ${lead.selfContribution.toLong()} DT",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (lead.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Note : ${lead.notes}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { selectedLeadForEdit = lead },
                            colors = ButtonDefaults.buttonColors(containerColor = MizenPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Changer le statut / Traiter le dossier", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    selectedLeadForEdit?.let { lead ->
        var currentStatus by remember { mutableStateOf(lead.status) }
        var adminNotes by remember { mutableStateOf(lead.notes) }

        AlertDialog(
            onDismissRequest = { selectedLeadForEdit = null },
            title = { Text("Mettre à jour le dossier", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Statut actuel : $currentStatus", fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("NEW", "QUALIFIED", "APPROVED", "REJECTED").forEach { st ->
                            val isSel = currentStatus == st
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) MizenPrimary else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { currentStatus = st }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = st,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = adminNotes,
                        onValueChange = { adminNotes = it },
                        label = { Text("Notes de suivi interne") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateLeadStatus(lead, currentStatus, adminNotes)
                        selectedLeadForEdit = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MizenPrimary)
                ) {
                    Text("Valider")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { selectedLeadForEdit = null }) {
                    Text("Fermer")
                }
            }
        )
    }
}

// ---------------- TAB 3: PROVIDERS MANAGEMENT ----------------
@Composable
fun AdminProvidersTab(
    providers: List<ProviderEntity>,
    onSaveProvider: (ProviderEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(providers, key = { it.id }) { provider ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = provider.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MizenPrimary.copy(alpha = 0.1f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(provider.providerType, fontSize = 10.sp, color = MizenPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${provider.address} • Tél: ${provider.phone}",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = provider.description,
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun AdminStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = MizenPrimary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = title, fontSize = 11.5.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = subtitle, fontSize = 10.sp, color = MizenPrimary)
        }
    }
}
