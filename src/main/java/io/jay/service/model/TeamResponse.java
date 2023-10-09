package io.jay.service.model;

public record TeamResponse(
        long id,
        String name,
        long memberCount,
        long milestoneCount
) {
}
