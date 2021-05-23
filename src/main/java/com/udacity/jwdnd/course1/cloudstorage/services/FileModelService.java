package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.FileMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.FileModel;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class FileModelService {

    private FileMapper fileMapper;

    public FileModelService(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    public Integer insertFile(FileModel fileModel){
        return fileMapper.insertFile(fileModel);
    }

    public List<FileModel> getFilesForUser(Integer userId){
        return fileMapper.selectFilesForUser(userId);
    }




    public Integer deleteFile(String fileName){
        return fileMapper.deleteFile(fileName);
    }



    public boolean isFileDuplicate(String fileName){
        List<String> fileNameList = fileMapper.getAllFileNames();
        return fileNameList.contains(fileName);
    }

    public FileModel getFileByName(String fileName){
        return fileMapper.getFileByName(fileName);
    }
}
