package io.jay.service;

import io.jay.service.model.MemberResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MainApplicationTests {

    @Autowired
    WebTestClient wtc;

    @Nested
    class Members {
        @Test
        void returnsMembers() {
            var response = wtc.get()
                    .uri("/v1/teams/1/members")
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBodyList(MemberResponse.class)
                    .returnResult()
                    .getResponseBody();


            assertThat(response).hasSize(4);
        }
    }

    @Nested
    class SearchMembers {
        @Test
        void returnsMembers() {
            var response = wtc.get()
                    .uri("/v2/teams/1/members")
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBodyList(MemberResponse.class)
                    .returnResult()
                    .getResponseBody();


            assertThat(response).hasSize(4);
        }

        @Test
        void returnsMembersWithMatchingName() {
            var response = wtc.get()
                    .uri("/v2/teams/1/members?searchText=jay")
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBodyList(MemberResponse.class)
                    .returnResult()
                    .getResponseBody();


            assertThat(response).hasSize(1);
            assertThat(response.get(0).name()).isEqualTo("Jay");
        }
    }

    @Nested
    class PaginatedMembers {
        @Test
        void returnsMembersWithPagination() {
            wtc.get()
                    .uri("/v3/members?page=0&size=2")
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody()
                    .jsonPath("$.totalPages").isEqualTo(4)
                    .jsonPath("$.totalElements").isEqualTo(7)
                    .jsonPath("$.content.length()").isEqualTo(2);
        }
    }
}
