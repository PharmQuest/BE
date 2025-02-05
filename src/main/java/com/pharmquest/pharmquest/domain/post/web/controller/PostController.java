package com.pharmquest.pharmquest.domain.post.web.controller;

import com.pharmquest.pharmquest.domain.post.converter.*;
import com.pharmquest.pharmquest.domain.mypage.data.PostScrap;
import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.data.enums.Country;
import com.pharmquest.pharmquest.domain.post.data.enums.PostCategory;
import com.pharmquest.pharmquest.domain.post.data.enums.ReportType;
import com.pharmquest.pharmquest.domain.post.data.mapping.*;
import com.pharmquest.pharmquest.domain.post.service.comment.CommentLikeService;
import com.pharmquest.pharmquest.domain.post.service.comment.CommentReportService;
import com.pharmquest.pharmquest.domain.post.service.post.PostCommandService;
import com.pharmquest.pharmquest.domain.post.service.comment.PostCommentService;
import com.pharmquest.pharmquest.domain.post.service.like.PostLikeService;
import com.pharmquest.pharmquest.domain.post.service.report.PostReportService;
import com.pharmquest.pharmquest.domain.post.service.scrap.PostScrapService;
import com.pharmquest.pharmquest.domain.post.web.dto.CommentRequestDTO;
import com.pharmquest.pharmquest.domain.post.web.dto.CommentResponseDTO;
import com.pharmquest.pharmquest.domain.post.web.dto.PostRequestDTO;
import com.pharmquest.pharmquest.domain.post.web.dto.PostResponseDTO;
import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.global.apiPayload.ApiResponse;
import com.pharmquest.pharmquest.domain.token.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
public class PostController {

    private final PostCommandService postCommandService;
    private final PostLikeService postLikeService;
    private final PostScrapService postScrapService;
    private final JwtUtil jwtUtil;
    private final PostCommentService postCommentService;
    private final PostReportService postReportService;
    private final CommentLikeService commentLikeService;
    private final CommentReportService commentReportService;

