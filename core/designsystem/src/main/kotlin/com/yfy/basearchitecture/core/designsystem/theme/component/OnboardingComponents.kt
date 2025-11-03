package com.yfy.basearchitecture.core.designsystem.theme.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yfy.basearchitecture.core.designsystem.R

/**
 * Page indicator component for onboarding screens
 */
@Composable
fun PageIndicator(
    currentPage: Int,
    totalPages: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(totalPages) { index ->
            val isSelected = index == currentPage
            Card(
                modifier = Modifier.size(
                    width = if (isSelected) 24.dp else 8.dp,
                    height = 8.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    }
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {}
        }
    }
}

/**
 * Skip button component for onboarding screens
 */
@Composable
fun OnboardingSkipButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = stringResource(R.string.nav_skip),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Text(stringResource(R.string.nav_skip))
    }
}

/**
 * Navigation button component for onboarding screens
 */
@Composable
fun OnboardingNavigationButton(
    text: String,
    onClick: () -> Unit,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(text = text)
        if (icon != null) {
            Spacer(modifier = Modifier.padding(4.dp))
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

/**
 * Back button component for onboarding screens
 */
@Composable
fun OnboardingBackButton(
    onClick: () -> Unit,
    text: String = stringResource(R.string.nav_back_button),
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = stringResource(R.string.nav_back_button),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Text(text)
    }
}

/**
 * Onboarding page content component
 */
@Composable
fun OnboardingPageContent(
    title: String,
    description: String,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
        
        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Description
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }
}

/**
 * Onboarding navigation section component
 */
@Composable
fun OnboardingNavigationSection(
    isLastPage: Boolean,
    currentPageIndex: Int,
    totalPages: Int,
    showBackButton: Boolean = true,
    showIndicator: Boolean = true,
    nextButtonText: String = stringResource(R.string.nav_next),
    getStartedButtonText: String = stringResource(R.string.nav_get_started),
    backButtonText: String = stringResource(R.string.nav_back_button),
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onGetStarted: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Page indicator
        if (showIndicator) {
            PageIndicator(
                currentPage = currentPageIndex,
                totalPages = totalPages
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
        
        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            if (showBackButton && currentPageIndex > 0) {
                OnboardingBackButton(
                    onClick = onPrevious,
                    text = backButtonText
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
            
            // Next/Get Started button
            OnboardingNavigationButton(
                text = if (isLastPage) getStartedButtonText else nextButtonText,
                onClick = if (isLastPage) onGetStarted else onNext,
                icon = Icons.Default.ArrowForward,
                modifier = Modifier.weight(1f)
            )
        }
    }
} 