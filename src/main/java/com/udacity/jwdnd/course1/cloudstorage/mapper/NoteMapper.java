package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.model.Credentials;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NoteMapper {
    @Insert("INSERT INTO NOTES(notetitle,notedescription,userid)  VALUES(#{noteTitle},#{noteDescription},#{userid})")
    @Options(useGeneratedKeys = true, keyProperty = "noteid")
    Integer insertNote(Note note);

    @Select("Select * from NOTES WHERE userid = #{userid}")
    List<Note> getNotesForUser(Integer userid);

    //looks like mybatis is updating all columns, even some coloumn is not given in SET
    @Update("UPDATE NOTES SET notetitle=#{noteTitle},notedescription=#{noteDescription},userid=#{userid} WHERE noteid=#{noteid}")
    Integer updateNote(Note note);

    @Delete("DELETE FROM NOTES WHERE noteid=#{noteid}")
    Integer deleteNote(Integer noteid);
}
