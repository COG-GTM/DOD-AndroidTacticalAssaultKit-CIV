
package com.atakmap.android.video.manager;

import com.atakmap.coremap.filesystem.FileSystemUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.os.Environment;

import java.io.File;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Tests that connection entry UIDs cannot be used to read or write files
 * outside of the entries directory (CWE-22 path traversal)
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({
        Environment.class, android.util.Log.class, FileSystemUtils.class
})
public class VideoManagerTest {

    private File entriesDir;

    @Before
    public void setup() throws Exception {
        PowerMockito.mockStatic(android.util.Log.class);
        PowerMockito.mockStatic(Environment.class);
        File root = Files.createTempDirectory("atak").toFile();
        PowerMockito.when(Environment.getExternalStorageDirectory())
                .thenReturn(root);
        entriesDir = Files.createTempDirectory("entries").toFile();
    }

    @Test
    public void getEntryFile_validUID() throws Exception {
        String uid = "3c8f2d90-1a5b-4c6e-9d7f-0e1a2b3c4d5e";
        File file = VideoManager.getEntryFile(entriesDir, uid);
        assertNotNull(file);
        assertEquals(uid + ".xml", file.getName());
        assertEquals(entriesDir.getCanonicalFile(),
                file.getCanonicalFile().getParentFile());
    }

    @Test
    public void getEntryFile_emptyUID() {
        assertNull(VideoManager.getEntryFile(entriesDir, null));
        assertNull(VideoManager.getEntryFile(entriesDir, ""));
    }

    @Test
    public void getEntryFile_pathTraversalUID() {
        assertNull(VideoManager.getEntryFile(entriesDir,
                "../../../../sdcard/atak/x"));
        assertNull(VideoManager.getEntryFile(entriesDir, "../sibling"));
        assertNull(VideoManager.getEntryFile(entriesDir, "a/../../b"));
    }

    @Test
    public void getEntryFile_absolutePathUID() {
        assertNull(VideoManager.getEntryFile(entriesDir, "/sdcard/atak/x"));
    }

    @Test
    public void getEntryFile_subdirectoryUID() {
        assertNull(VideoManager.getEntryFile(entriesDir, "subdir/entry"));
    }
}
