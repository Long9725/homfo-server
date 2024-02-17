package com.homfo.user.usecase;

import com.homfo.user.dto.UserDto;
import com.homfo.user.dto.UserMarketingAgreementDto;
import com.homfo.user.service.UserMarketingAgreementReadService;
import com.homfo.user.service.UserMarketingAgreementWriteService;
import com.homfo.user.service.UserReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MySqlGetUserInfoUsecase implements GetUserInfoUsecase {
    private final UserReadService userReadService;

    private final UserMarketingAgreementReadService userMarketingAgreementReadService;

    /**
     * 사용자 정보를 가져옵니다. 마케팅 정보를 포함합니다.
     *
     * COMMIT 된 최신 정보를 가져오는게 좋기 때문에 한 트랜잭션으로 묶지 않습니다.
     * */
    @Override
    public UserMarketingAgreementDto execute(long userId) {
        UserDto userDto = userReadService.findById(userId);

        return userMarketingAgreementReadService.getUserMarketingInfo(userDto);
    }
}