package com.boongdev.atdd.membership.api.controller;

import com.boongdev.atdd.membership.common.GlobalExceptionHandler;
import com.boongdev.atdd.membership.domain.membership.*;
import com.boongdev.atdd.membership.domain.membership.dto.MembershipAddResponse;
import com.boongdev.atdd.membership.domain.membership.dto.MembershipDetailResponse;
import com.boongdev.atdd.membership.domain.membership.dto.MembershipRequest;
import com.boongdev.atdd.membership.exception.MembershipErrorResult;
import com.boongdev.atdd.membership.exception.MembershipException;
import com.boongdev.atdd.membership.service.MembershipService;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest 자동으로 목객체 생성해주고 하지만 너무 느리다 상황봐서 사용하자.
@ExtendWith(MockitoExtension.class)
class MembershipApiControllerTest {

    @InjectMocks
    private MembershipApiController membershipApiController;

    @Mock
    private MembershipService membershipService;

    private MockMvc mockMvc;
    private Gson gson;

    @BeforeEach
    public void init() {
        gson = new Gson();
        mockMvc = MockMvcBuilders.standaloneSetup(membershipApiController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void mockMvcIsNotNull() throws Exception {
        assertThat(membershipApiController).isNotNull();
        assertThat(mockMvc).isNotNull();
    }

    @Test
    public void 멤버쉽_상세조회_사용자식별값_헤더없음() throws Exception {
        // given
        final String url = "/api/v1/memberships/-1";

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(url));

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버쉽_상세조회_멤버쉽_존재안함() throws Exception {
        // given
        final String url = "/api/v1/memberships/-1";
        doThrow(new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND))
                .when(membershipService)
                .getMembership(-1L, "12345");

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .header(MembershipConstant.USER_ID_HEADER, "12345")
        );

        // then
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    public void 멤버쉽_상세조회_성공() throws Exception {
        // given
        final String url = "/api/v1/memberships/-1";

        doReturn(
                MembershipDetailResponse.builder().build()
        ).when(membershipService).getMembership(-1L, "12345");

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .get(url)
                        .header(MembershipConstant.USER_ID_HEADER, "12345")
                        .param("membershipType", MembershipType.NAVER.name())
        );

        // then
        resultActions.andExpect(status().isOk());
    }
    
    @Test
    public void 멤버쉽_목록_조회실패_사용자식별값_헤더에없음() throws Exception {
        // given
        final String url = "/api/v1/memberships";
        
        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(url));

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버쉽_목록_조회성공() throws Exception {
        // given
        final String url = "/api/v1/memberships";
        doReturn(
                Arrays.asList(
                        MembershipDetailResponse.builder().build(),
                        MembershipDetailResponse.builder().build(),
                        MembershipDetailResponse.builder().build()
                )
        ).when(membershipService).getMembershipList("12345");

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .header(MembershipConstant.USER_ID_HEADER, "12345")
        );

        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    public void 멤버쉽_등록성공() throws Exception {
        // given
        String url = "/api/v1/memberships";
        MembershipAddResponse membershipResponse = MembershipAddResponse.builder()
                .id(-1L)
                .membershipType(MembershipType.KAKAO)
                .build();

        doReturn(membershipResponse)
                .when(membershipService)
                .addMembership("12345", 10000, MembershipType.KAKAO);

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(MembershipConstant.USER_ID_HEADER, "12345")
                        .content(gson.toJson(membershipRequest(10000, MembershipType.KAKAO)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isCreated());

        MembershipAddResponse returnMembership = gson.fromJson(
                resultActions.andReturn()
                        .getResponse()
                        .getContentAsString(StandardCharsets.UTF_8),
                MembershipAddResponse.class
        );

        assertThat(returnMembership.getMembershipType()).isEqualTo(MembershipType.KAKAO);
        assertThat(returnMembership.getId()).isNotNull();
    }

    @Test
    public void 멤버쉽_등록호출_실패_사용자식별값_헤더에없음() throws Exception {
        // given
        String url = "/api/v1/memberships";

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .content(gson.toJson(membershipRequest(10000, MembershipType.NAVER)))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버쉽_등록실패_MembershipService_Exception() throws Exception {
        // given
        String url = "/api/v1/memberships";

        doThrow(new MembershipException(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER))
                .when(membershipService)
                .addMembership("12345", 10000, MembershipType.KAKAO);

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(MembershipConstant.USER_ID_HEADER, "12345")
                        .content(gson.toJson(membershipRequest(10000, MembershipType.KAKAO)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isBadRequest());

    }

    // 멤버쉽 등록 실패 테스트 리팩토링 -> @ParameterizedTest
    // 파라미터를 받아서 중복된 3가지 테스트를 1개의 테스트로 만들고 파라미터만 다르게 하여
    // 중복을 제거한 테스트 코드로 리팩토링이 가능하다.
    @ParameterizedTest
    @MethodSource("invalidMembershipAddParameter")
    public void 멤버쉽_등록실패_잘못된파라미터(Integer point, MembershipType membershipType) throws Exception {
        // given
        String url = "/api/v1/memberships";

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                    .header(MembershipConstant.USER_ID_HEADER, "12345")
                    .content(gson.toJson(membershipRequest(point, membershipType)))
                    .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> invalidMembershipAddParameter() {
        return Stream.of(
                Arguments.of(null, MembershipType.KAKAO),
                Arguments.of(-1, MembershipType.KAKAO),
                Arguments.of(10000, null)
        );
    }



    @Test
    public void 멤버쉽_등록실패_포인트_Null() throws Exception {
        // given
        String url = "/api/v1/memberships";

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .header(MembershipConstant.USER_ID_HEADER, "12345")
                .content(gson.toJson(membershipRequest(null, MembershipType.KAKAO)))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버쉽_등록실패_포인트_음수() throws Exception {
        // given
        String url = "/api/v1/memberships";

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .header(MembershipConstant.USER_ID_HEADER, "12345")
                .content(gson.toJson(membershipRequest(-1, MembershipType.KAKAO)))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버쉽_등록실패_멤버쉽타입_Null() throws Exception {
        // given
        String url = "/api/v1/memberships";

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .header(MembershipConstant.USER_ID_HEADER, "12345")
                .content(gson.toJson(membershipRequest(10000, null)))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버쉽_삭제실패_사용자식별값_헤더없음() throws Exception {
        // given
        String url = "/api/v1/memberships/-1";

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete(url));

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버쉽_삭제성공() throws Exception {
        // given
        String url = "/api/v1/memberships/-1";

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url)
                        .header(MembershipConstant.USER_ID_HEADER, "12345")
        );

        // then
        // delete method 는 No Content 로 응답하는게 표준이므로
        // 상태가 No Content 일 때 테스트 성공이다.
        resultActions.andExpect(status().isNoContent());
    }

    private MembershipRequest membershipRequest(Integer point, MembershipType membershipType) {
        return MembershipRequest.builder()
                .point(point)
                .membershipType(membershipType)
                .build();
    }

}