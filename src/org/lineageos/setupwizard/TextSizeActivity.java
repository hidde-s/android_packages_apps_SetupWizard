/*
 * SPDX-FileCopyrightText: 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.setupwizard;

import static org.lineageos.setupwizard.SetupWizardApp.TEXT_SIZE_OPTION_KEY;

import android.app.Activity;
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
    private static final int TEXT_SIZE_NORMAL = 100;  // fontScale 0.75
    private static final int TEXT_SIZE_BIGGER = 125; // fontScale 1.0
    private static final int TEXT_SIZE_BIGGEST = 150; // fontScale 1.25

    private int mCurrentSelection = TEXT_SIZE_NORMAL;
    private Button mNormalBtn;
    private Button mBiggerBtn;
    private Button mBiggestBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSetupWizardApp = (SetupWizardApp) getApplication();
        
        getGlifLayout().setDescriptionText(getString(R.string.setup_text_size_summary));
        setNextText(R.string.next);

        // Get existing selection from settings bundle if available
        if (mSetupWizardApp.getSettingsBundle().containsKey(TEXT_SIZE_OPTION_KEY)) {
            mCurrentSelection = mSetupWizardApp.getSettingsBundle()
                    .getInt(TEXT_SIZE_OPTION_KEY, TEXT_SIZE_NORMAL);
        } else {
            // Default to normal
            mCurrentSelection = TEXT_SIZE_NORMAL;
        }

        // Initialize UI elements
        mNormalBtn = findViewById(R.id.text_size_normal_btn);
        mBiggerBtn = findViewById(R.id.text_size_bigger_btn);
        mBiggestBtn = findViewById(R.id.text_size_biggest_btn);

        // Verify buttons were found
        if (mNormalBtn == null || mBiggerBtn == null || mBiggestBtn == null) {
            throw new RuntimeException("Failed to initialize text size buttons - one or more button IDs not found in layout");
        }

        // Set up click listeners
        mNormalBtn.setOnClickListener(v -> applyTextSize(TEXT_SIZE_NORMAL));
        mBiggerBtn.setOnClickListener(v -> applyTextSize(TEXT_SIZE_BIGGER));
        mBiggestBtn.setOnClickListener(v -> applyTextSize(TEXT_SIZE_BIGGEST));

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
        if (mNormalBtn != null) {
            mNormalBtn.setSelected(mCurrentSelection == TEXT_SIZE_NORMAL);
        }
        if (mBiggerBtn != null) {
            mBiggerBtn.setSelected(mCurrentSelection == TEXT_SIZE_BIGGER);
        }
        if (mBiggestBtn != null) {
            mBiggestBtn.setSelected(mCurrentSelection == TEXT_SIZE_BIGGEST);
        }
    }

    private void applyTextSizeToActivity(float fontScale) {
        // Create a new configuration with the desired font scale
        Configuration config = new Configuration(getResources().getConfiguration());
        config.fontScale = fontScale;
        
        // Get a context with the new configuration
        android.content.Context scaledContext = createConfigurationContext(config);
    }

    @Override
    protected void onNextPressed() {
        // Store the selected text size in the bundle and system settings
        float fontScale = mCurrentSelection / 100f;
        mSetupWizardApp.getSettingsBundle().putInt(TEXT_SIZE_OPTION_KEY, mCurrentSelection);
        Settings.System.putFloat(getContentResolver(),
                Settings.System.FONT_SCALE, fontScale);
        
        // Proceed to next action
        nextAction(Activity.RESULT_OK);
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
