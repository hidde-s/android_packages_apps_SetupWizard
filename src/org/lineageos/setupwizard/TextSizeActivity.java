/*
 * SPDX-FileCopyrightText: 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.setupwizard;

import static org.lineageos.setupwizard.SetupWizardApp.TEXT_SIZE_OPTION_KEY;

import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.os.UserHandle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import lineageos.providers.LineageSettings;

import org.lineageos.setupwizard.util.SetupWizardUtils;

public class TextSizeActivity extends BaseSetupWizardActivity {

    private SetupWizardApp mSetupWizardApp;
    
    // Text size preset values
    private static final int TEXT_SIZE_SMALL = 50;  // fontScale 0.5
    private static final int TEXT_SIZE_MEDIUM = 75; // fontScale 0.75
    private static final int TEXT_SIZE_LARGE = 100; // fontScale 1.0

    private int mCurrentSelection = TEXT_SIZE_MEDIUM;
    private TextView mPreviewText;
    private Button mSmallBtn;
    private Button mMediumBtn;
    private Button mLargeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSetupWizardApp = (SetupWizardApp) getApplication();
        
        getGlifLayout().setDescriptionText(getString(R.string.setup_text_size_summary));
        setNextText(R.string.next);

        // Get existing selection from settings bundle if available
        if (mSetupWizardApp.getSettingsBundle().containsKey(TEXT_SIZE_OPTION_KEY)) {
            mCurrentSelection = mSetupWizardApp.getSettingsBundle()
                    .getInt(TEXT_SIZE_OPTION_KEY, TEXT_SIZE_MEDIUM);
        } else {
            // Default to medium
            mCurrentSelection = TEXT_SIZE_MEDIUM;
        }

        // Initialize UI elements
        mPreviewText = findViewById(R.id.text_size_preview);
        mSmallBtn = findViewById(R.id.text_size_small_btn);
        mMediumBtn = findViewById(R.id.text_size_medium_btn);
        mLargeBtn = findViewById(R.id.text_size_large_btn);

        // Set up click listeners
        mSmallBtn.setOnClickListener(v -> applyTextSize(TEXT_SIZE_SMALL));
        mMediumBtn.setOnClickListener(v -> applyTextSize(TEXT_SIZE_MEDIUM));
        mLargeBtn.setOnClickListener(v -> applyTextSize(TEXT_SIZE_LARGE));

        // Apply current selection
        applyTextSize(mCurrentSelection);
    }

    private void applyTextSize(int textSizePreset) {
        mCurrentSelection = textSizePreset;
        
        // Update button states
        updateButtonStates();
        
        // Calculate font scale from preset
        float fontScale = textSizePreset / 100f;
        
        // Update system setting
        Settings.System.putFloat(getContentResolver(),
                Settings.System.FONT_SCALE, fontScale);
        
        // Update preview text by creating a new configuration context
        applyTextSizeToActivity(fontScale);
        
        // Store selection in bundle for later use
        mSetupWizardApp.getSettingsBundle().putInt(TEXT_SIZE_OPTION_KEY, textSizePreset);
    }

    private void updateButtonStates() {
        // Update button visual states to show which is selected
        mSmallBtn.setSelected(mCurrentSelection == TEXT_SIZE_SMALL);
        mMediumBtn.setSelected(mCurrentSelection == TEXT_SIZE_MEDIUM);
        mLargeBtn.setSelected(mCurrentSelection == TEXT_SIZE_LARGE);
        
        // Optional: Update button styling
        if (mCurrentSelection == TEXT_SIZE_SMALL) {
            mSmallBtn.setTextAppearance(android.R.style.TextAppearance_Large);
        } else if (mCurrentSelection == TEXT_SIZE_MEDIUM) {
            mMediumBtn.setTextAppearance(android.R.style.TextAppearance_Large);
        } else {
            mLargeBtn.setTextAppearance(android.R.style.TextAppearance_Large);
        }
    }

    private void applyTextSizeToActivity(float fontScale) {
        // Create a new configuration with the desired font scale
        Configuration config = new Configuration(getResources().getConfiguration());
        config.fontScale = fontScale;
        
        // Get a context with the new configuration
        android.content.Context scaledContext = createConfigurationContext(config);
        
        // Update preview text appearance
        if (mPreviewText != null) {
            // Update text size in the preview
            float baseSizeInSp = 16f;
            float scaledSize = baseSizeInSp * fontScale;
            mPreviewText.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, scaledSize);
        }
    }

    @Override
    protected void onNextPressed() {
        // Store the selected text size
        float fontScale = mCurrentSelection / 100f;
        
        // Apply to system settings for persistence
        Settings.System.putFloat(getContentResolver(),
                Settings.System.FONT_SCALE, fontScale);
        
        // Also store in LineageSettings if available
        try {
            LineageSettings.System.putFloatForUser(getContentResolver(),
                    LineageSettings.System.FONT_SCALE, fontScale,
                    UserHandle.USER_CURRENT);
        } catch (Exception e) {
            // Fall back to Settings.System if LineageSettings fails
        }
        
        super.onNextPressed();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.setup_text_size;
    }

    @Override
    protected int getTitleResId() {
        return R.string.setup_text_size_title;
    }

    @Override
    protected int getIconResId() {
        return R.drawable.ic_features;
    }
}
