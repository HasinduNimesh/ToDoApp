package com.example.moderntodo.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.moderntodo.ui.theme.GradientEnd
import com.example.moderntodo.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsOfServiceScreen(
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(GradientStart, GradientEnd)
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 24.dp)
        ) {
            item {
                // Header
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.15f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Icon(
                            imageVector = Icons.Default.Gavel,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = Color.White
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column {
                            Text(
                                text = "Terms of Service",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Your agreement with us",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            item {
                TermsSection(
                    title = "Acceptance of Terms",
                    icon = Icons.Default.Check,
                    content = """
                        By downloading, installing, or using ModernTodo, you agree to be bound by these Terms of Service. If you do not agree to these terms, please do not use our application.
                        
                        These terms constitute a legal agreement between you and ModernTodo, governing your use of our service.
                    """.trimIndent()
                )
            }

            item {
                TermsSection(
                    title = "Service Description",
                    icon = Icons.Default.Apps,
                    content = """
                        ModernTodo is a task management application that allows you to:
                        
                        • Create, organize, and manage your tasks and todo lists
                        • Set reminders and due dates for your tasks
                        • Sync your data across multiple devices (if enabled)
                        • Customize your experience with themes and settings
                        • Backup and restore your data
                    """.trimIndent()
                )
            }

            item {
                TermsSection(
                    title = "User Responsibilities",
                    icon = Icons.Default.Person,
                    content = """
                        You are responsible for:
                        
                        • Maintaining the confidentiality of your account credentials
                        • All activities that occur under your account
                        • Ensuring your use complies with applicable laws
                        • Providing accurate information when creating your account
                        • Notifying us of any unauthorized use of your account
                    """.trimIndent()
                )
            }

            item {
                TermsSection(
                    title = "Prohibited Uses",
                    icon = Icons.Default.Block,
                    content = """
                        You may not use ModernTodo to:
                        
                        • Violate any applicable laws or regulations
                        • Infringe on intellectual property rights
                        • Transmit harmful, offensive, or inappropriate content
                        • Attempt to gain unauthorized access to our systems
                        • Use the service for commercial purposes without permission
                        • Share your account with others
                    """.trimIndent()
                )
            }

            item {
                TermsSection(
                    title = "Intellectual Property",
                    icon = Icons.Default.Copyright,
                    content = """
                        • ModernTodo and its content are protected by copyright and other intellectual property laws
                        • You retain ownership of your personal data and tasks
                        • We grant you a limited, non-exclusive license to use the app
                        • You may not copy, modify, or distribute our software without permission
                        • All trademarks and logos are the property of their respective owners
                    """.trimIndent()
                )
            }

            item {
                TermsSection(
                    title = "Data and Privacy",
                    icon = Icons.Default.PrivacyTip,
                    content = """
                        • Your data privacy is governed by our Privacy Policy
                        • We implement security measures to protect your information
                        • You can export or delete your data at any time
                        • We may use aggregated, anonymized data to improve our service
                        • Cloud sync is optional and can be disabled in settings
                    """.trimIndent()
                )
            }

            item {
                TermsSection(
                    title = "Service Availability",
                    icon = Icons.Default.Cloud,
                    content = """
                        • We strive to maintain high service availability
                        • Scheduled maintenance may temporarily interrupt service
                        • We are not liable for service interruptions beyond our control
                        • Local features work offline and are not affected by service outages
                        • We reserve the right to modify or discontinue features with notice
                    """.trimIndent()
                )
            }

            item {
                TermsSection(
                    title = "Limitation of Liability",
                    icon = Icons.Default.Info,
                    content = """
                        • ModernTodo is provided "as is" without warranties
                        • We are not liable for any indirect, incidental, or consequential damages
                        • Our total liability is limited to the amount you paid for the service
                        • You use the service at your own risk
                        • We recommend regular backups of your important data
                    """.trimIndent()
                )
            }

            item {
                TermsSection(
                    title = "Changes to Terms",
                    icon = Icons.Default.Update,
                    content = """
                        • We may update these terms from time to time
                        • You will be notified of significant changes
                        • Continued use after changes constitutes acceptance
                        • Previous versions remain available for reference
                        • You may terminate your account if you disagree with changes
                    """.trimIndent()
                )
            }

            item {
                TermsSection(
                    title = "Termination",
                    icon = Icons.Default.ExitToApp,
                    content = """
                        • You may terminate your account at any time
                        • We may suspend or terminate accounts for violations
                        • Upon termination, your data will be deleted according to our retention policy
                        • Some provisions of these terms survive termination
                        • You can export your data before termination
                    """.trimIndent()
                )
            }

            item {
                // Contact Information
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.1f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = Color.White
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = "Contact Us",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "If you have questions about these Terms of Service, please contact us:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = "Email: hasindunimesh89@gmail.com",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                        
                        Text(
                            text = "Last Updated: June 23, 2025",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TermsSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f),
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
            )
        }
    }
}
