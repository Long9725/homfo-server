package com.homfo.user.usecase;

/**
 * 사용자 계정 정보를 삭제합니다.
 * */
public interface DeleteAccountUsecase {
    /**
     * 사용자 계정 삭제와 Refresh token 삭제를 한 트랜잭션으로 묶어야 합니다.
     * */
    void deleteAccount(long userId);
}
