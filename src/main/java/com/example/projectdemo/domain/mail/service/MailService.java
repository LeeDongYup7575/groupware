package com.example.projectdemo.domain.mail.service;

import com.example.projectdemo.config.HmailserverPasswordEncoder;
import com.example.projectdemo.domain.mail.dto.MailAccountDTO;
import com.example.projectdemo.domain.mail.mapper.MailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    @Autowired
    private MailMapper mailMapper;

    /**
     * 메일서버에 계정 등록
     */
    public void registerMailAccount(String internalEmail, String password) {
        String encodedPassword = HmailserverPasswordEncoder.encode(password);

        MailAccountDTO mailAccount = MailAccountDTO.builder()
                .accountaddress(internalEmail)
                .accountpassword(encodedPassword)
                .build();

        mailMapper.insertMailAccount(mailAccount);

        Integer accountid = mailAccount.getAccountid();

        if (accountid == null) {
            throw new RuntimeException("메일 계정 생성 실패: accountId를 가져올 수 없습니다.");
        }

        mailMapper.insertImapFolders(accountid);
    }

    /**
     * 메일 비밀번호 업데이트
     */
    public void updateMailPassword(String internalEmail, String password) {
        // 비밀번호 암호화
        String encodedPassword = HmailserverPasswordEncoder.encode(password);

        // DB 업데이트 실행
        int rowsUpdated = mailMapper.updateMailPassword(internalEmail, encodedPassword);

        // 업데이트된 행이 없으면 오류 처리 (예: 해당 email이 존재하지 않음)
        if (rowsUpdated == 0) {
            throw new RuntimeException("메일 비밀번호 업데이트 실패: 해당 email(" + internalEmail + ")이 존재하지 않습니다.");
        }
    }

    /**
     * 메일 존재 여부 체크
     */
    public boolean emailExists(String accountaddress) {
        return mailMapper.existsByEmail(accountaddress) > 0;
    }

}
