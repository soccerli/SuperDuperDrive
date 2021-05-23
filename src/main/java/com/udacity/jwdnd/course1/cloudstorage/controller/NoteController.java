package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.mapper.NoteMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.udacity.jwdnd.course1.cloudstorage.constant.ConstantMsgs.*;

@Controller
@RequestMapping
public class NoteController {
    private NoteService noteService;
    private UserService userService;

    public NoteController(NoteService noteService, UserService userService) {
        this.noteService = noteService;
        this.userService = userService;
    }

    @PostMapping("home/note")
    public String notePostRequest(@ModelAttribute Note note, RedirectAttributes redirectAttributes, Authentication authentication) {
        Logger logger = LoggerFactory.getLogger(NoteMapper.class);
        String note_err = null;
        try {
            Integer userId = userService.getUser(authentication.getName()).getUserId();
            note.setUserid(userId);

            logger.error("noteId="+note.getNoteid());
            if (note.getNoteid() == null) {//new note
                Integer rowAdded = noteService.insertNote(note);
                logger.error("note row added="+rowAdded.toString());
                if (rowAdded < 0) {
                    note_err = NOTE_ERR_CREATION_FAILURE;
                    logger.error("insert note row<0");
                }
            } else {//edit note
                logger.error("updating note");
                Integer rowUpdated = noteService.updateNote(note);
                if(rowUpdated<0){
                    note_err = NOTE_ERR_UPDATE_FAILURE;
                }

            }
        }catch(Exception a){
            logger.error("note exception");
            note_err=NOTE_ERR_INVALIDSESSION;
            logger.error(a.toString());
        }


        return("redirect:/home");
    }

    @GetMapping("/home/note/delete/{noteId}")
    public String deleteNote(@PathVariable("noteId") Integer noteId, RedirectAttributes redirectAttributes){
        String note_err=null;
        int rowDeleted=noteService.deleteNote(noteId);
        if(rowDeleted<0)
            note_err=NOTE_DELETE_ERR;

        return("redirect:/home");
    }

}
