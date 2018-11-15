package edu.illinois.financemanager.picture;

import java.io.File;

abstract public class AlbumStorageDirFactory {
    public abstract File getAlbumStorageDir(String albumName);
}
