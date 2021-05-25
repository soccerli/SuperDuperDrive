package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.FileModel;
import com.udacity.jwdnd.course1.cloudstorage.services.FileModelService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;

//import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
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
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.InputStream;

import static com.udacity.jwdnd.course1.cloudstorage.constant.ConstantMsgs.*;

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
        final String file_ok=FILE_UPLOAD_SUCCESS;
        byte[] fb=null;
        String fileName=null;

        try {
            Integer userId= userService.getUser(authentication.getName()).getUserId();
            fileName=multipartFile.getOriginalFilename();
            String contentType= multipartFile.getContentType();
            Long fileSize=multipartFile.getSize();

            //First test if user really selected a file
            logger.error("fileName="+fileName);
            if(fileName.length()==0) {
                file_err=FILE_NOT_SELECTED_ERR;
                Exception a= new Exception("No file was selected");
                throw(a);
            }

            if((multipartFile.getSize() > 5242880)) {
                throw new MaxUploadSizeExceededException(multipartFile.getSize());
            }

            //Second test if the file (by fileName) is already in DB
            if(fileModelService.isFileDuplicate(userId,fileName)){
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
        }catch (MaxUploadSizeExceededException a){
            logger.error("file size limit exceed expception captured");
            logger.error(a.toString());
            a.printStackTrace();
            file_err=FILE_SIZE_LIMIT_EXCEED;
        }
        catch (Exception a){
            if(file_err==null) file_err=FILE_UNKNOWN_ERR;
            logger.error(a.toString());
            a.printStackTrace();
        }

        //handling msg (success or failure) attributes
        if(file_err==null) {redirectAttributes.addAttribute("opok",true); redirectAttributes.addAttribute("opmsg",file_ok+": "+fileName);}
        else {redirectAttributes.addAttribute("opnotok",true);redirectAttributes.addAttribute("opmsg",file_err+": "+fileName);}

        return ("redirect:/home");
    }

    @GetMapping("/home/file/delete/{filename}")
    public String deleteFile(@PathVariable("filename") String fileName,Authentication authentication,RedirectAttributes redirectAttributes){
        String file_err=null;
        String file_ok=FILE_DELETE_SUCCESS;
        logger.error("File delete controller");
        try{
            int rowDeleted=fileModelService.deleteFile(fileName);
            if(rowDeleted<0) file_err=FILE_DELETE_FAILURE;

        }catch(Exception a){
            if(file_err==null) file_err=FILE_UNKNOWN_ERR;
           logger.error(a.toString());
        }

        //handling msg (success or failure) attributes
        if(file_err==null) {redirectAttributes.addAttribute("opok",true); redirectAttributes.addAttribute("opmsg",file_ok+": "+fileName);}
        else {redirectAttributes.addAttribute("opnotok",true);redirectAttributes.addAttribute("opmsg",file_err+": "+fileName);}

        return ("redirect:/home");
    }

    @GetMapping("home/files/download/{filename}")
    public ResponseEntity downloadFile(@PathVariable("filename") String fileName, Authentication authentication, RedirectAttributes redirectAttributes){

        Integer userId= userService.getUser(authentication.getName()).getUserId();
        FileModel dfile = null;
        try {
              dfile = fileModelService.getFileByNameForUser(userId,fileName);
        }catch (Exception a){
            logger.error(a.toString());
            return ResponseEntity.notFound().build();
        }

        //no re-rendering of our page so can't display the msg
        //redirectAttributes.addAttribute("opok",true); redirectAttributes.addAttribute("opmsg","You tried to download: "+fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(dfile.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+fileName+"\"")
                .body(dfile.getFileData());

    }
}
