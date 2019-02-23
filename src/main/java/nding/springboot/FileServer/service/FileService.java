package nding.springboot.FileServer.service;

import nding.springboot.FileServer.domain.File;

import java.util.List;
import java.util.Optional;

public interface FileService {
    /**
     * Save the file
     * @param file
     * @return
     */
    File saveFile(File file);


    /**
     * Remove the file
     * @param id
     */
    void removeFile(String id);

    /**
     * Get a file object through the id
     * @param id
     * @return
     */
    File getFileById(String id);

    /**
     * Retrieve all the files by page
     * @param pageIndex
     * @param pageSize
     * @return
     */
    List<File> listFilesByPage(int pageIndex, int pageSize);
}
