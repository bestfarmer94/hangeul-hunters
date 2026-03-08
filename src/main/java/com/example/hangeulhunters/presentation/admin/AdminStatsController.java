package com.example.hangeulhunters.presentation.admin;

import com.example.hangeulhunters.domain.user.repository.UserStatRow;
import com.example.hangeulhunters.domain.user.repository.UserStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminStatsController {

    private final UserStatsRepository userStatsRepository;

    @GetMapping("/users")
    public String getUserStats(Model model) {
        List<UserStatRow> stats = userStatsRepository.findUserStats();

        long totalRolePlay = stats.stream().mapToLong(UserStatRow::getRolePlayCount).sum();
        long totalReport = stats.stream().mapToLong(UserStatRow::getReportCount).sum();
        long totalAsk = stats.stream().mapToLong(UserStatRow::getAskCount).sum();
        String updatedAt = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        model.addAttribute("stats", stats);
        model.addAttribute("totalUsers", stats.size());
        model.addAttribute("totalRolePlay", totalRolePlay);
        model.addAttribute("totalReport", totalReport);
        model.addAttribute("totalAsk", totalAsk);
        model.addAttribute("updatedAt", updatedAt);

        return "admin/users";
    }
}
