package de.fernunihagen.techsearch.restservice.dtos;

import java.util.List;

public class SettingsResponseDto  {

    private List<SettingDto> settings;

    public List<SettingDto> getSettings() {
        return settings;
    }

    public void setSettings(List<SettingDto> settings) {
        this.settings = settings;
    }
    
}
