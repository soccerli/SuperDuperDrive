package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.Credentials;
import com.udacity.jwdnd.course1.cloudstorage.model.FileModel;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Iterator;
import java.util.List;

@Controller
@RequestMapping("/home")
public class HomeController {

    private CredentialService credentialService;
    private UserService userService;
    private NoteService noteService;
    private FileModelService fileModelService;
    private EncryptionService encryptionService;

    private Logger logger = LoggerFactory.getLogger(HomeController.class);

    public HomeController(CredentialService credentialService, UserService userService,
                          NoteService noteService,FileModelService fileModelService, EncryptionService encryptionService) {
        this.credentialService = credentialService;
        this.userService=userService;
        this.noteService=noteService;
        this.fileModelService=fileModelService;
        this.encryptionService=encryptionService;
    }

    @GetMapping
    public String getHome(Model model, Authentication authentication){
        Integer currentUserId = userService.getUser(authentication.getName()).getUserId();

        logger.error("at HOME");
        //Credentials
        List<Credentials> credentialsList=credentialService.getCredentialsForUser(currentUserId);

        Iterator<Credentials> iterator=credentialsList.iterator();
        while(iterator.hasNext()){
            Credentials cren=iterator.next();
            logger.error("username="+cren.getUrlUserName());
            logger.error("cryptedpassword="+cren.getUrlPassWord());
            logger.error("url="+cren.getUrl());
            logger.error("crenid="+cren.getCredentialId().toString());
            logger.error("userid="+cren.getUserId().toString());
            //credentialService.decryptPassword(cren);
            //logger.error("decrypted password="+cren.getUrlPassWord());
        }

        model.addAttribute("encryptionService",encryptionService);
        model.addAttribute("credentials",credentialsList);

        //Notes
        logger.error("Home:ready to add note attributes");
        List<Note> notesList= noteService.getNotesForUser(currentUserId);
        Iterator<Note> iterator1=notesList.iterator();
        while(iterator1.hasNext()){
            Note n1= iterator1.next();
            logger.error("at Home, from DB query noteid="+n1.getNoteid().toString());
        }

        model.addAttribute("noteslist",notesList);

        //Files
        List<FileModel> fileModelList = fileModelService.getFilesForUser(currentUserId);

        model.addAttribute("fileList",fileModelList);

        return "home";
    }
}
