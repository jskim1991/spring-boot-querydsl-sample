package io.jay.service.model;

import com.querydsl.core.annotations.QueryProjection;

public record MemberResponse(long id, String name) {

    @QueryProjection
    public MemberResponse {
    }
}
