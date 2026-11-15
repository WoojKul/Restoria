package com.example.restoria.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.restoria.ui.components.AuthBottomLink
import com.example.restoria.ui.components.AuthTextField
import com.example.restoria.ui.components.GradientButton
import com.example.restoria.ui.components.authBackground
import com.example.restoria.ui.theme.BackgroundBottom
import com.example.restoria.ui.theme.OutlineColor
import com.example.restoria.ui.theme.RestoriaTheme
import com.example.restoria.ui.theme.SurfaceColor
import com.example.restoria.ui.theme.TextMuted
import com.example.restoria.ui.theme.TextPrimary
import com.example.restoria.ui.theme.Turquoise

@Composable
fun RegisterScreen(
    onBackClick: () -> Unit = {},
    onCreateAccountClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var accepted by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .authBackground()
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SurfaceColor)
                    .clickable(onClick = onBackClick)
                    .align(Alignment.CenterStart),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Wstecz",
                    tint = TextPrimary
                )
            }
            Text(
                text = "Rejestracja",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Text(
            text = "Utwórz konto",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 28.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            AuthTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = "Imię",
                leadingIcon = Icons.Filled.Person
            )
            AuthTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = "example@example.com",
                leadingIcon = Icons.Filled.Email,
                keyboardType = KeyboardType.Email
            )
            AuthTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Hasło",
                leadingIcon = Icons.Filled.Lock,
                isPassword = true
            )
            PasswordStrength(password)
            AuthTextField(
                value = repeatPassword,
                onValueChange = { repeatPassword = it },
                placeholder = "Powtórz hasło",
                leadingIcon = Icons.Filled.Lock,
                isPassword = true
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = accepted,
                onCheckedChange = { accepted = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = Turquoise,
                    uncheckedColor = OutlineColor,
                    checkmarkColor = BackgroundBottom
                )
            )
            Text(
                text = "Akceptuję regulamin i politykę prywatności",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }

        Spacer(Modifier.height(16.dp))
        GradientButton(text = "Utwórz konto", onClick = onCreateAccountClick)

        Spacer(Modifier.height(32.dp))

        AuthBottomLink(
            question = "Masz już konto?",
            action = "Zaloguj się",
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun PasswordStrength(password: String) {
    var score = 0
    if (password.length >= 8) score++
    if (password.any { it.isDigit() }) score++
    if (password.any { it.isUpperCase() }) score++
    if (password.any { !it.isLetterOrDigit() }) score++

    val label = when {
        password.isEmpty() -> "Użyj min. 8 znaków, cyfry i wielkiej litery"
        score <= 1 -> "Hasło jest słabe"
        score == 2 -> "Hasło jest średnie"
        score == 3 -> "Hasło jest dość mocne"
        else -> "Hasło jest mocne"
    }

    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(4) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (index < score) Turquoise else OutlineColor)
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun RegisterScreenPreview() {
    RestoriaTheme { RegisterScreen() }
}