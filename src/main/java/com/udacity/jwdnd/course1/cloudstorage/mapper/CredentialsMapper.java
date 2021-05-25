package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.model.Credentials;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import org.apache.ibatis.annotations.*;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface CredentialsMapper {
    @Select("SELECT * FROM CREDENTIALS WHERE userid=#{userId} and url=#{url}")
    User getCredential(Integer userId,String url);

/*
    @Insert("INSERT INTO CREDENTIALS(url, username,key,password,userid) VALUES(#{url},#{urlUserName},#{key},#{urlPassWord},#{userId})")
    @Options(useGeneratedKeys= true, keyProperty="credentialId")
    int createCredential(Credentials credential);
*/
    @Insert("INSERT INTO CREDENTIALS (url, urlusername, key, urlpassword, userid) " +
            "VALUES(#{url}, #{urlUserName}, #{key}, #{urlPassWord}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "credentialId")
    Integer createCredential(Credentials credential);

    @Select("SELECT * FROM CREDENTIALS")
    List<Credentials> getAllCredentials();

    @Select(("SELECT * FROM CREDENTIALS WHERE userid=#{userId}"))
    List<Credentials> getCredentialsForUser(Integer userId);

    //looks like mybatis is updating all columns, even some coloumn is not given in SET, e.g, key
    @Update("UPDATE CREDENTIALS SET url=#{url},urlusername=#{urlUserName},key=#{key},urlpassword=#{urlPassWord} WHERE credentialId=#{credentialId}")
    Integer updateCredential(Credentials credential);

    @Delete("DELETE FROM CREDENTIALS WHERE credentialId=#{credentialId}")
    Integer deleteCredential(Integer credentialId);

    @Select("Select key FROM CREDENTIALS WHERE credentialId=#{credentialId}")
    String getKey(Integer credentialId);

    @Select("Select max(credentialId) from CREDENTIALS")
    Integer getLastCredentialId();


}
