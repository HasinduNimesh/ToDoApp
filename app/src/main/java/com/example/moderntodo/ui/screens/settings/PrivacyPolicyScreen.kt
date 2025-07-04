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
fun PrivacyPolicyScreen(
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
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = Color.White
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column {
                            Text(
                                text = "Privacy Policy",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Your privacy matters to us",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            item {
                PrivacySection(
                    title = "Information We Collect",
                    icon = Icons.Default.Info,
                    content = """
                        ModernTodo collects only the information necessary to provide you with the best todo management experience:
                        
                        • Account Information: Username, display name, and encrypted password
                        • Todo Data: Your tasks, lists, due dates, and completion status
                        • App Preferences: Theme settings, notification preferences, and other customizations
                        • Device Information: Basic device information for app optimization
                    """.trimIndent()
                )
            }

            item {
                PrivacySection(
                    title = "How We Use Your Information",
                    icon = Icons.Default.Build,
                    content = """
                        Your information is used solely to:
                        
                        • Provide and maintain the ModernTodo service
                        • Sync your data across devices (if cloud sync is enabled)
                        • Send you task reminders and notifications
                        • Improve app performance and user experience
                        • Ensure data security and prevent unauthorized access
                    """.trimIndent()
                )
            }

            item {
                PrivacySection(
                    title = "Data Storage and Security",
                    icon = Icons.Default.Lock,
                    content = """
                        We take your data security seriously:
                        
                        • Local Storage: Data is stored securely on your device using encryption
                        • Cloud Storage: If enabled, data is encrypted before transmission to Firebase
                        • Password Security: Passwords are hashed and never stored in plain text
                        • Access Control: Only you have access to your personal todo data
                        • Regular Updates: We continuously improve our security measures
                    """.trimIndent()
                )
            }

            item {
                PrivacySection(
                    title = "Data Sharing",
                    icon = Icons.Default.Share,
                    content = """
                        We do not sell, trade, or share your personal information with third parties, except:
                        
                        • Service Providers: Trusted partners who help us operate the app (like Firebase for cloud storage)
                        • Legal Requirements: When required by law or to protect our rights
                        • Your Consent: When you explicitly give us permission to share specific information
                        
                        Your todo data remains private and is never used for advertising or marketing purposes.
                    """.trimIndent()
                )
            }

            item {
                PrivacySection(
                    title = "Your Rights and Controls",
                    icon = Icons.Default.AccountCircle,
                    content = """
                        You have full control over your data:
                        
                        • Access: View all data we have about you
                        • Correction: Update or correct your information at any time
                        • Deletion: Delete your account and all associated data
                        • Export: Download your data in a portable format
                        • Notification Control: Manage what notifications you receive
                    """.trimIndent()
                )
            }

            item {
                PrivacySection(
                    title = "Notifications and Communications",
                    icon = Icons.Default.Notifications,
                    content = """
                        ModernTodo may send you:
                        
                        • Task Reminders: Notifications about upcoming or overdue tasks
                        • App Updates: Information about new features and improvements
                        • Important Notices: Critical information about service changes
                        
                        You can control all notification settings in the app's notification preferences.
                    """.trimIndent()
                )
            }

            item {
                PrivacySection(
                    title = "Children's Privacy",
                    icon = Icons.Default.ChildCare,
                    content = """
                        ModernTodo is designed for users of all ages. However:
                        
                        • We do not knowingly collect personal information from children under 13
                        • If you believe a child has provided us with personal information, please contact us
                        • Parents can review, delete, or stop the collection of their child's information
                    """.trimIndent()
                )
            }

            item {
                PrivacySection(
                    title = "Changes to This Policy",
                    icon = Icons.Default.Update,
                    content = """
                        We may update this Privacy Policy from time to time:
                        
                        • You will be notified of any significant changes
                        • The updated policy will be available in the app
                        • Continued use of the app after changes constitutes acceptance
                        • You can always review the current policy in Settings
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
                            text = "If you have any questions about this Privacy Policy or our data practices, please contact us:",
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
fun PrivacySection(
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
