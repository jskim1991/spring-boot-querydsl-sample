package io.jay.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.jay.service.model.MemberResponse;
import io.jay.service.model.TeamResponse;
import io.jay.service.repository.DefaultTeamQueryRepository;
import io.jay.service.repository.Member;
import io.jay.service.repository.Team;
import io.jay.service.repository.TeamRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;


@SpringBootApplication
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}

@Configuration
class QueryDslConfiguration {

    @PersistenceContext
    private EntityManager em;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(em);
    }
}


@Component
@RequiredArgsConstructor
class DataInitializer implements CommandLineRunner {

    private final TeamRepository repository;

    @Override
    @Transactional
    public void run(String... args) {
        Team firstTeam = new Team("First Team");
        firstTeam.addMember(new Member("Jay"));
        firstTeam.addMember(new Member("Steve"));
        firstTeam.addMember(new Member("Jun"));
        firstTeam.addMember(new Member("Joel"));

        Team secondTeam = new Team("Second Team");
        secondTeam.addMember(new Member("Ats"));
        secondTeam.addMember(new Member("Ken"));
        secondTeam.addMember(new Member("Yu"));

        repository.saveAll(List.of(firstTeam, secondTeam));
    }
}

@Controller
@ResponseBody
@RequiredArgsConstructor
class TeamController {

    private final DefaultTeamQueryRepository query;

    @GetMapping("/v1/teams/{teamId}/members")
    public List<MemberResponse> members(@PathVariable long teamId) {
        return query.findMembersByTeamId(teamId);
    }

    @GetMapping("/v2/teams/{teamId}/members")
    public List<MemberResponse> searchMembers(@PathVariable long teamId, @RequestParam(required = false) String searchText) {
        return query.searchMembersByTeamId(teamId, searchText);
    }

    @GetMapping("/v3/members")
    public Page<MemberResponse> paginatedMembers(Pageable pageable) {
        return query.members(pageable);
    }
}

@Controller
@ResponseBody
@RequiredArgsConstructor
class DashboardController {

    private final TeamRepository teamRepository;

    @GetMapping("/v1/dashboard")
    public List<TeamResponse> dashboard1() {
        List<Team> all = teamRepository.findAllUsingJoinFetch();
        return all.stream()
                .map(team -> new TeamResponse(team.getId(), team.getName(), team.getMembers().size(), team.getMilestones().size()))
                .toList();
    }

    @GetMapping("/v2/dashboard")
    public Page<TeamResponse> dashboard2(Pageable pageable) {
        Page<Team> all = teamRepository.findAll(pageable);
        var content = all.stream()
                .map(team -> new TeamResponse(team.getId(), team.getName(), team.getMembers().size(), team.getMilestones().size()))
                .toList();
        return new PageImpl<>(content, pageable, all.getTotalElements());
    }
}