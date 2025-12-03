package com.example.auth.presentation.register

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auth.domain.UserDataValidator
import com.example.auth.presentation.R
import com.example.core.presentation.designsystem.CheckIcon
import com.example.core.presentation.designsystem.CrossIcon
import com.example.core.presentation.designsystem.EmailIcon
import com.example.core.presentation.designsystem.Poppins
import com.example.core.presentation.designsystem.RuniqueDarkRed
import com.example.core.presentation.designsystem.RuniqueGray
import com.example.core.presentation.designsystem.RuniqueGreen
import com.example.core.presentation.designsystem.RuniqueTheme
import com.example.core.presentation.designsystem.components.GradientBackground
import com.example.core.presentation.designsystem.components.RuniqueActionButton
import com.example.core.presentation.designsystem.components.RuniquePasswordTextField
import com.example.core.presentation.designsystem.components.RuniqueTextField
import com.example.core.presentation.designsystem.components.util.RuniqueMessageSnackBar
import com.example.core.presentation.ui.ObserveAsEvents
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreenRoot(
    onSignInClick: () -> Unit,
    onSuccessfulRegistration: (SnackbarHostState) -> Unit,
    viewModel: RegisterViewModel = koinViewModel(),
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val context = LocalContext.current
    val snackBarScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    ObserveAsEvents(flow = viewModel.events) { event ->
        when (event) {
            is RegisterEvent.RegistrationSuccess -> {
                keyboardController?.hide()
                onSuccessfulRegistration(snackBarHostState)
            }

            is RegisterEvent.Error -> {
                keyboardController?.hide()
                snackBarScope.launch {
                    snackBarHostState.showSnackbar(
                        message = event.error.asString(context),
                        duration = SnackbarDuration.Long,
                        withDismissAction = true
                    )
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .imePadding()
            .fillMaxSize()
    ) {
        RegisterScreen(
            state = viewModel.state,
            onAction = { action ->
                when (action) {
                    is RegisterAction.OnLoginClick -> onSignInClick()
                    else -> Unit
                }
                viewModel.onAction(action)
            }
        )

        SnackbarHost(
            modifier = Modifier.align(Alignment.BottomCenter),
            hostState = snackBarHostState,
            snackbar = { snackBarData ->
                val shouldShowSnackBar =
                    snackBarData.visuals.message != stringResource(id = R.string.registration_successful)

                if (shouldShowSnackBar) {
                    val isErrorMessage =
                        snackBarData.visuals.message != stringResource(id = R.string.registration_successful)
                                && snackBarData.visuals.message != stringResource(id = R.string.youre_logged_in)

                    RuniqueMessageSnackBar(
                        snackBarData = snackBarData,
                        isErrorMessage = isErrorMessage,
                        modifier = Modifier.padding(bottom = 40.dp)
                    )
                }
            },
        )
    }
}

@Composable
fun RegisterScreen(
    state: RegisterState,
    onAction: (RegisterAction) -> Unit,
    modifier: Modifier = Modifier
) {
    GradientBackground {
        Column(
            modifier = Modifier
                .verticalScroll(state = rememberScrollState())
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(vertical = 32.dp)
                .padding(top = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.register_account),
                style = MaterialTheme.typography.headlineMedium,
            )

            val annotatedString = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontFamily = Poppins,
                        color = RuniqueGray
                    )
                ) {
                    append(stringResource(id = R.string.already_hace_an_account) + " ")
                    pushStringAnnotation(
                        tag = "clickable_text",
                        annotation = stringResource(id = R.string.login)
                    )
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = Poppins
                        )
                    ) {
                        append(stringResource(id = R.string.login))
                    }
                }
            }

            ClickableText(
                text = annotatedString,
                onClick = { offset ->
                    annotatedString.getStringAnnotations(
                        tag = "clickable_text",
                        start = offset,
                        end = offset
                    ).firstOrNull()?.let {
                        onAction(RegisterAction.OnLoginClick)
                    }
                }
            )
            Spacer(modifier = Modifier.height(48.dp))
            RuniqueTextField(
                state = state.email,
                startIcon = EmailIcon,
                endIcon =
                    if (state.email.text.isNotEmpty() && state.isEmailValid) {
                        CheckIcon
                    } else {
                        null
                    },
                hint = stringResource(id = R.string.example_email),
                title = stringResource(id = R.string.email),
                additionalInfo = stringResource(id = R.string.must_be_a_valid_email_address),
                modifier = Modifier.fillMaxWidth(),
                keyboardType = KeyboardType.Email,
            )
            Spacer(modifier = Modifier.height(16.dp))
            RuniquePasswordTextField(
                state = state.password,
                hint = stringResource(R.string.password),
                title = stringResource(R.string.password),
                modifier = Modifier.fillMaxWidth(),
                isPasswordVisible = state.isPasswordVisible,
                onTogglePasswordVisibility = {
                    onAction(RegisterAction.OnTogglePasswordVisibilityClick)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (state.password.text.isNotEmpty()) {
                PasswordRequirement(
                    text = stringResource(
                        id = R.string.at_least_x_characters,
                        UserDataValidator.MIN_PASSWORD_LENGTH
                    ),
                    isValid = state.passwordValidationState.hasMinLength,
                )
                Spacer(modifier = Modifier.height(4.dp))
                PasswordRequirement(
                    text = stringResource(id = R.string.at_least_one_number),
                    isValid = state.passwordValidationState.hasNumber,
                )
                Spacer(modifier = Modifier.height(4.dp))
                PasswordRequirement(
                    text = stringResource(id = R.string.contains_lowercase_character),
                    isValid = state.passwordValidationState.hasLowerCaseCharacter,
                )
                Spacer(modifier = Modifier.height(4.dp))
                PasswordRequirement(
                    text = stringResource(id = R.string.contains_uppercase_character),
                    isValid = state.passwordValidationState.hasUpperCaseCharacter,
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            RuniqueActionButton(
                text = stringResource(id = R.string.register),
                isLoading = state.isRegistering,
                modifier = Modifier.fillMaxWidth(),
                enabled = state.canRegister,
                onClick = { onAction(RegisterAction.OnRegisterClick) }

            )
        }
    }
}

@Composable
fun PasswordRequirement(
    text: String,
    isValid: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isValid) CheckIcon else CrossIcon,
            contentDescription = null,
            tint = if (isValid) RuniqueGreen else RuniqueDarkRed
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
    }
}

@Preview
@Composable
private fun RegisterScreenPreview() {
    RuniqueTheme {
        RegisterScreen(
            state = RegisterState(
                /*passwordValidationState = PasswordValidationState(
                    hasMinLength = true,
                    hasNumber = true,
                    hasLowerCaseCharacter = true,
                    hasUpperCaseCharacter = true
                ),*/
            ),
            onAction = {}
        )
    }
}