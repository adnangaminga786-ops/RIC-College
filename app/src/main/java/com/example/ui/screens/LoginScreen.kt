package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import com.example.viewmodel.RICViewModel

@Composable
fun LoginScreen(
    viewModel: RICViewModel,
    modifier: Modifier = Modifier
) {
    var emailOrId by remember { mutableStateOf("RIC-2025-015") }
    var password by remember { mutableStateOf("••••••••") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var isRegisterMode by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(modifier = modifier.fillMaxSize()) {
        // Fullscreen campus backdrop image
        Image(
            painter = painterResource(id = R.drawable.img_campus_night),
            contentDescription = "RIC Campus Night",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient overlay for readability and depth
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            DeepNavyDark.copy(alpha = 0.6f),
                            DeepNavyDark.copy(alpha = 0.85f),
                            DeepNavyDark.copy(alpha = 0.98f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Official RIC Logo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(2.5.dp, Brush.sweepGradient(listOf(NeonBlue, ElectricBlue, GoldenYellow, NeonBlue)), CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_ric_logo),
                    contentDescription = "RIC Logo",
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Riphah International College",
                color = TextWhite,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = GoldenYellow.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldenYellow.copy(alpha = 0.6f)),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    text = "THOKAR CAMPUS",
                    color = GoldenYellow,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
            Text(
                text = "LEARN  •  CONNECT  •  GROW",
                color = TextLightGrey,
                fontSize = 10.sp,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Card surface (frosted glass)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = CardNavy.copy(alpha = 0.95f),
                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isRegisterMode) "Create Account" else "Welcome Back!",
                        color = TextWhite,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isRegisterMode) "Join the RIC Thokar Campus community" else "Sign in to your RIC account to continue your journey",
                        color = TextMuted,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Email or Student ID
                    OutlinedTextField(
                        value = emailOrId,
                        onValueChange = { emailOrId = it },
                        label = { Text("Email or Student ID", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.MailOutline, contentDescription = null, tint = NeonBlue)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_email_input"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = NeonBlue,
                            unfocusedBorderColor = GlassBorder,
                            focusedLabelColor = NeonBlue,
                            unfocusedLabelColor = TextMuted
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = NeonBlue)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password visibility",
                                    tint = TextMuted
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_password_input"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = NeonBlue,
                            unfocusedBorderColor = GlassBorder,
                            focusedLabelColor = NeonBlue,
                            unfocusedLabelColor = TextMuted
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Remember Me & Forgot Password
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { rememberMe = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = ElectricBlue,
                                    uncheckedColor = TextMuted
                                )
                            )
                            Text(
                                text = "Remember me",
                                color = TextLightGrey,
                                fontSize = 12.sp
                            )
                        }

                        Text(
                            text = "Forgot Password?",
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { showForgotPasswordDialog = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Login Button
                    Button(
                        onClick = {
                            val asAdmin = emailOrId.contains("admin", ignoreCase = true)
                            viewModel.login(asAdmin = asAdmin)
                            Toast.makeText(context, if (asAdmin) "Welcome Admin!" else "Welcome back, Student!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("login_button"),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricBlue,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = if (isRegisterMode) "Register" else "Login",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Or continue with",
                        color = TextMuted,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Google Login & College Login
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.login(asAdmin = false)
                                Toast.makeText(context, "Google Sign-In successful!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CardNavyVariant),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                        ) {
                            Text(text = "Google", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = {
                                viewModel.login(asAdmin = false)
                                Toast.makeText(context, "College Portal Sign-In successful!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkBlueAccent),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonBlue.copy(alpha = 0.4f))
                        ) {
                            Text(text = "College Login", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Toggle Register / Login
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isRegisterMode) "Already have an account? " else "Don't have an account? ",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                        Text(
                            text = if (isRegisterMode) "Login" else "Register",
                            color = NeonBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { isRegisterMode = !isRegisterMode }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Demo Role Selectors (for easy testing as Student or Admin)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Demo quick access: ", color = TextMuted, fontSize = 11.sp)
                Text(
                    text = "Student",
                    color = NeonBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { viewModel.login(asAdmin = false) }
                        .padding(horizontal = 4.dp)
                )
                Text(text = " | ", color = TextMuted, fontSize = 12.sp)
                Text(
                    text = "Admin Panel",
                    color = GoldenYellow,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { viewModel.login(asAdmin = true) }
                        .padding(horizontal = 4.dp)
                )
            }
        }
    }

    if (showForgotPasswordDialog) {
        var resetEmail by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = { Text("Reset Your Password", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Enter your registered RIC email address or Student ID to receive reset instructions.", color = TextMuted, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it },
                        label = { Text("Registered Email / ID") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showForgotPasswordDialog = false
                        Toast.makeText(context, "Password reset instructions sent securely!", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                ) {
                    Text("Send Reset Link")
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPasswordDialog = false }) {
                    Text("Back", color = TextMuted)
                }
            },
            containerColor = CardNavy
        )
    }
}
