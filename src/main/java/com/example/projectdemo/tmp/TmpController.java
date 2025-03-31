package com.example.projectdemo.tmp;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class TmpController {

    //임시토큰 사용법
    @GetMapping("tmp-page")
    public String groupwareMain(Model model) {
        model.addAttribute("token", TmpJwtUtil.getDevUser());

        return "tmp/tmp-page";
    }

    @GetMapping("/tmp-groupware")
    public String tmpPage(Model model, HttpServletResponse response) {

        // 개발용 환경에서만 작동하도록 체크 -> 함수에서 true 반환중~
        if (isDevelopmentEnvironment()) {
            String token = TmpJwtUtil.createDevToken();

            // 쿠키에 임의 JWT 추가
            Cookie jwtCookie = new Cookie("JWT_TOKEN", token);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(24 * 60 * 60); // 1일이랍니다~
            response.addCookie(jwtCookie);

            response.setHeader("Authorization", "Bearer " + token);
        }
        // 개발용 사용자 객체를 모델에 추가
        model.addAttribute("token", TmpJwtUtil.getDevUser());
        System.out.println("token = " + TmpJwtUtil.getDevUser());


        model.addAttribute("token", TmpJwtUtil.getDevUser());

        return "tmp/tmp-groupware";
    }

    //    임시 토큰용 코드
    @GetMapping("/dev/token")
    @ResponseBody
    public Map<String, String> getDevToken() {
        String tmpToken = TmpJwtUtil.createDevToken();
        Map<String, String> response = new HashMap<>();
        response.put("tmpToken", tmpToken);
        return response;
    }

    private boolean isDevelopmentEnvironment() {
        return true;
    }
}
