package com.hompo.usecase;

import com.hompo.auth.dto.JwtDto;
import com.hompo.auth.dto.JwtSecretDto;
import com.hompo.auth.infra.util.JwtUtil;
import com.hompo.auth.service.JwtWriteService;
import com.hompo.user.dto.UserDto;
import com.hompo.user.service.UserRefreshTokenWriteService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.hompo.user.command.RegisterCommand;
import com.hompo.user.service.UserWriteService;

@Service
public class RegisterUsecase {
    private final UserWriteService userWriteService;

    private final UserRefreshTokenWriteService userRefreshTokenWriteService;

    private final JwtSecretDto accessTokenInfo;

    private final JwtSecretDto refreshTokenInfo;

    public RegisterUsecase(
            UserWriteService userWriteService,
            UserRefreshTokenWriteService userRefreshTokenWriteService,
            @Qualifier("userAccessTokenInfo") JwtSecretDto accessTokenInfo,
            @Qualifier("userRefreshTokenInfo") JwtSecretDto refreshTokenInfo
    ) {
        this.userWriteService = userWriteService;
        this.userRefreshTokenWriteService = userRefreshTokenWriteService;
        this.accessTokenInfo = accessTokenInfo;
        this.refreshTokenInfo = refreshTokenInfo;
    }

    public JwtDto execute(RegisterCommand command) {
        UserDto userDto = userWriteService.register(command);
        String accessToken = JwtUtil.createToken(userDto.id(), accessTokenInfo);
        String refreshToken = userRefreshTokenWriteService.save(userDto.id(), refreshTokenInfo);

        return new JwtDto(accessToken, refreshToken);
    }
}