    @PostMapping(value = "/posts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "게시글 작성 API")
    public ApiResponse<PostResponseDTO.postResultDTO> createPost(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader,
            @RequestPart("request") @Valid PostRequestDTO.CreatePostDTO request,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        User user = jwtUtil.getUserFromHeader(authorizationHeader);

        Post post = postCommandService.registerPost(user.getId(), request, file);
        return ApiResponse.onSuccess(PostConverter.toPostResultDTO(post));
    }


    @DeleteMapping("/posts/{post_id}")
    @Operation(summary = "게시글 삭제 API")
    public ApiResponse<String> deletePost(
            @Parameter (hidden = true) @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long post_id
            ) {

        User user = jwtUtil.getUserFromHeader(authorizationHeader);

        postCommandService.deletePost(user.getId(),post_id);
        return ApiResponse.onSuccess("게시글이 삭제되었습니다.");
    }

    @PatchMapping(value = "/posts/{post_id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "게시글 수정 API")
    public ApiResponse<PostResponseDTO.postResultDTO> updatePost(
            @Parameter (hidden = true) @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long post_id,
            @RequestPart("request") @Valid PostRequestDTO.UpdatePostDTO request,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        User user = jwtUtil.getUserFromHeader(authorizationHeader);

         Post post = postCommandService.updatePost(user.getId(),post_id,request,file);
        return ApiResponse.onSuccess(PostConverter.toPostResultDTO(post));
    }
    @GetMapping("/posts/lists")
    @Operation(summary = "카테고리별 게시글 리스트 조회 API")
    public ApiResponse<PostResponseDTO.PostPreViewListDTO> getPostList(@Parameter (hidden = true) @RequestHeader(value = "Authorization",required = false) String authorizationHeader, @RequestParam(name = "category")PostCategory category, @RequestParam(name="page")Integer page){

        Long userId = null;
        if (authorizationHeader != null) {
            User user = jwtUtil.getUserFromHeader(authorizationHeader);
            userId = user.getId();
        }

        Page<Post> postList = postCommandService.getAllPosts(userId, category, page);

        return ApiResponse.onSuccess(PostConverter.postPreViewListDTO(postList));

    }

    @GetMapping("/posts/{post_id}")
    @Operation(summary = "게시글 상세조회 API")
    public ApiResponse<PostResponseDTO.PostDetailDTO> getPost
            (@Parameter (hidden = true) @RequestHeader("Authorization") String authorizationHeader,
             @PathVariable(name = "post_id")Long postId,
             @RequestParam(name="page")Integer page){
     
        User user = jwtUtil.getUserFromHeader(authorizationHeader);

        PostResponseDTO.PostDetailDTO postDetail = postCommandService.getPost(user.getId(), postId,page);

        return ApiResponse.onSuccess(postDetail);
    }

    @GetMapping("/search")
    @Operation(summary = "게시글 키워드로 검색 API")
    public ApiResponse<PostResponseDTO.PostPreViewListDTO> searchPost(@Parameter (hidden = true) @RequestHeader(value = "Authorization",required = false) String authorizationHeader,@RequestParam(name = "keyword")String keyword,@RequestParam(name = "country") Country country, @RequestParam(name = "category")PostCategory category, @RequestParam(name="page")Integer page){

        Long userId = null;
        if (authorizationHeader != null) {
            User user = jwtUtil.getUserFromHeader(authorizationHeader);
            userId = user.getId();
        }

        Page<Post> postList = postCommandService.searchPostsDynamically(userId, keyword, country, category, page);
        return ApiResponse.onSuccess(PostConverter.postPreViewListDTO(postList));
    }

    @PostMapping("/posts/{post_id}/likes")
    @Operation(summary = "게시글 좋아요 API")
    public ApiResponse<PostResponseDTO.CreatePostLikeResponseDTO> createPostLike(@Parameter (hidden = true) @RequestHeader("Authorization") String authorizationHeader, @PathVariable(name = "post_id")Long postId){

        User user = jwtUtil.getUserFromHeader(authorizationHeader);

        PostLike postLike = postLikeService.createPostLike(user.getId(), postId);
        return ApiResponse.onSuccess(PostLikeConverter.toPostLikeDTO(postLike));
    }

    @DeleteMapping("/posts/{post_id}/likes")
    @Operation(summary = "게시글 좋아요 취소 API")
    public ApiResponse<String> deletePostLike(@Parameter (hidden = true) @RequestHeader("Authorization") String authorizationHeader, @PathVariable(name = "post_id")Long postId){

        User user = jwtUtil.getUserFromHeader(authorizationHeader);

        postLikeService.deletePostLike(user.getId(), postId);
        return ApiResponse.onSuccess("좋아요가 삭제되었습니다");
    }

    @PostMapping("/posts/{post_id}/scraps")
    @Operation(summary = "게시글 스크랩 API")
    public ApiResponse<PostResponseDTO.CreatePostScrapResponseDTO> createPostScrap(@Parameter (hidden = true) @RequestHeader("Authorization") String authorizationHeader, @PathVariable(name = "post_id")Long postId){

        User user = jwtUtil.getUserFromHeader(authorizationHeader);

        PostScrap postScrap = postScrapService.createPostScrap(user.getId(), postId);
        return ApiResponse.onSuccess(PostScrapConverter.toPostScrapDTO(postScrap));
    }

    @DeleteMapping("/posts/scraps")
    @Operation(summary = "게시글 스크랩 취소 API")
    public ApiResponse<String> deletePostScraps(@Parameter (hidden = true) @RequestHeader("Authorization") String authorizationHeader,
                                                @RequestParam List<Long> postIds){

        User user = jwtUtil.getUserFromHeader(authorizationHeader);

        postScrapService.deletePostScrap(user.getId(), postIds);
        return ApiResponse.onSuccess("스크랩이 취소되었습니다");
    }

    @PostMapping("/posts/{post_id}/reports")
    @Operation(summary = "게시글 신고 API")
    public ApiResponse<PostResponseDTO.CreatePostReportResponseDTO> createPostLike(@Parameter (hidden = true) @RequestHeader("Authorization") String authorizationHeader, @PathVariable(name = "post_id")Long postId,  @RequestParam(name = "type") ReportType reportType){

        User user = jwtUtil.getUserFromHeader(authorizationHeader);

        PostReport postReport = postReportService.createReport(user.getId(), postId, reportType);
        return ApiResponse.onSuccess(PostReportConverter.toPostReportDTO(postReport));
    }

    @PostMapping("/posts/{post_id}/comments")
    @Operation(summary = "게시글 댓글, 답글 작성 API")
    public ApiResponse<CommentResponseDTO.commentResultDTO> createComment(
            @Parameter (hidden = true) @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable(name = "post_id")Long postId,
            @RequestParam(name="parentsId", required = false)Long parentsId,
            @RequestBody @Valid CommentRequestDTO.CreateCommentDTO requestDTO) {

        User user = jwtUtil.getUserFromHeader(authorizationHeader);

        Comment createdComment = postCommentService.addComment(user.getId(),postId, parentsId,requestDTO);
        return ApiResponse.onSuccess(PostCommentConverter.toCommentResultDTO(createdComment));
    }


    @PatchMapping("/comments/{comment_id}/update")
    @Operation(summary = "댓글 수정 API")
    public ApiResponse<CommentResponseDTO.commentResultDTO> updateComment(
            @Parameter (hidden = true) @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long comment_id,
            @RequestBody @Valid CommentRequestDTO.UpdateCommentDTO request) {

        User user = jwtUtil.getUserFromHeader(authorizationHeader);

       Comment comment = postCommentService.updateComment(user.getId(),comment_id,request);
        return ApiResponse.onSuccess(PostCommentConverter.toCommentResultDTO(comment));
    }

    @PatchMapping("/comments/delete")
    @Operation(summary = "댓글 삭제 API")
    public ApiResponse<String> deleteComment(
            @Parameter (hidden = true) @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam List<Long> commentIds) {

        User user = jwtUtil.getUserFromHeader(authorizationHeader);

        postCommentService.deleteComment(user.getId(),commentIds);
        return ApiResponse.onSuccess("댓글이 삭제되었습니다.");
    }

    @PostMapping("/comments/{comment_id}/likes")
    @Operation(summary = "댓글 좋아요 API")
    public ApiResponse<CommentResponseDTO.CreateCommentLikeResponseDTO> createCommentLike(
            @Parameter (hidden = true) @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable(name = "comment_id") Long commentId){

        User user = jwtUtil.getUserFromHeader(authorizationHeader);

        CommentLike commentLike = commentLikeService.createCommentLike(user.getId(), commentId);

        return ApiResponse.onSuccess(PostCommentConverter.toCommentLikeDTO(commentLike));
    }


    @DeleteMapping("/comments/{comment_id}/likes")
    @Operation(summary = "댓글 좋아요 취소 API")
    public ApiResponse<String> deleteCommentLike(
            @Parameter (hidden = true) @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable(name = "comment_id")Long commentId){

        User user = jwtUtil.getUserFromHeader(authorizationHeader);

        commentLikeService.deleteCommentLike(user.getId(), commentId);
        return ApiResponse.onSuccess("좋아요가 삭제되었습니다");
    }

    @PostMapping("/comments/{comment_id}/reports")
    @Operation(summary = "댓글 신고 API")
    public ApiResponse<CommentResponseDTO.CreateCommentReportResponseDTO> createCommentLike(
            @Parameter (hidden = true)
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable(name = "comment_id")Long commentId,
            @RequestParam(name = "type") ReportType reportType){

        User user = jwtUtil.getUserFromHeader(authorizationHeader);

        CommentReport commentReport = commentReportService.createReport(user.getId(), commentId, reportType);
        return ApiResponse.onSuccess(PostCommentConverter.toCommentReportDTO(commentReport));
    }
}
