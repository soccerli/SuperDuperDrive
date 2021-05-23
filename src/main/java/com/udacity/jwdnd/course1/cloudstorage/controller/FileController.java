package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.FileModel;
import com.udacity.jwdnd.course1.cloudstorage.services.FileModelService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.InputStream;

import static com.udacity.jwdnd.course1.cloudstorage.constant.ConstantMsgs.FILE_DELETE_FAILURE;
import static com.udacity.jwdnd.course1.cloudstorage.constant.ConstantMsgs.FILE_DUPLICATE_ERR;

@Controller
public class FileController {
    private UserService userService;
    private FileModelService fileModelService;

    public FileController(UserService userService, FileModelService fileModelService) {
        this.userService = userService;
        this.fileModelService = fileModelService;
    }

    private Logger logger = LoggerFactory.getLogger(FileController.class);

    @PostMapping("/home/fileupload")
    public String uploadFile(@RequestParam("fileUpload") MultipartFile multipartFile, RedirectAttributes redirectAttributes,
                             Authentication authentication){

        logger.error("fileuploadController, upload file ");
        String file_err=null;
        byte[] fb=null;

        try {
            Integer userId= userService.getUser(authentication.getName()).getUserId();
            String fileName=multipartFile.getOriginalFilename();
            String contentType= multipartFile.getContentType();
            Long fileSize=multipartFile.getSize();

            //First test if the file (by fileName) is already in DB
            if(fileModelService.isFileDuplicate(fileName)){
                file_err=FILE_DUPLICATE_ERR;
                Exception a= new Exception("Duplicated file name");
                throw(a);
            }

            InputStream fis = multipartFile.getInputStream();
            fb=new byte[fis.available()];
            fis.read(fb);
            fis.close();

            //manually create the fileModel POJO with info from multipart file
            FileModel fileModel=new FileModel(null,fileName,contentType,fileSize.toString(),userId,fb);
            fileModelService.insertFile(fileModel);
        }catch (Exception a){
            logger.error(a.toString());
            a.printStackTrace();
        }
        return ("redirect:/home");
    }

    @GetMapping("/home/file/delete/{filename}")
    public String deleteFile(@PathVariable("filename") String fileName,Authentication authentication,RedirectAttributes redirectAttributes){
        String file_err=null;
        logger.error("File delete controller");
        try{
            int rowDeleted=fileModelService.deleteFile(fileName);
            if(rowDeleted<0) file_err=FILE_DELETE_FAILURE;

        }catch(Exception a){
           logger.error(a.toString());
        }
        return ("redirect:/home");
    }

    @GetMapping("home/files/download/{filename}")
    public ResponseEntity downloadFile(@PathVariable("filename") String fileName, Authentication authentication, RedirectAttributes redirectAttributes){

        FileModel dfile = null;
        try {
              dfile = fileModelService.getFileByName(fileName);
        }catch (Exception a){
            logger.error(a.toString());
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(dfile.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+fileName+"\"")
                .body(dfile.getFileData());

    }
}
