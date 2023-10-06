package io.jay.service.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.jay.service.model.MemberResponse;
import io.jay.service.model.QMemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DefaultTeamQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<MemberResponse> findMembersByTeamId(long teamId) {
        QMember memberTable = new QMember("member");

        return queryFactory
                .select(
                        new QMemberResponse(
                                memberTable.id,
                                memberTable.name
                        )
                )
                .from(memberTable)
                .where(
                        memberTable.team.id.eq(teamId)
                )
                .fetch();
    }


    public List<MemberResponse> searchMembersByTeamId(long teamId, String searchText) {
        QMember memberTable = new QMember("member");

        return queryFactory
                .select(
                        new QMemberResponse(
                                memberTable.id,
                                memberTable.name
                        )
                )
                .from(memberTable)
                .where(
                        memberTable.team.id.eq(teamId),
                        nameLike(searchText)
                )
                .fetch();
    }

    private BooleanExpression nameLike(String searchText) {
        if (!StringUtils.hasText(searchText)) {
            return null;
        }

        QMember memberTable = new QMember("member");
        return memberTable.name.containsIgnoreCase(searchText);
    }


    public Page<MemberResponse> members(Pageable pageable) {
        QMember memberTable = new QMember("member");

        List<MemberResponse> members = queryFactory
                .select(
                        new QMemberResponse(
                                memberTable.id,
                                memberTable.name
                        )
                )
                .from(memberTable)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(members, pageable, () -> countQuery().fetchOne());
    }

    private JPAQuery<Long> countQuery() {
        QMember memberTable = new QMember("member");
        return queryFactory
                .select(memberTable.count())
                .from(memberTable);
    }
}
