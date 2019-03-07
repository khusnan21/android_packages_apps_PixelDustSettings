/*
 * Copyright (C) 2018 The Pixel Dust Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pixeldust.settings.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.SettingsPreferenceFragment;

import com.pixeldust.settings.preferences.SystemSettingSeekBarPreference;

import java.util.ArrayList;
import java.util.List;

public class MiscSettings extends SettingsPreferenceFragment implements Indexable, Preference.OnPreferenceChangeListener {

    public static final String TAG = "Misc";
    
    private static final String BURN_INTERVAL_KEY = "burn_in_protection_interval";
    private static final String KEY_DEVICE_PART = "advanced_controls";
    private static final String KEY_DEVICE_PART_PACKAGE_NAME = "com.thht.settings.device";

    private SystemSettingSeekBarPreference mBurnInterval;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.pixeldust_settings_misc);
        
        // Advanced Controls
        if (!com.havoc.settings.preferences.Utils.isPackageInstalled(getActivity(), KEY_DEVICE_PART_PACKAGE_NAME)) {
            getPreferenceScreen().removePreference(findPreference(KEY_DEVICE_PART));
        }
        
        ContentResolver resolver = getActivity().getContentResolver();

        mBurnInterval = (SystemSettingSeekBarPreference) findPreference(BURN_INTERVAL_KEY);
        int burninterval = Settings.System.getInt(resolver,
                Settings.System.BURN_IN_PROTECTION_INTERVAL, 60);
        mBurnInterval.setValue(burninterval);
        mBurnInterval.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mBurnInterval) {
            int interval = (Integer) newValue;
            Settings.System.putIntForUser(resolver,
                    Settings.System.BURN_IN_PROTECTION_INTERVAL, interval, UserHandle.USER_CURRENT);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.PIXELDUST;
    }

    public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                        boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();
                     SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.pixeldust_settings_misc;
                    result.add(sir);
                    return result;
                }
                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    ArrayList<String> result = new ArrayList<String>();
                    return result;
                }
    };
}
