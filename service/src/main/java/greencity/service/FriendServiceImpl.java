package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.friends.UserFriendDto;
import greencity.dto.user.UserManagementDto;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;

@Service
@AllArgsConstructor
public class FriendServiceImpl implements FriendService{
    private final UserRepo userRepo;

    @Override
    public PageableDto<UserFriendDto> findAllFriendsOfUser(long userId, String name) {
        validateUserExistence(userId);
        return null;
    }

    @Override
    public PageableDto<UserManagementDto> findUserFriendsByUserId(Pageable page, long userId) {
        return null;
    }

    private void validateUserExistence(long userId) {
        if (!userRepo.existsById(userId)) {
            throw new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId);
        }
    }
}
