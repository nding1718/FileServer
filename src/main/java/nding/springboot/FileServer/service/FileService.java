package nding.springboot.FileServer.service;

import nding.springboot.FileServer.domain.File;

import java.util.List;
import java.util.Optional;

public interface FileService {

    File saveFile(File file);


    void removeFile(String id);

    File getFileById(String id);


    List<File> listFilesByPage(int pageIndex, int pageSize);
}

