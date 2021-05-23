package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.Credentials;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.AuthenticationService;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.udacity.jwdnd.course1.cloudstorage.constant.ConstantMsgs.*;

@Controller
public class CredentialController {
    private CredentialService credentialService;
    private UserService userService;
    private AuthenticationService authenticationService;

    private Logger logger = LoggerFactory.getLogger(CredentialController.class);

    @Autowired
    public CredentialController(CredentialService credentialService, UserService userService, AuthenticationService authenticationService) {
        this.credentialService = credentialService;
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("home/credentials")
    public String createCredential(@ModelAttribute Credentials credential, RedirectAttributes redirectAttributes){
        logger.error("---Begining Credential Controller----");
        String credential_err=null;
        String userName=authenticationService.getUserName();


        if(userName==null)
            credential_err = CREDENTIAL_INVALIDSESSION_ERR;

        int userId=-1;
        if(credential_err==null) {
            User user = userService.getUser(userName);
            if(user!=null)
               credential.setUserId(user.getUserId());
            else
                credential_err = CREDENTIAL_INVALIDSESSION_ERR;
        }

        if(credential_err==null){//create new credential
            if(credential.getCredentialId()==null) {
                logger.error("Now ready to insert the credential  " + credential.getUrl() + "  " + credential.getUrlUserName() + " " + credential.getUrlPassWord());
                credentialService.encryptPassword(credential);
                int rowAdded = credentialService.createCredential(credential);
                if (rowAdded < 0) {
                    logger.error("CredentialController: insert failed");
                    credential_err = CREDENTIAL_CREATE_ERR;
                }
            }
            else {
                //now the POJO credential is from the form, not from DB, so below line encrptPssword won't work
                // credentialService.encryptPassword(credential);

                credentialService.updateCredentialWithKey(credential);
                credentialService.encryptPassword(credential);

                int rowUpdated = credentialService.updateCredential(credential);
                if(rowUpdated<0)
                    credential_err = CREDENTIAL_UPDATE_ERR;
            }
        }

        //redirectAttributes.addAttribute("credentials", credentialService.getAllCredentials());
        return("redirect:/home");
    }

    //Delete Credential, but have to use GET?
    @GetMapping("/home/credentials/delete/{credentialId}")
    public String deleteCredential(@PathVariable("credentialId") Integer credentialId,
                                   RedirectAttributes redirectAttributes){

        String credential_err=null;
        int rowDeleted=credentialService.deleteCredential(credentialId);
        if(rowDeleted<0)
            credential_err=CREDENTIAL_DELETE_ERR;

        return("redirect:/home");
    }
}
