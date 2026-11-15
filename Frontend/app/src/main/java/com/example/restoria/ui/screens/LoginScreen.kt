package com.example.restoria.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.restoria.R
import com.example.restoria.ui.components.AuthBottomLink
import com.example.restoria.ui.components.AuthTextField
import com.example.restoria.ui.components.GradientButton
import com.example.restoria.ui.components.authBackground
import com.example.restoria.ui.theme.RestoriaTheme
import com.example.restoria.ui.theme.Turquoise

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .authBackground()
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(96.dp))

        Image(
            painter = painterResource(R.drawable.ic_logo),
            contentDescription = "Logo Restoria",
            modifier = Modifier.size(110.dp)
        )

        Text(
            text = "Witaj ponownie",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp, bottom = 36.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
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
        }

        Text(
            text = "Nie pamiętasz hasła?",
            style = MaterialTheme.typography.bodySmall,
            color = Turquoise,
            modifier = Modifier
                .align(Alignment.End)
                .clickable(onClick = onForgotPasswordClick)
                .padding(vertical = 12.dp)
        )

        Spacer(Modifier.height(12.dp))
        GradientButton(text = "Zaloguj się", onClick = onLoginClick)

        Spacer(Modifier.height(48.dp))

        AuthBottomLink(
            question = "Nie masz konta?",
            action = "Zarejestruj się",
            onClick = onRegisterClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun LoginScreenPreview() {
    RestoriaTheme { LoginScreen() }
}