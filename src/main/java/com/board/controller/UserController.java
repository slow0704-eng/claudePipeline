package com.board.controller;

import com.board.entity.Board;
import com.board.entity.User;
import com.board.service.BoardService;
import com.board.service.FollowService;
import com.board.service.UserService;
import com.board.util.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BoardService boardService;
    private final FollowService followService;

    @GetMapping("/{userId}")
    public String userProfile(@PathVariable Long userId, Model model) {
        User user = userService.findById(userId);
        User currentUser = AuthenticationUtils.getCurrentUser(userService);

        // Get user's posts
        List<Board> userPosts = boardService.getBoardsByUserId(userId);

        // Get follow stats
        long followerCount = followService.getFollowerCount(userId);
        long followingCount = followService.getFollowingCount(userId);

        // Check if current user is following this user
        boolean isFollowing = false;
        boolean isOwnProfile = false;

        if (currentUser != null) {
            isFollowing = followService.isFollowing(currentUser.getId(), userId);
            isOwnProfile = currentUser.getId().equals(userId);
        }

        model.addAttribute("profileUser", user);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userPosts", userPosts);
        model.addAttribute("postCount", userPosts.size());
        model.addAttribute("followerCount", followerCount);
        model.addAttribute("followingCount", followingCount);
        model.addAttribute("isFollowing", isFollowing);
        model.addAttribute("isOwnProfile", isOwnProfile);

        return "user/profile";
    }

    @GetMapping("/{userId}/followers")
    public String followers(@PathVariable Long userId, Model model) {
        User user = userService.findById(userId);
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        List<User> followers = followService.getFollowers(userId);

        model.addAttribute("profileUser", user);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("users", followers);
        model.addAttribute("listType", "followers");

        return "user/follow-list";
    }

    @GetMapping("/{userId}/following")
    public String following(@PathVariable Long userId, Model model) {
        User user = userService.findById(userId);
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        List<User> following = followService.getFollowing(userId);

        model.addAttribute("profileUser", user);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("users", following);
        model.addAttribute("listType", "following");

        return "user/follow-list";
    }
}
