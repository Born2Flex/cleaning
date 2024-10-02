package ua.edu.ukma.cleaning.storage;

import java.util.List;

public interface Storageable {
    String getDir();
    Long getId();
    List<String> getAllowedFileTypes();
}
