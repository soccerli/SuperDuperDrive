package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.model.FileModel;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FileMapper {

    @Insert("INSERT INTO FILES(filename,contenttype,filesize,userid,filedata) VALUES(#{fileName},#{contentType},#{fileSize},#{userId},#{fileData})")
    @Options(useGeneratedKeys = true,keyProperty = "key")
    Integer insertFile(FileModel fileModel);

    @Select("SELECT * FROM FILES WHERE userid=#{userId}")
    List<FileModel> selectFilesForUser(Integer userId);

    //Make sure only one is selected at most
   // @Select("SELECT key FROM FILES WHERE filename=#{fileName} and ROWNUM<2")
    //Integer getKeyByFileName(String fileName);

    @Delete("DELETE FROM FILES WHERE filename=#{fileName}")
    Integer deleteFile(String fileName);

    @Select("SELECT filename FROM FILES WHERE userid=#{userId}")
    List<String> getAllFileNamesForUser(Integer userId);

    @Select("SELECT * FROM FILES WHERE filename=#{fileName} and userid=#{userId} ")
    FileModel getFileByNameForUser(Integer userId,String fileName);

}
