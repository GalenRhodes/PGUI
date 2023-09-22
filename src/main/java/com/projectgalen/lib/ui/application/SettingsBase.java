package com.projectgalen.lib.ui.application;

// ===========================================================================
//     PROJECT: PGUI
//    FILENAME: SettingsBase.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: September 22, 2023
//
// Copyright © 2023 Project Galen. All rights reserved.
//
// Permission to use, copy, modify, and distribute this software for any
// purpose with or without fee is hereby granted, provided that the above
// copyright notice and this permission notice appear in all copies.
//
// THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
// WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
// SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
// WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
// ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
// IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
// ===========================================================================

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.projectgalen.lib.utils.IO;
import com.projectgalen.lib.utils.concurrency.Trigger;
import com.projectgalen.lib.utils.json.JsonTools;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.concurrent.TimeUnit;

import static com.projectgalen.lib.ui.M.msgs;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public abstract class SettingsBase {

    protected @JsonIgnore       File    settingsFile;
    protected @JsonIgnore final Trigger trigger = new Trigger(1, TimeUnit.SECONDS, this::autoSaveSettings);

    public SettingsBase()              { }

    public SettingsBase(boolean dummy) { }

    public void trigger() {
        trigger.trigger();
    }

    protected void autoSaveSettings() {
        try { saveSettings(); } catch(IOException e) { e.printStackTrace(System.err); }
    }

    protected void saveSettings() throws IOException {
        //noinspection ResultOfMethodCallIgnored
        settingsFile.getParentFile().mkdirs();
        JsonTools.getObjectMapper().writeValue(settingsFile, this);
    }

    public static @NotNull <T extends SettingsBase> T load(@NotNull Class<T> cls, @NotNull String filename) {
        File settingsFile = IO.getCanonicalFileQuietly(new File(filename));

        if(settingsFile.exists() && settingsFile.isFile()) {
            try {
                T settings = JsonTools.readJsonFile(settingsFile, cls);
                settings.settingsFile = settingsFile;
                return settings;
            }
            catch(IOException e) {
                JOptionPane.showMessageDialog(null, msgs.getString("dlg.msg.unable_to_load_settings"), msgs.getString("dlg.title.error_loading_settings"), JOptionPane.WARNING_MESSAGE);
                e.printStackTrace(System.err);
            }
        }
        else if(settingsFile.exists()) {
            JOptionPane.showMessageDialog(null, msgs.getString("dlg.msg.dir_as_settings_file"), msgs.getString("dlg.title.error_loading_settings"), JOptionPane.WARNING_MESSAGE);
            System.exit(1);
        }

        try {
            Constructor<T> c = cls.getDeclaredConstructor(boolean.class);
            c.setAccessible(true);
            T settings = c.newInstance(true);
            settings.settingsFile = settingsFile;
            settings.saveSettings();
            return settings;
        }
        catch(Exception e) {
            e.printStackTrace(System.err);
            JOptionPane.showMessageDialog(null, msgs.getString("dlg.msg.cannot_create_settings_file"), msgs.getString("dlg.title.error_creating_settings"), JOptionPane.WARNING_MESSAGE);
            System.exit(1);
            throw new RuntimeException();
        }
    }
}
