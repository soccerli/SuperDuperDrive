package com.udacity.jwdnd.course1.cloudstorage.constant;

public final class ConstantMsgs {
    private ConstantMsgs() { //can't be instantiated
    }

    //signup messages
    public static final String SIGNUP_ERR = "Sign Failed, Please try again later";
    public static final String SIGNUP_USER_EXISTING_ERR ="User name exists, Please try a different user name";
    public static final String SIGNUP_SUCCESS ="Sign up is successful!";

    public static final String CREDENTIAL_INVALIDSESSION_ERR="Invalid session for credential operation";
    public static final String CREDENTIAL_CREATE_ERR="Creating credential failed, please try later";
    public static final String CREDENTIAL_UPDATE_ERR="Updating credential failed, please try later";
    public static final String CREDENTIAL_DELETE_ERR="Deleting credential failed, please try later";

    public static final String NOTE_ERR_INVALIDSESSION="Invalid session for credential";
    public static final String NOTE_ERR_CREATION_FAILURE="Creating Note failed, please try later";
    public static final String NOTE_ERR_UPDATE_FAILURE="Updating Note failed, please try later";
    public static final String NOTE_DELETE_ERR="Deleting Note failed, please try later";

    public static final String FILE_DELETE_FAILURE="Deleting File failed, please try later";
    public static final String FILE_DUPLICATE_ERR="Duplicated file with the same file name";
}
