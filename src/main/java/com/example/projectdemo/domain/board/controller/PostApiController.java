package com.example.projectdemo.domain.board.controller;

import com.example.projectdemo.domain.auth.jwt.JwtTokenUtil;
import com.example.projectdemo.domain.board.dto.PostsDTO;
import com.example.projectdemo.domain.board.service.PostsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
public class PostApiController {

    @Autowired
    private PostsService postsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Value("${board.image.upload-dir}")
    private String boardImageUploadDir;

    // === 게시글 관련 API ===

    // 게시글 작성, 저장 API
    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody PostsDTO postsDTO, HttpSession session, HttpServletRequest request) {
        //HTTP 요청의 속성 값(id)을 Integer 타입으로 캐스팅
        Integer empId = (Integer) request.getAttribute("id");

        if (empId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // 서비스를 통해 게시글 작성
        PostsDTO savedPost = postsService.createPost(empId, postsDTO);

        // 성공 응답 반환
        return ResponseEntity.ok(savedPost);
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable int id, HttpServletRequest request) {
        try {
            // 요청 속성에서 인증된 사용자 ID 가져오기 (필터에서 설정됨)
            Integer loginId = (Integer) request.getAttribute("id");

            if (loginId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "인증이 필요합니다."));
            }

            // 게시글 정보 조회
            PostsDTO post = postsService.getPostById(id);
            if (post == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "게시글을 찾을 수 없습니다."));
            }

            // 작성자 확인 - ID 비교
            if (!post.getEmpId().equals(loginId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("success", false, "message", "작성자만 게시글을 삭제할 수 있습니다."));
            }

            // 삭제 처리
            boolean result = postsService.deletePost(id);
            if (result) {
                return ResponseEntity.ok(Map.of("success", true, "message", "게시글이 성공적으로 삭제되었습니다."));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("success", false, "message", "게시글 삭제 중 오류가 발생했습니다."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "게시글 삭제 중 오류: " + e.getMessage()));
        }
    }

    //현재 로그인한 사용자의 ID와 사번(empNum)을 반환하는 API
    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        try {
            // 필터에서 설정한 id 속성 가져오기
            Integer userId = (Integer) request.getAttribute("id");

            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "인증이 필요합니다."));
            }

            // 필요한 사용자 정보만 반환
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", userId);
            userData.put("empNum", request.getAttribute("empNum"));

            return ResponseEntity.ok(userData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "사용자 정보 조회 중 오류가 발생했습니다."));
        }
    }

    //Summernote 에디터에서 업로드한 이미지를 서버에 저장하고, 저장된 이미지의 URL을 클라이언트에 응답으로 보내주는 API
    @PostMapping("/upload/image")
    public ResponseEntity<?> uploadSummernoteImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("파일이 비어있습니다.");
        }

        try {
            // 원본 파일명과 확장자 추출
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // 고유한 파일명 생성 (UUID 사용)
            String newFilename = UUID.randomUUID().toString() + fileExtension;

            // 년월일 폴더 구조 생성 (선택사항)
            String datePath = new java.text.SimpleDateFormat("yyyy/MM/dd").format(new java.util.Date());
            String savePath = boardImageUploadDir + "/" + datePath;

            // 디렉토리 생성
            File directory = new File(savePath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 파일 저장
            Path targetPath = Paths.get(savePath, newFilename);
            Files.copy(file.getInputStream(), targetPath);

            // 클라이언트에서 접근 가능한 URL 생성
            String fileUrl = "/uploaded-images/" + datePath + "/" + newFilename;

            // 응답 데이터 구성
            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("파일 업로드 중 오류가 발생했습니다.");
        }
    }


}
