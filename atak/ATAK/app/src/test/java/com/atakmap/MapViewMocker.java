package com.atakmap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.atakmap.android.maps.MapItem;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.maps.Marker;
import com.atakmap.comms.NetworkUtils;
import com.atakmap.coremap.filesystem.FileSystemUtils;

import android.os.Environment;

/**
 * Registers static mocks for the classes needed to obtain a mocked
 * {@link MapView}. The static mocks stay active until {@link #close()} is
 * called, which must happen on the same thread that called
 * {@link #getMapView()}.
 */
public class MapViewMocker implements AutoCloseable {

    private final List<MockedStatic<?>> staticMocks = new ArrayList<>();

    private <T> MockedStatic<T> mockStatic(Class<T> clazz) {
        MockedStatic<T> mocked = Mockito.mockStatic(clazz);
        staticMocks.add(mocked);
        return mocked;
    }

    public MapView getMapView() {
        mockStatic(android.util.Log.class);
        mockStatic(Environment.class);
        mockStatic(MapItem.class);
        mockStatic(Marker.class);

        MockedStatic<NetworkUtils> networkUtils = mockStatic(NetworkUtils.class);
        networkUtils.when(NetworkUtils::getIP).thenReturn("127.0.0.1");

        MockedStatic<FileSystemUtils> fileSystemUtils = mockStatic(
                FileSystemUtils.class);
        File fileMock = Mockito.mock(File.class);
        Mockito.when(fileMock.getAbsolutePath()).thenReturn("filePath");
        fileSystemUtils
                .when(() -> FileSystemUtils.getItem(ArgumentMatchers.anyString()))
                .thenReturn(fileMock);

        MockedStatic<MapView> mapView = mockStatic(MapView.class);
        MapView mapViewMock = Mockito.mock(MapView.class);
        mapView.when(MapView::getMapView).thenReturn(mapViewMock);
        return mapViewMock;
    }

    @Override
    public void close() {
        for (int i = staticMocks.size() - 1; i >= 0; i--)
            staticMocks.get(i).close();
        staticMocks.clear();
    }

    @Test
    public void testMapView() {
        try (MapViewMocker mocker = new MapViewMocker()) {
            MapView mapView = mocker.getMapView();
            Assert.assertNotNull(mapView);
        }
    }
}
