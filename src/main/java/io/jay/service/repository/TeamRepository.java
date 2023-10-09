package io.jay.service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {

    @Query("""
            select distinct t
                from Team t
                left join fetch t.members
                left join fetch t.milestones
            """)
    List<Team> findAllUsingJoinFetch();

    @EntityGraph(attributePaths = {
            "members", "milestones"
    })
    Page<Team> findAll(Pageable pageable);
}
