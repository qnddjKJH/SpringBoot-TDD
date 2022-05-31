package com.boongdev.atdd.membership.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {

    @InjectMocks
    private RatePointService ratePointService;
    // 포인트의 적립은 멤버쉽 서비스의 역할이 아니라고 판단

    @ParameterizedTest
    @MethodSource("points")
    public void _1만원_적립_100원(int inputPrice, int expect) throws Exception {
        // given
        final int price = inputPrice;

        // when
        final int result = ratePointService.calculateAmount(price);

        // then
        assertThat(result).isEqualTo(expect);
    }

    public static Stream<Arguments> points() {
        return Stream.of(
                Arguments.of(10000, 100),
                Arguments.of(20000, 200),
                Arguments.of(30000, 300)
        );
    }
}
