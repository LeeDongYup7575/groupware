package com.example.projectdemo.domain.mail.mapper;

import com.example.projectdemo.domain.mail.dto.MailAccountDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MailMapper {

    void insertMailAccount(
            @Param("mailAccount") MailAccountDTO mailAccount
    );

    int insertImapFolders(@Param("accountid") Integer accountid);

    int updateMailPassword(
            @Param("internalEmail") String internalEmail,
            @Param("encodedPassword") String encodedPassword);

    int existsByEmail(@Param("accountaddress") String accountaddress);

}
